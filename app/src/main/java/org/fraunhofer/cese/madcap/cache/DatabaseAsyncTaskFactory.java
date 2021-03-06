package org.fraunhofer.cese.madcap.cache;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.greenrobot.eventbus.EventBus;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.inject.Inject;

import timber.log.Timber;

/**
 * Factory for creating aysnchronous database related tasks.
 *
 * @author Lucas
 * @see Cache
 */
@SuppressWarnings({"FinalClass", "UtilityClass", "UtilityClassCanBeEnum", "UtilityClassWithoutPrivateConstructor"})
final class DatabaseAsyncTaskFactory {

    /**
     * Default no-arg constructor.
     * The @Inject annotation tells Dagger2 to use this constructor to create new instances of this class.
     */
    @SuppressWarnings("RedundantNoArgConstructor")
    @Inject
    DatabaseAsyncTaskFactory() {

    }

    /**
     * Create an asynchronous task which supports asynchronous writing of cache entries to a persistent SQLite database.
     * This class is used when cache entries stored in memory should be persisted. Results of the database writing actions
     * are stored in a DatabaseWriteResult.
     * <p/>
     * This task supports partial saves, i.e., the task will attempt to save each entry. If it encounters an exception or error,
     * it will abort but any previously saved entries will be persisted in the database.
     *
     * @param context the calling context needed to reference the database
     * @return a new instance of an asynchronous database writing task
     * @see DatabaseWriteResult
     */
    AsyncTask<Map<String, CacheEntry>, Void, DatabaseWriteResult> createWriteTask(Context context, UploadStrategy uploadStrategy) {

        //noinspection OverloadedVarargsMethod,OverloadedVarargsMethod
        return new DataBaseWriteTask(uploadStrategy, context);
    }

    private static class DataBaseWriteTask extends AsyncTask<Map<String, CacheEntry>, Void, DatabaseWriteResult> {
        private static final String TAG = "Fraunhofer.DBWrite";
        private final UploadStrategy uploadStrategy;
        private final Context context;

        private DataBaseWriteTask(UploadStrategy uploadStrategy, Context context) {
            this.uploadStrategy = uploadStrategy;
            this.context = context;
        }

        @SuppressWarnings("ParameterNameDiffersFromOverriddenParameter")
        @Override
        @SafeVarargs
        public final DatabaseWriteResult doInBackground(Map<String, CacheEntry>... memcaches) {
            Thread.currentThread().setName(TAG);
            Log.d(TAG, "Do in Background executed");
            DatabaseWriteResult result = DatabaseWriteResult.create(uploadStrategy);
            // Check preconditions for full or partial write of entry objects to database
            if (context == null) {
                Log.e(TAG, "Context is Null");
                result.setError(new RuntimeException("{doInBackground} context object is null!"));
                return result;
            }

            DatabaseOpenHelper databaseHelper = OpenHelperManager.getHelper(context, DatabaseOpenHelper.class);

            if (databaseHelper == null) {
                Log.e(TAG, "DatabaseHelper is Null");
                result.setError(new RuntimeException("{doInBackground} Attempting to write cache to database, but DatabaseOpenHelper is null. Returning empty result."));
                return result;
            }

            RuntimeExceptionDao<CacheEntry, String> dao;
            try {
                dao = databaseHelper.getDao();
            } catch (RuntimeException e) {
                result.setError(e);
                return result;
            }

            if (dao == null) {
                result.setError(new RuntimeException("{doInBackground} Attempting to write cache to database, but DatabaseOpenHelper.getDao() is null. Returning empty result."));
                return result;
            }

            // Try to save objects to the database. Supports partial saves, i.e., only some objects are saved.
            Collection<String> savedEntries = new ArrayList<>(1000);
            try {
                if (memcaches != null) {
                    for (Map<String, CacheEntry> memcache : memcaches) {
                        if ((memcache != null) && !memcache.isEmpty()) {
                            int skippedCount = 0;
                            for (CacheEntry entry : memcache.values()) {
                                if (databaseHelper.isOpen() && (dao.queryForId(entry.getId()) == null) && (dao.create(entry) > 0)) {
                                    savedEntries.add(entry.getId());
                                } else {
                                    skippedCount++;
                                }
                            }
                            Timber.d("{doInBackground} Skipped " + skippedCount + " entries already in database.");
                        }
                    }
                }
            } catch (RuntimeException e) {
                result.setError(e);
            } finally {
                if (databaseHelper.isOpen()) {
                    result.setDatabaseSize(dao.countOf());
                }
                result.setSavedEntries(savedEntries);
            }
            return result;
        }

        @Override
        public void onPostExecute(DatabaseWriteResult result) {
            OpenHelperManager.releaseHelper();
            EventBus.getDefault().post(result);
            Timber.d("onPostExecute");
        }

        @Override
        protected void onCancelled(DatabaseWriteResult result) {
            super.onCancelled(result);
            OpenHelperManager.releaseHelper();
            Timber.d("onCancelled dbWriteResult");
        }

        /**
         * <p>Applications should preferably override {@link #onCancelled(Object)}.
         * This method is invoked by the default implementation of
         * {@link #onCancelled(Object)}.</p>
         * <p>
         * <p>Runs on the UI thread after {@link #cancel(boolean)} is invoked and
         * {@link #doInBackground(Object[])} has finished.</p>
         *
         * @see #onCancelled(Object)
         * @see #cancel(boolean)
         * @see #isCancelled()
         */
        @Override
        protected void onCancelled() {
            super.onCancelled();

            Timber.d("onCancelled no args");
        }
    }

    /**
     * Creates an asynchronous task to remove entries from a database that has surpassed the specified limit. The oldest entries will be removed
     * based on their timestamp.
     *
     * @param context      the calling context needed to access the database
     * @param dbEntryLimit the maximum number of objects to be left in the database. If < 0, no operation is performed.
     * @return the new task instance
     */

    AsyncTask<Void, Void, Void> createCleanupTask(Context context, long dbEntryLimit) {
        //noinspection OverloadedVarargsMethod
        return new CleanupTask(dbEntryLimit, context);
    }


    private static class CleanupTask extends AsyncTask<Void, Void, Void> {
        private static final int CURSOR_INCREMENT = 250;
        private static final String TAG = "Fraunhofer.DBCleanup";
        private final long dbEntryLimit;
        private final Context context;

        private CleanupTask(long dbEntryLimit, Context context) {
            this.dbEntryLimit = dbEntryLimit;
            this.context = context;
        }

        @android.support.annotation.Nullable
        @Override
        protected Void doInBackground(Void... params) {
            Timber.d("Running task to determine if database is still within size limits");
            Thread.currentThread().setName(TAG);

            if (dbEntryLimit < 0L) {
                return null;
            }

            DatabaseOpenHelper databaseHelper = OpenHelperManager.getHelper(context, DatabaseOpenHelper.class);
            if ((databaseHelper == null) || (databaseHelper.getDao() == null)) {
                return null;
            }

            RuntimeExceptionDao<CacheEntry, String> dao = databaseHelper.getDao();
            long size = dao.countOf();
            if (size < dbEntryLimit) {
                Timber.i("No cleanup needed. Database limit: " + dbEntryLimit + " > Database size: " + size);
            } else {
                Timber.i("Attempting cleanup. Database limit: " + dbEntryLimit + " <= Database size: " + size);

                try {
                    long numToDelete = size - (dbEntryLimit / 2L);
                    List<CacheEntry> toDelete = dao.queryBuilder()
                            .selectColumns(CacheEntry.ID_FIELD_NAME)
                            .orderBy(CacheEntry.TIMESTAMP_FIELD_NAME, true)
                            .limit(numToDelete)
                            .query();

                    @SuppressWarnings("AnonymousInnerClassMayBeStatic") List<String> toDeleteIds = Lists.transform(toDelete, new Function<CacheEntry, String>() {
                        @SuppressWarnings("ParameterNameDiffersFromOverriddenParameter")
                        @Nullable
                        @Override
                        public String apply(CacheEntry cacheEntry) {
                            return cacheEntry.getId();
                        }
                    });

                    int cursor = 0;
                    while (cursor < toDeleteIds.size()) {

                        dao.deleteIds(toDeleteIds.subList(cursor, (((cursor + CURSOR_INCREMENT) > toDeleteIds.size()) ? toDeleteIds.size() : (cursor + CURSOR_INCREMENT))));
                        cursor += CURSOR_INCREMENT;
                    }

                    Timber.i("Cleanup completed. New database size: " + dao.countOf());
                } catch (SQLException e) {
                    Timber.e("Unable to delete entries from database", e);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            OpenHelperManager.releaseHelper();
        }

        @Override
        protected void onCancelled(Void result) {
            super.onCancelled(result);
            OpenHelperManager.releaseHelper();
        }
    }
}


