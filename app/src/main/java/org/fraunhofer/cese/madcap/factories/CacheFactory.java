package org.fraunhofer.cese.madcap.factories;

import android.util.Log;

import org.fraunhofer.cese.madcap.authentification.MadcapAuthManager;
import org.fraunhofer.cese.madcap.cache.Cache;
import org.fraunhofer.cese.madcap.cache.CacheEntry;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import edu.umd.fcmd.sensorlisteners.model.State;
import edu.umd.fcmd.sensorlisteners.service.StateManager;

/**
 * Created by ANepaul on 10/28/2016.
 */

public class CacheFactory implements StateManager {
    private static final String TAG = CacheFactory.class.getSimpleName();
    private MadcapAuthManager madcapAuthManager = MadcapAuthManager.getInstance();

    private final Cache cache;

    @Override
    public void save(State state) {
        saveToCache(state);
    }

    @Override
    public List getAll() {
        return null;
    }

    enum DataType {
        ACCELEROMETER
    }

    @Inject
    public CacheFactory(Cache cache) {
        this.cache = cache;
    }

    public void saveToCache(State state) {
        //Log.i(TAG, "UID "+madcapAuthManager.getUserId());
        CacheEntry probeEntry = new CacheEntry();
        probeEntry.setId(UUID.randomUUID().toString());
        probeEntry.setTimestamp(state.getDate());
        probeEntry.setUserID(madcapAuthManager.getUserId());
        probeEntry.setProbeType(state.getType());
        probeEntry.setSensorData(state.toString());

        Log.i(TAG, "CACHED "+probeEntry.toString());
        cache.add(probeEntry);
    }
}
