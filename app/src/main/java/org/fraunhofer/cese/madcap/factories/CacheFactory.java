package org.fraunhofer.cese.madcap.factories;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.fraunhofer.cese.madcap.MyApplication;
import org.fraunhofer.cese.madcap.SignInActivity;
import org.fraunhofer.cese.madcap.authentification.MadcapAuthManager;
import org.fraunhofer.cese.madcap.cache.Cache;
import org.fraunhofer.cese.madcap.cache.CacheEntry;
import org.fraunhofer.cese.madcap.services.DataCollectionService;
import org.fraunhofer.cese.madcap.services.LoginService;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;

/**
 * Created by ANepaul on 10/28/2016.
 */

public class CacheFactory implements ProbeManager {
    private static final String TAG = CacheFactory.class.getSimpleName();
    private MadcapAuthManager madcapAuthManager = MadcapAuthManager.getInstance();

    private final Cache cache;
    private final Context context;

    @Override
    public void save(Probe probe) {
        saveToCache(probe);
    }

    enum DataType {
        ACCELEROMETER
    }

    @Inject
    public CacheFactory(Cache cache, Context context) {
        this.cache = cache;
        this.context = context;
    }

    public void saveToCache(Probe probe) {
        //Log.i(TAG, "UID "+madcapAuthManager.getUserId());
        if(madcapAuthManager.getUserId() != null){
            CacheEntry probeEntry = new CacheEntry();
            probeEntry.setId(UUID.randomUUID().toString());
            probeEntry.setTimestamp(probe.getDate());
            probeEntry.setUserID(madcapAuthManager.getUserId());
            probeEntry.setProbeType(probe.getType());
            probeEntry.setSensorData(probe.toString());

            Log.i(TAG, "CACHED "+probeEntry.toString());
            cache.add(probeEntry);
        }else{
            //start login service again
            context.startService(new Intent(context, LoginService.class));
        }
    }
}