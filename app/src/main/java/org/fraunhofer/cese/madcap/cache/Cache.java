package org.fraunhofer.cese.madcap.cache;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;

import org.fraunhofer.cese.madcap.R;
import org.fraunhofer.cese.madcap.util.EndpointApiBuilder;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;


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
@SuppressWarnings("ClassNamingConvention")
@Singleton
public class Cache {

    private long currentDbSize;

    /**
     * Background task holder for the remote upload task. Stored to query for uploads in progress.
     */
    @Nullable
    private AsyncTask<Void, Integer, RemoteUploadResult> uploadTask;

    /**
     * Timestamp (in millis) of the last attempted write to the database.
     */
    private long lastDbWriteAttempt;

    /**
     * Timestamp (in millis) of the last attempted remote upload.
     */
    private long lastUploadAttempt;

    /**
     * In-memory representation of the cache. Entries are held here prior to being written to the persistent database.
     */
    private final Map<String, CacheEntry> memcache;

    /**
     * Main object for accessing the SQLite database.
     * <p/>
     * See <a href="http://ormlite.com/javadoc/ormlite-core/doc-files/ormlite_4.html#Use-With-Android">ORMLite documentation: Using with Android</a>
     *
     * @see OrmLiteSqliteOpenHelper
     */
    @Nullable
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
    private final EndpointApiBuilder endpointApiBuilder;

    private final FirebaseRemoteConfig mFirebaseRemoteConfig;

    private final SharedPreferences prefs;

    @Inject
    public Cache(Context context,
                 ConnectivityManager connManager,
                 DatabaseAsyncTaskFactory dbWriteTaskFactory,
                 RemoteUploadAsyncTaskFactory uploadTaskFactory,
                 EndpointApiBuilder endpointApiBuilder,
                 FirebaseRemoteConfig firebaseRemoteConfig,
                 SharedPreferences sharedPreferences) {
        this.context = context;
        this.connManager = connManager;
        dbTaskFactory = dbWriteTaskFactory;
        this.uploadTaskFactory = uploadTaskFactory;
        this.endpointApiBuilder = endpointApiBuilder;

        lastDbWriteAttempt = System.currentTimeMillis();
        lastUploadAttempt = 0L;

        mFirebaseRemoteConfig = firebaseRemoteConfig;
        prefs = sharedPreferences;

        //noinspection NumericCastThatLosesPrecision
        memcache = Collections.synchronizedMap(new LinkedHashMap<String, CacheEntry>((int) mFirebaseRemoteConfig.getLong(context.getString(R.string.MAX_MEM_ENTRIES_KEY))));

        EventBus.getDefault().register(this);
        currentDbSize = getHelper().getDao().countOf();
        if (checkUploadConditions(UploadStrategy.NORMAL) == UPLOAD_READY) {
            upload();
        }
    }

    /**
     * Adds an entry to the cache. This will also trigger DB writes or remote uploads if the proper conditions are met.
     *
     * @param entry the entry to save to the cache.
     */
    @SuppressWarnings({"InstanceMethodNamingConvention", "NonBooleanMethodNameMayNotStartWithQuestion"})
    public void add(CacheEntry entry) {
        if (entry == null) {
            return;
        }

        memcache.put(entry.getId(), entry);
        if (((long) memcache.size() > mFirebaseRemoteConfig.getLong(context.getString(R.string.MAX_MEM_ENTRIES_KEY))) && ((System.currentTimeMillis() - lastDbWriteAttempt) > mFirebaseRemoteConfig.getLong(context.getString(R.string.DB_WRITE_INTERVAL_KEY)))) {
            flush(UploadStrategy.NORMAL);
        }

        EventBus.getDefault().post(new CacheCountUpdate(currentDbSize + (long) memcache.size()));
    }

    @SuppressWarnings("PublicInnerClass")
    public static class CacheCountUpdate {
        private final long count;

        CacheCountUpdate(long count) {
            this.count = count;
        }

        public long getCount() {
            return count;
        }
    }

    /**
     * Flush any entries to the database in memory. They will be uploaded according to the upload strategy.
     *
     * @param uploadStrategy The upload strategy to use
     */
    public void flush(UploadStrategy uploadStrategy) {
        Timber.d("Cache now flushing.");
        lastDbWriteAttempt = System.currentTimeMillis();

        AsyncTask<Map<String, CacheEntry>, Void, DatabaseWriteResult> task = dbTaskFactory.createWriteTask(context, uploadStrategy);

        //noinspection unchecked
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, ImmutableMap.copyOf(memcache));
        Timber.d(task.getStatus().toString());

    }

    /**
     * This method is called when the DatabaseWriteAsyncTask successfully saves to the SQLLite database.
     * This method removes saved entries from the memcache and triggers an upload if conditions are correct.
     *
     * @param result object containing information on successfully saved entries (if any) and errors that occured
     */
    @Subscribe
    void doPostDatabaseWrite(DatabaseWriteResult result) {

        currentDbSize = getHelper().getDao().countOf();

        // 1. Remove ids written to DB from memory
        if ((result.getSavedEntries() != null) && !result.getSavedEntries().isEmpty()) {
            Timber.d("{doPostDatabaseWrite} entries saved to database: " + result.getSavedEntries().size() + ", new database size: " + result.getDatabaseSize());
            memcache.keySet().removeAll(result.getSavedEntries());
        }

        // 2. Do some sanity checking. If DB writing persistently fails, we may need to drop some items from memory.
        //noinspection ThrowableResultOfMethodCallIgnored
        if (result.getError() != null) {
            Timber.e("{doPostDatabaseWrite} Database write failed.", result.getError());
            long memLimit = mFirebaseRemoteConfig.getLong(context.getString(R.string.MEM_FORCED_CLEANUP_LIMIT_KEY));
            if ((long) memcache.size() > memLimit) {
                Timber.w("{doPostDatabaseWrite} Too many cache entries in memory. Purging oldest entries. LIMIT: "
                        + memLimit + ", memcache size: " + memcache.size());

                // Remove the oldest entries so that config.getMemForcedCleanupLimit() / 2 entries remain
                Iterator<String> iterator = memcache.keySet().iterator();
                while (iterator.hasNext() && ((long) memcache.size() > (memLimit / 2L))) {
                    iterator.next();
                    iterator.remove();
                }
                Timber.w("{doPostDatabaseWrite} New memcache size: " + memcache.size());
            }
        }

        // 3. Do upload if conditions are met.
        if (checkUploadConditions(result.getUploadStrategy()) == UPLOAD_READY) {
            upload();
        }
    }


    /**
     * Returns the total number of cached entries.
     *
     * @return the total number of cached entries, or -1 if the number cannot be determined (i.e., there is an error reading the database cache)
     */
    public long getSize() {
        if ((getHelper() == null) || (getHelper().getDao() == null)) {
            return -1L;
        }
        try {
            return getHelper().getDao().countOf() + (long) memcache.size();
        } catch (RuntimeException e) {
            Timber.e(e.getMessage());
            return -1L;
        }
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
    private static final long IMMEDIATE_MAX_DB_ENTRIES = 1L;

    /**
     * Wait interval in millis between successfive upload attempts in "immediate" configuration
     */
    private static final long IMMEDIATE_UPLOAD_INTERVAL = 5000L;

    /**
     * This method checks if the conditions are met to trigger a remote upload, and then starts an asynchronous task to perform
     * the upload if so
     *
     * @param strategy the upload strategy to use
     */
    @SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
    public final int checkUploadConditions(UploadStrategy strategy) {
        // 1. Check preconditions
        if (endpointApiBuilder == null) {
            Timber.w("{uploadIfNeeded} No remote app engine API for uploading.");
            return INTERNAL_ERROR;
        }

        if (getHelper() == null) {
            Timber.w("{uploadIfNeeded} No helper found");
            return INTERNAL_ERROR;
        }

        if (getHelper().getDao() == null) {
            Timber.w("{uploadIfNeeded} getHelper().getDao() is null");
            return INTERNAL_ERROR;
        }

        // 2. Check if an upload is already in progress
        //noinspection VariableNotUsedInsideIf
        if (uploadTask != null) {
            return UPLOAD_ALREADY_IN_PROGRESS;
        }

        // 3. Determine if an upload is ready based on parameters
        long maxDbEntries;
        long uploadInterval;
        boolean fbWifiOnly = mFirebaseRemoteConfig.getBoolean(context.getString(R.string.UPLOAD_ON_WIFI_ONLY_KEY));
        boolean userWifiOnly = !prefs.getBoolean(context.getString(R.string.pref_upload_on_wifi), false);


        if (strategy == UploadStrategy.IMMEDIATE) {
            maxDbEntries = IMMEDIATE_MAX_DB_ENTRIES;
            uploadInterval = IMMEDIATE_UPLOAD_INTERVAL;
        } else {
            maxDbEntries = mFirebaseRemoteConfig.getLong(context.getString(R.string.MAX_DB_ENTRIES_KEY));
            uploadInterval = mFirebaseRemoteConfig.getLong(context.getString(R.string.UPLOAD_INTERVAL_KEY));
        }

        int status = UPLOAD_READY;
        try {
            long numEntries = getHelper().getDao().countOf();
            if (strategy == UploadStrategy.IMMEDIATE) {
                numEntries += (long) memcache.size();
            }

            if (numEntries < maxDbEntries) {
                status |= DATABASE_LIMIT_NOT_MET;
            }

            if ((System.currentTimeMillis() - lastUploadAttempt) <= uploadInterval) {
                status |= UPLOAD_INTERVAL_NOT_MET;
            }

            NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();
            //noinspection OverlyComplexBooleanExpression
            if ((activeNetwork == null) || !activeNetwork.isConnected() || ((fbWifiOnly || userWifiOnly) && (activeNetwork.getType() != ConnectivityManager.TYPE_WIFI))) {
                status |= NO_INTERNET_CONNECTION;
            }

        } catch (RuntimeException e) {
            Timber.e("{uploadIfNeeded}  Unable to get count of database entries.", e);
            status |= INTERNAL_ERROR;
        }
        return status;
    }

    /**
     * Starts an asynchronous task to perform the upload
     */
    private void upload() {
        Timber.d("Upload now called");
        lastUploadAttempt = System.currentTimeMillis();

        uploadTask = uploadTaskFactory.createRemoteUploadTask(context, endpointApiBuilder).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * This method should not be called by clients.
     * <p/>
     * Examines the upload result and cleans the database accordingly. Also notifies listeners with the upload result.
     *
     * @param uploadResult the upload result passed from the remote upload task
     */
    @Subscribe
    void doPostUpload(RemoteUploadResult uploadResult) {
        currentDbSize = getHelper().getDao().countOf();
        uploadTask = null;

        if (uploadResult == null) {
            Timber.w("uploadResult was null for some reason");
            return;
        }

        if (!uploadResult.isUploadAttempted()) {
            Timber.i("{doPostUpload} Upload aborted: no entries were sent to be uploaded.");
            return;
        }

        if (uploadResult.getException() != null) {
            Timber.w("{doPostUpload} Uploading entries failed: " + uploadResult.getException().getMessage());
            dbTaskFactory.createCleanupTask(context, mFirebaseRemoteConfig.getLong(context.getString(R.string.DB_FORCED_CLEANUP_LIMIT_KEY))).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }


    /**
     * Closes the Cache, which has the effect of flushing pending entries to the database.
     * <p/>
     * Should be called when the app is destroyed, or other events when the cache is no longer needed.
     */
    public void close(UploadStrategy strategy) {
        flush(strategy);
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
    private DatabaseOpenHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(context, DatabaseOpenHelper.class);
        }
        return databaseHelper;
    }
}
