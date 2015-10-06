package org.fraunhofer.cese.funf_sensor.cache;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.google.inject.Inject;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roboguice.inject.ContextSingleton;

/**
 * Created by Lucas on 10/5/2015.
 */
@ContextSingleton
public class Cache {

    private static final String TAG = "Fraunhofer." + Cache.class.getSimpleName();

    // These variables will be moved to a config file
    private static final int MAX_MEM_ENTRIES = 0;
    private static final int MAX_DB_ENTRIES = 2;

    private static final int MEM_ENTRY_LIMIT = 10000;
    private static final int DB_ENTRY_LIMIT = 100000;

    private static final long DB_WRITE_INTERVAL = 2000;
    private static final long UPLOAD_INTERVAL = 5000;
    private static final boolean UPLOAD_WIFI_ONLY = true;

    private long last_db_write_attempt;
    private long last_upload_attempt;

    private Map<String, CacheEntry> memcache = new HashMap<>();
    private ProbeCacheOpenHelper databaseHelper = null;

    // Injected dependencies
    private final Context context;
    private final ConnectivityManager connManager;

    /**
     * Constructor. This should be injected rather than called directly.
     * <p/>
     * Once instantiated, the Cache will attempt to upload to the remote server if proper conditions are met.
     */
    @Inject
    public Cache(Context context, ConnectivityManager connManager) {
        this.context = context;
        this.connManager = connManager;

        last_db_write_attempt = System.currentTimeMillis();
        last_upload_attempt = 0;

        if (isTimeToUpload())
            uploadData();
    }

    /**
     * Adds an entry to the cache. This will also trigger DB writes or remote uploads if the proper conditions are met.
     *
     * @param entry the entry to save to the cache.
     */
    public void add(CacheEntry entry) {
        if (entry == null)
            return;

        memcache.put(entry.getId(), entry);
        if (memcache.size() > MAX_MEM_ENTRIES && System.currentTimeMillis() - last_db_write_attempt > DB_WRITE_INTERVAL) {
            flush();
        }
    }

    /**
     * This method is called when the DatabaseWriteAsyncTask successfully saves to the SQLLite database.
     * This method removes saved entries from the memcache and triggers an upload if conditions are correct.
     *
     * @param savedIds a collection of saved entry IDs.
     */
    protected void doPostDatabaseWrite(Collection<String> savedIds) {
        // TODO: Need to do some sanity checking. If DB writing persistently fails, we need to drop some items from memory.
        if (savedIds == null || savedIds.isEmpty())
            return;

        Log.d(TAG, "{doPostDatabaseWrite} entries saved to database: " + savedIds.size());
        // Remove ids written to DB from memory
        memcache.keySet().removeAll(savedIds);

        // Trigger an upload if the conditions are met.
        if (isTimeToUpload())
            uploadData();
    }

    /**
     * Method which determines if the appropriate conditions are met to trigger a remote upload.
     * The conditions are determined by static class variables, but will eventually be configurable.
     *
     * @return <code>true</code> if upload conditions are met, <code>false</code> otherwise.
     */
    private boolean isTimeToUpload() {
        boolean result = getHelper().getDao().countOf() > MAX_DB_ENTRIES;
        result &= System.currentTimeMillis() - last_upload_attempt > UPLOAD_INTERVAL;

        if (UPLOAD_WIFI_ONLY) {
            result &= connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
        } else {
            NetworkInfo netInfo = connManager.getActiveNetworkInfo();
            result &= netInfo != null && netInfo.isConnected();
        }

        return result;
    }

    private void uploadData() {
        last_upload_attempt = System.currentTimeMillis();
        new RemoteUploadAsyncTask(this).execute();
    }

    /**
     * This method should not be called by clients.
     * <p/>
     * Examines to the upload result and cleans the database accordingly.
     *
     * @param uploadResult
     */
    protected synchronized void doPostUpload(UploadResult uploadResult) {
        // TODO: Need some sanity checking. If the upload fails and the DB is getting huge, we need to compact it.

        if (uploadResult == null)
            return;

        if (!uploadResult.isUploadAttempted()) {
            Log.i(TAG, "{doPostUpload} Upload aborted: no entries in database cache.");
            return;
        }

        if (uploadResult.getException() != null) {
            Log.i(TAG, "{doPostUpload} Uploading entries failed due to exception. No entries will be removed from cache.", uploadResult.getException());
            return;
        }

        if (uploadResult.getSaveResult() == null) {
            Log.e(TAG, "{doPostUpload} saveResult is null.");
            return;
        }

        new AsyncTask<List<String>, Void, Integer>() {
            @Override
            protected Integer doInBackground(List<String>... lists) {
                if (lists == null)
                    return 0;

                Log.d(TAG, "Removing entries from database.");
                int result = 0;
                for (List<String> ids : lists) {
                    if (ids != null && !ids.isEmpty()) {
                        result += getHelper().getDao().deleteIds(ids);
                    }
                }
                return result;
            }

            @Override
            protected void onPostExecute(Integer numEntriesRemoved) {
                Log.d(TAG, "cached entries removed: " + numEntriesRemoved);
            }
        }.execute(uploadResult.getSaveResult().getSaved(), uploadResult.getSaveResult().getAlreadyExists());
    }

    /**
     * Flush cache entries to the database for persistent storage.
     */
    public void flush() {
        last_db_write_attempt = System.currentTimeMillis();
//        dbWriteProvider.get().execute(memcache);
        new DatabaseWriteAsyncTask(this).execute(memcache);
//
        //
    }

    /**
     * Closes the Cache, which has the effect of flushing pending entries to the database.
     * <p/>
     * Should be called when the app is destroyed, or other events when the cache is no longer needed.
     */
    public void close() {
        flush();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    /**
     * Gets the ORMLite database helper used in the cache.
     * <p/>
     * Only visible for purposes of callbacks from background threads.
     *
     * @return
     */
    protected ProbeCacheOpenHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(context, ProbeCacheOpenHelper.class);
        }
        return databaseHelper;
    }
}
