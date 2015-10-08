package org.fraunhofer.cese.funf_sensor.cache;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.inject.Inject;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

import org.fraunhofer.cese.funf_sensor.backend.models.probeDataSetApi.ProbeDataSetApi;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import roboguice.inject.ContextSingleton;

/**
 * Implementation of a two-stage cache for ProbeEntry data. The cache first stores objects in memory, and flushes them to a backing
 * database storage when specified conditions are met. Once the database size reaches a specified limit, the cache attempts to remotely
 * upload entries to the remote data store.
 * <p/>
 * Internally, the cache has a fail-safe to make sure that memory and disk space are not being overly consumed.
 * The cache will automatically purge its oldest entries if specified memory or disk limits are exceeded, which should only
 * happen if remote upload fails consistently.
 * <p/>
 * This class is a Singleton.
 *
 * @author Lucas
 */
@ContextSingleton
public class Cache {

    private static final String TAG = "Fraunhofer." + Cache.class.getSimpleName();

    /**
     * Timestamp (in millis) of the last attempted write to the database.
     */
    private long last_db_write_attempt;

    /**
     * Timestamp (in millis) of the last attempted remote upload.
     */
    private long last_upload_attempt;

    /**
     * In-memory representation of the cache. Entries are held here prior to being written to the persistent database.
     */
    private Map<String, CacheEntry> memcache = Collections.synchronizedMap(new LinkedHashMap<String, CacheEntry>());

    /**
     * Main object for accessing the SQLite database.
     * <p/>
     * See <a href="http://ormlite.com/javadoc/ormlite-core/doc-files/ormlite_4.html#Use-With-Android">ORMLite documentation: Using with Android</a>
     *
     * @see OrmLiteSqliteOpenHelper
     */
    private DatabaseOpenHelper databaseHelper = null;

    /**
     * The application context
     */
    private final Context context;

    /**
     * The ConnectivityManager system service, which is used to determine wifi state for uploading.
     */
    private final ConnectivityManager connManager;

    /**
     * Configuration settings the govern cache behavior.
     */
    private final CacheConfig config;

    /**
     * Factory for creating asynchronous tasks to access the database
     */
    private final DatabaseAsyncTaskFactory dbTaskFactory;

    /**
     * Factoring for creating asynchronous tasks to upload data
     */
    private final RemoteUploadAsyncTaskFactory uploadTaskFactory;

    /**
     * The app engine api for remote upload.
     */
    private final ProbeDataSetApi appEngineApi;

    @Inject
    public Cache(Context context,
                 ConnectivityManager connManager,
                 CacheConfig config,
                 DatabaseAsyncTaskFactory dbWriteTaskFactory,
                 RemoteUploadAsyncTaskFactory uploadTaskFactory,
                 ProbeDataSetApi appEngineApi) {
        this.context = context;
        this.connManager = connManager;
        this.config = config;
        this.dbTaskFactory = dbWriteTaskFactory;
        this.uploadTaskFactory = uploadTaskFactory;
        this.appEngineApi = appEngineApi;

        last_db_write_attempt = System.currentTimeMillis();
        last_upload_attempt = 0;

        uploadIfNeeded();
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
        if (memcache.size() > config.getMaxMemEntries() && System.currentTimeMillis() - last_db_write_attempt > config.getDbWriteInterval()) {
            flush();
        }
    }

    /**
     * Flush any entries to the database in memory.
     */
    public void flush() {
        last_db_write_attempt = System.currentTimeMillis();
        //noinspection unchecked
        dbTaskFactory.createWriteTask(this).execute(memcache);
    }

    /**
     * This method is called when the DatabaseWriteAsyncTask successfully saves to the SQLLite database.
     * This method removes saved entries from the memcache and triggers an upload if conditions are correct.
     *
     * @param result object containing information on successfully saved entries (if any) and errors that occured
     */
    void doPostDatabaseWrite(DatabaseWriteResult result) {

        // 1. Remove ids written to DB from memory
        if (result.getSavedEntries() != null && !result.getSavedEntries().isEmpty()) {
            Log.d(TAG, "{doPostDatabaseWrite} entries saved to database: " + result.getSavedEntries().size() + ", new database size: " + result.getDatabaseSize());
            memcache.keySet().removeAll(result.getSavedEntries());
        }

        // 2. Do some sanity checking. If DB writing persistently fails, we may need to drop some items from memory.
        //noinspection ThrowableResultOfMethodCallIgnored
        if (result.getError() != null) {
            Log.e(TAG, "{doPostDatabaseWrite} Database write failed.", result.getError());
            if (memcache.size() > config.getMemForcedCleanupLimit()) {
                Log.w(TAG, "{doPostDatabaseWrite} Too many cache entries in memory. Purging oldest entries. LIMIT: "
                        + config.getMemForcedCleanupLimit() + ", memcache size: " + memcache.size());

                // Remove the oldest entries so that config.getMemForcedCleanupLimit() / 2 entries remain
                Iterator<String> iterator = memcache.keySet().iterator();
                while (iterator.hasNext() && memcache.size() > config.getMemForcedCleanupLimit() / 2) {
                    iterator.next();
                    iterator.remove();
                }
                Log.w(TAG, "{doPostDatabaseWrite} New memcache size: " + memcache.size());
            }
        }

        // 3. Request an upload if the conditions are met.
        uploadIfNeeded();
    }

    /**
     * This method checks if the conditions are met to trigger a remote upload, and then starts an asynchronous task to perform
     * the upload if so
     */
    private void uploadIfNeeded() {
        // 1. Check preconditions
        if (appEngineApi == null) {
            Log.w(TAG, "{uploadIfNeeded} No remote app engine API for uploading.");
            return;
        }

        if (getHelper() == null) {
            Log.w(TAG, "{uploadIfNeeded} No helper found");
            return;
        }

        if (getHelper().getDao() == null) {
            Log.w(TAG, "{uploadIfNeeded} getHelper().getDao() is null");
            return;
        }

        // 2. Determine if an upload is needed based on configuration and number of database entries
        boolean shouldUpload;
        try {
            long numEntries = getHelper().getDao().countOf();
            shouldUpload = numEntries > config.getMaxDbEntries();
            shouldUpload &= System.currentTimeMillis() - last_upload_attempt > config.getUploadInterval();

            if (config.isUploadWifiOnly()) {
                shouldUpload &= connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
            } else {
                NetworkInfo netInfo = connManager.getActiveNetworkInfo();
                shouldUpload &= netInfo != null && netInfo.isConnected();
            }
        } catch (Exception e) {
            Log.e(TAG, "{uploadIfNeeded}  Unable to get count of database entries.", e);
            return;
        }

        // 3. Upload if needed
        if (shouldUpload) {
            last_upload_attempt = System.currentTimeMillis();
            uploadTaskFactory.createRemoteUploadTask(this, appEngineApi).execute();
        }
    }

    /**
     * This method should not be called by clients.
     * <p/>
     * Examines the upload result and cleans the database accordingly.
     *
     * @param uploadResult the upload result passed from the remote upload task
     */
    void doPostUpload(RemoteUploadResult uploadResult) {
        // TODO: Need some sanity checking. If the upload fails and the DB is getting huge, we need to compact it.

        if (uploadResult == null)
            return;

        if (!uploadResult.isUploadAttempted()) {
            Log.i(TAG, "{doPostUpload} Upload aborted: no entries in database cache.");
            return;
        }

        if (uploadResult.getException() != null) {
            Log.w(TAG, "{doPostUpload} Uploading entries failed due to exception.", uploadResult.getException());
            Log.i(TAG, "{doPostUpload} Running task to determine if database is still within size limits");
            dbTaskFactory.createCleanupTask(this, config.getDbForcedCleanupLimit()).execute();
            return;
        }

        if (uploadResult.getSaveResult() == null) {
            Log.w(TAG, "{doPostUpload} saveResult is null.");
            return;
        }

        //noinspection unchecked
        dbTaskFactory.createRemoveTask(this)
                .execute(uploadResult.getSaveResult().getSaved(), uploadResult.getSaveResult().getAlreadyExists());
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
     * @return the helper to access the database
     */
    DatabaseOpenHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(context, DatabaseOpenHelper.class);
        }
        return databaseHelper;
    }
}
