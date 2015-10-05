package org.fraunhofer.cese.funf_sensor.cache;

import android.content.Context;
import android.util.Log;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import org.fraunhofer.cese.funf_sensor.backend.models.probeDataSetApi.model.ProbeDataSet;
import org.fraunhofer.cese.funf_sensor.backend.models.probeDataSetApi.model.ProbeEntry;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import roboguice.inject.ContextSingleton;

/**
 * Created by Lucas on 10/5/2015.
 */
@ContextSingleton
public class ProbeCache {

    private static final int MAX_MEM_ENTRIES = 0;
    private static final int MAX_DB_ENTRIES = 2;

    private static final String TAG = "Fraunhofer." + ProbeCache.class.getSimpleName();

    private Collection<CacheEntry> memcache = new ArrayList<>();

    @Inject
    Context context;

    private ProbeCacheOpenHelper databaseHelper = null;

    public void add(CacheEntry entry) throws SQLException {
        if(entry == null)
            return;

        memcache.add(entry);
        if(memcache.size() > MAX_MEM_ENTRIES) {
            new CacheWriter(this).execute(memcache);
        }
    }

    protected void doPostCacheWrite() throws SQLException {
        if(getHelper().getDao().countOf() > MAX_MEM_ENTRIES)
            uploadData();
    }

    private void uploadData() throws SQLException {

        // Handle timeouts and other connection errors
        ProbeDataSet dataSet = new ProbeDataSet();
        dataSet.setTimestamp(Calendar.getInstance().getTimeInMillis());

        List<CacheEntry> entries = getHelper().getDao().queryForAll();

        List<ProbeEntry> toUpload = Lists.transform(entries, new Function<CacheEntry, ProbeEntry>() {
            @Nullable
            @Override
            public ProbeEntry apply(CacheEntry cacheEntry) {
                return CacheEntry.createProbeEntry(cacheEntry);
            }
        });

        dataSet.setEntryList(toUpload);
        new ListOfMessagesAsyncSender(this).execute(dataSet);
    }

    protected synchronized void removeEntries(List<String> ids) throws SQLException {
        if(ids != null && ids.size() > 0) {
            Log.d(TAG, "Removing entries from cache.");

            int result = getHelper().getDao().deleteIds(ids);

            Log.d(TAG, "cache changed?: " + result + ", cache size: "+getHelper().getDao().countOf());
        }
    }

    public void flush() {
        // TODO: implement
    }

    public void close() {
        flush();
        if(databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    public ProbeCacheOpenHelper getHelper() {
        if(databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(context,ProbeCacheOpenHelper.class);
        }
        return databaseHelper;
    }
}
