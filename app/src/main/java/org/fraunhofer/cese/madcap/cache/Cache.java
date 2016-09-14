package org.fraunhofer.cese.madcap.cache;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.opencsv.CSVWriter;

import org.fraunhofer.cese.madcap.backend.probeEndpoint.ProbeEndpoint;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

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

    private List<UploadStatusListener> uploadStatusListeners = new ArrayList<>();

    /**
     * Background task holder for the remote upload task. Stored to query for uploads in progress.
     */
    private AsyncTask<Void, Integer, RemoteUploadResult> uploadTask;

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
    private DatabaseOpenHelper databaseHelper;

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
    private final ProbeEndpoint appEngineApi;

    @Inject
    public Cache(Context context,
                 ConnectivityManager connManager,
                 CacheConfig config,
                 DatabaseAsyncTaskFactory dbWriteTaskFactory,
                 RemoteUploadAsyncTaskFactory uploadTaskFactory,
                 ProbeEndpoint appEngineApi) {
        this.context = context;
        this.connManager = connManager;
        this.config = config;
        this.dbTaskFactory = dbWriteTaskFactory;
        this.uploadTaskFactory = uploadTaskFactory;
        this.appEngineApi = appEngineApi;

        last_db_write_attempt = System.currentTimeMillis();
        last_upload_attempt = 0;

        if (checkUploadConditions(UploadStrategy.NORMAL) == UPLOAD_READY)
            upload();
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
            flush(UploadStrategy.NORMAL);
        }
    }

    /**
     * Flush any entries to the database in memory. They will be uploaded according to the upload strategy.
     *
     * @param uploadStrategy The upload strategy to use
     */
    public void flush(UploadStrategy uploadStrategy) {
        last_db_write_attempt = System.currentTimeMillis();
        //noinspection unchecked
        dbTaskFactory.createWriteTask(context, this, uploadStrategy).execute(ImmutableMap.copyOf(memcache));
    }

    /**
     * This method is called when the DatabaseWriteAsyncTask successfully saves to the SQLLite database.
     * This method removes saved entries from the memcache and triggers an upload if conditions are correct.
     *
     * @param result         object containing information on successfully saved entries (if any) and errors that occured
     * @param uploadStrategy the upload strategy to use
     */
    void doPostDatabaseWrite(DatabaseWriteResult result, UploadStrategy uploadStrategy) {

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

        // 3. Do upload if conditions are met.
        if (checkUploadConditions(uploadStrategy) == UPLOAD_READY)
            upload();
    }


    /**
     * Returns the total number of cached entries.
     *
     * @return the total number of cached entries, or -1 if the number cannot be determined (i.e., there is an error reading the database cache)
     */
    public long getSize() {
        if (getHelper() == null || getHelper().getDao() == null)
            return -1;
        try {
            return getHelper().getDao().countOf() + memcache.size();
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Add a listener for upload events. The listener will be provided with an {@link RemoteUploadResult}. Listeners will also be notified
     * when the cache is shutting down.
     *
     * @param listener the listener to add
     */
    public void addUploadListener(UploadStatusListener listener) {
        if (uploadStatusListeners == null)
            uploadStatusListeners = new ArrayList<>();
        uploadStatusListeners.add(listener);
    }

    /**
     * Removes the specified listener from upload events.
     *
     * @param listener the listener to remove
     * @return {@code true} if this {@code listener} was removed from the list of listeners, {@code false} otherwise.
     */
    public boolean removeUploadListener(UploadStatusListener listener) {
        return uploadStatusListeners != null && uploadStatusListeners.remove(listener);
    }

    /**
     * An internal error occurred (such as the database could not be read) and no upload attempt will occur.
     */
    public static final int INTERNAL_ERROR = 1;

    /**
     * The required internet connection (i.e. wifi vs data) is not available to support an upload.
     */
    public static final int NO_INTERNET_CONNECTION = 1 << 1;

    /**
     * No upload will be attempted because one has been attempted too recently.
     */
    public static final int UPLOAD_INTERVAL_NOT_MET = 1 << 2;

    /**
     * No upload will be attempted because there are not enough entries in the database.
     */
    public static final int DATABASE_LIMIT_NOT_MET = 1 << 3;

    /**
     * All preconditions are met for an upload.
     */
    public static final int UPLOAD_READY = 0;

    /**
     * No upload will be attempted because an upload is already in progress.
     */
    public static final int UPLOAD_ALREADY_IN_PROGRESS = 1 << 5;

    /**
     * Number of db entries before upload in "immediate" configuration
     */
    private static final int IMMEDIATE_MAX_DB_ENTRIES = 1;

    /**
     * Wait interval in millis between successfive upload attempts in "immediate" configuration
     */
    private static final int IMMEDIATE_UPLOAD_INTERVAL = 5000;
    /**
     * This method checks if the conditions are met to trigger a remote upload, and then starts an asynchronous task to perform
     * the upload if so
     *
     * @param strategy the upload strategy to use
     */
    public int checkUploadConditions(UploadStrategy strategy) {
        // 1. Check preconditions
        if (appEngineApi == null) {
            Log.w(TAG, "{uploadIfNeeded} No remote app engine API for uploading.");
            return INTERNAL_ERROR;
        }

        if (getHelper() == null) {
            Log.w(TAG, "{uploadIfNeeded} No helper found");
            return INTERNAL_ERROR;
        }

        if (getHelper().getDao() == null) {
            Log.w(TAG, "{uploadIfNeeded} getHelper().getDao() is null");
            return INTERNAL_ERROR;
        }

        // 2. Check if an upload is already in progress
        if (uploadTask != null) {
            return UPLOAD_ALREADY_IN_PROGRESS;
        }

        // 3. Determine if an upload is ready based on parameters
        long maxDbEntries;
        long uploadInterval;
        boolean wifiOnly = config.isUploadWifiOnly();


        if (strategy == UploadStrategy.IMMEDIATE) {
            maxDbEntries = IMMEDIATE_MAX_DB_ENTRIES;
            uploadInterval = IMMEDIATE_UPLOAD_INTERVAL;
        } else {
            maxDbEntries = config.getMaxDbEntries();
            uploadInterval = config.getUploadInterval();
        }

        int status = UPLOAD_READY;
        try {
            long numEntries = getHelper().getDao().countOf();
            if (strategy == UploadStrategy.IMMEDIATE)
                numEntries += memcache.size();

            if (numEntries < maxDbEntries) {
                status |= DATABASE_LIMIT_NOT_MET;
            }

            if (System.currentTimeMillis() - last_upload_attempt <= uploadInterval) {
                status |= UPLOAD_INTERVAL_NOT_MET;
            }
            if (wifiOnly && !connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
                status |= NO_INTERNET_CONNECTION;
            } else if (connManager.getActiveNetworkInfo() == null || !connManager.getActiveNetworkInfo().isConnected()) {
                status |= NO_INTERNET_CONNECTION;
            }
        } catch (Exception e) {
            Log.e(TAG, "{uploadIfNeeded}  Unable to get count of database entries.", e);
            status |= INTERNAL_ERROR;
        }
        return status;
    }

    /**
     * Starts an asynchronous task to perform the upload
     */
    private void upload() {
        last_upload_attempt = System.currentTimeMillis();

        if (config.getWriteToFile()) {
            try {
                writeToFile();
            } catch (IOException e) {
                Log.e(TAG, "Error writing to CSV file", e);
            }
        }
        uploadTask = uploadTaskFactory.createRemoteUploadTask(context, this, appEngineApi, uploadStatusListeners).execute();
    }

    private void writeToFile() throws IOException {
        String state = Environment.getExternalStorageState();
        if (!Environment.MEDIA_MOUNTED.equals(state)) {
            Log.e(TAG, "External media not mounted for read/write");
            return;
        }

        File dir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "probeData");
        Log.d(TAG, "CSV directory " + dir.getAbsolutePath() + " exists: " + dir.exists());
        if (!dir.exists() && !dir.mkdirs()) {
            Log.e(TAG, "Probe data directory not created");
            return;
        }

        List<String[]> toWrite = Lists.transform(getHelper().getDao().queryForAll(), new Function<CacheEntry, String[]>() {
            @Nullable
            @Override
            public String[] apply(CacheEntry cacheEntry) {
                return new String[]{cacheEntry.getId(), cacheEntry.getUserID(), cacheEntry.getTimestamp().toString(), cacheEntry.getProbeType(), cacheEntry.getSensorData()};
            }
        });

        File f = new File(dir, "probeData.csv");
        if (!f.exists() && f.createNewFile()) {
            CSVWriter writer = new CSVWriter(new FileWriter(f, true));
            Log.d(TAG, "Writing CSV file to:" + f.getAbsolutePath());
            writer.writeAll(toWrite);
            writer.flush();
            writer.close();
            Log.d(TAG, "CSV write completed");
        }
    }


    /**
     * This method should not be called by clients.
     * <p/>
     * Examines the upload result and cleans the database accordingly. Also notifies listeners with the upload result.
     *
     * @param uploadResult the upload result passed from the remote upload task
     */
    void doPostUpload(RemoteUploadResult uploadResult) {
        uploadTask = null;
        if (uploadStatusListeners != null && !uploadStatusListeners.isEmpty()) {
            for (UploadStatusListener listener : uploadStatusListeners) {
                listener.uploadFinished(uploadResult);
            }
        }
        if (uploadResult == null)
            return;

        if (!uploadResult.isUploadAttempted()) {
            Log.i(TAG, "{doPostUpload} Upload aborted: no entries were sent to be uploaded.");
            return;
        }

        if (uploadResult.getException() != null) {
            Log.w(TAG, "{doPostUpload} Uploading entries failed: " + uploadResult.getException().getMessage());
            dbTaskFactory.createCleanupTask(context, this, config.getDbForcedCleanupLimit()).execute();
        }
    }


    /**
     * Closes the Cache, which has the effect of flushing pending entries to the database.
     * <p/>
     * Should be called when the app is destroyed, or other events when the cache is no longer needed.
     */
    public void close() {
        flush(UploadStrategy.NORMAL);
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }

        if (uploadStatusListeners != null) {
            for (UploadStatusListener listener : uploadStatusListeners) {
                listener.cacheClosing();
            }
        }
    }

    /**
     * Gets the ORMLite database helper used in the cache.
     * <p/>
     * Only visible for purposes of callbacks from background threads.
     *
     * @return the helper to access the database
     */
    private DatabaseOpenHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(context, DatabaseOpenHelper.class);
        }
        return databaseHelper;
    }

    public enum UploadStrategy {
        NORMAL, IMMEDIATE
    }
}
