package org.fraunhofer.cese.funf_sensor.cache;

import android.os.AsyncTask;
import android.util.Log;

import com.google.inject.Inject;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Created by Lucas on 10/5/2015.
 */
public class DatabaseWriteAsyncTask extends AsyncTask<Map<String, CacheEntry>, Void, Collection<String>> {

    private static final String TAG = "Fraunhofer." + DatabaseWriteAsyncTask.class.getSimpleName();

    private final Cache cache;

    @Inject
    public DatabaseWriteAsyncTask(Cache cache) {
        this.cache = cache;
    }

    @Override
    protected Collection<String> doInBackground(Map<String, CacheEntry>... memcaches) {
        Collection<String> result = new ArrayList<>();
        if (memcaches == null)
            return result;

        if (cache == null) {
            Log.e(TAG, "{doInBackground} Cache is null!");
            return result;
        }

        if (cache.getHelper() == null) {
            Log.w(TAG, "{doInBackground} Attempting to write cache to database, but ProbeCacheOpenHelper is null. Returning empty result.");
            return result;
        }

        RuntimeExceptionDao<CacheEntry, String> dao = cache.getHelper().getDao();
        if (dao == null) {
            Log.w(TAG, "{doInBackground} Attempting to write cache to database, but ProbeCacheOpenHelper.getDao() is null. Returning empty result.");
            return result;
        }

        long oldDatabaseSize = dao.countOf();
        for (Map<String, CacheEntry> memcache : memcaches) {
            if (memcache != null && !memcache.isEmpty()) {
                for (CacheEntry entry : memcache.values()) {
                    if (dao.create(entry) > 0) {
                        result.add(entry.getId());
                    } else {
                        Log.d(TAG, "{doInBackground} Entry was not saved to the database: " + entry.toString());
                    }
                }
            }
        }

        long newDatabaseSize = dao.countOf();
        Log.d(TAG, "{doInBackground} db entries added: " + (newDatabaseSize - oldDatabaseSize) + ", total db entries: " + newDatabaseSize);
        return result;
    }

    @Override
    protected void onPostExecute(Collection<String> savedEntries) {
        cache.doPostDatabaseWrite(savedEntries);
    }
}
