package org.fraunhofer.cese.madcap.cache;

import android.util.Log;

import org.fraunhofer.cese.madcap.authentication.AuthenticationProvider;

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

    private final AuthenticationProvider authenticationProvider;
    private final Cache cache;

    @Override
    public void save(Probe probe) {
        Log.d(TAG, "UID "+authenticationProvider.getUserId());
        if (authenticationProvider.getUserId() != null) {
            CacheEntry probeEntry = new CacheEntry();
            probeEntry.setId(UUID.randomUUID().toString());
            probeEntry.setTimestamp(probe.getDate());
            probeEntry.setUserID(authenticationProvider.getUserId());
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
    public CacheFactory(Cache cache, AuthenticationProvider authenticationProvider) {
        this.cache = cache;
        this.authenticationProvider = authenticationProvider;
    }
}