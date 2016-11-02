package org.fraunhofer.cese.madcap.factories;

import android.util.Log;

import org.fraunhofer.cese.madcap.cache.Cache;
import org.fraunhofer.cese.madcap.cache.CacheEntry;

import java.util.UUID;

import javax.inject.Inject;

import edu.umd.fcmd.sensorlisteners.model.State;
import edu.umd.fcmd.sensorlisteners.service.StateManager;

/**
 * Created by ANepaul on 10/28/2016.
 */

public class CacheFactory implements StateManager {
    private static final String TAG = CacheFactory.class.getSimpleName();

    private final Cache cache;

    enum DataType {
        ACCELEROMETER
    }

    @Inject
    public CacheFactory(Cache cache) {
        this.cache = cache;
    }

    public void saveToCache(State state) {
        Log.i(TAG, "saveToCache: Sensor Added to Cache");
        CacheEntry probeEntry = new CacheEntry();
        probeEntry.setId(UUID.randomUUID().toString());
        probeEntry.setTimestamp(state.getDate().getTime());
        probeEntry.setProbeType(DataType.ACCELEROMETER.name());
        probeEntry.setSensorData(state.toString());

        cache.add(probeEntry);
    }
}
