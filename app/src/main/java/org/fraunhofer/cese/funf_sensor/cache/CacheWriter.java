package org.fraunhofer.cese.funf_sensor.cache;

import android.os.AsyncTask;

import com.j256.ormlite.dao.Dao;

import org.fraunhofer.cese.funf_sensor.backend.models.probeDataSetApi.model.ProbeDataSet;

import java.sql.SQLException;
import java.util.Collection;

/**
 * Created by Lucas on 10/5/2015.
 */
public class CacheWriter extends AsyncTask<Collection<CacheEntry>, Void, Integer> {

    private final ProbeCache probeCache;
    private final Dao<CacheEntry, String> dao;

    public CacheWriter(ProbeCache probeCache) throws SQLException {
        this.probeCache = probeCache;
        this.dao = probeCache.getHelper().getDao();

    }

    @Override
    protected Integer doInBackground(Collection<CacheEntry>... cacheEntryCollections) {
        if(cacheEntryCollections == null)
            return 0;

        int numSaved = 0;
        for (Collection<CacheEntry> cacheEntries: cacheEntryCollections) {
            if(cacheEntries != null && !cacheEntries.isEmpty())
            for(CacheEntry entry : cacheEntries) {
                //ths is probably why I need the runtimeexception stuff...


                //dao.create(entry);

            }

        }

        return null;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
    }
}
