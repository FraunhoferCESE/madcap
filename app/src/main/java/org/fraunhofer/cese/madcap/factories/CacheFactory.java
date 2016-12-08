package org.fraunhofer.cese.madcap.factories;

import android.util.Log;

import org.fraunhofer.cese.madcap.authentication.AuthenticationManager;
import org.fraunhofer.cese.madcap.cache.Cache;
import org.fraunhofer.cese.madcap.cache.CacheEntry;

import java.util.UUID;

import javax.inject.Inject;

import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;

/**
 * Factory class for saving Probe data to the Cache for eventual upload.
 *
 * Created by ANepaul on 10/28/2016.
 */

public class CacheFactory implements ProbeManager {
    private static final String TAG = CacheFactory.class.getSimpleName();

    private final AuthenticationManager authenticationManager;
    private final Cache cache;

    @Override
    public void save(Probe probe) {
        //Log.i(TAG, "UID "+authenticationManager.getUserId());
        if (authenticationManager.getUserId() != null) {
            CacheEntry probeEntry = new CacheEntry();
            probeEntry.setId(UUID.randomUUID().toString());
            probeEntry.setTimestamp(probe.getDate());
            probeEntry.setUserID(authenticationManager.getUserId());
            probeEntry.setProbeType(probe.getType());
            probeEntry.setSensorData(probe.toString());

            Log.i(TAG, "CACHED " + probeEntry);
            cache.add(probeEntry);
        }
        // TODO: Figure out what to do when getUserId() == null.
        //start login service again
//            context.startService(new Intent(context, LoginService.class));

    }

    @Inject
    public CacheFactory(Cache cache, AuthenticationManager authenticationManager) {
        this.cache = cache;
        this.authenticationManager = authenticationManager;
    }
}