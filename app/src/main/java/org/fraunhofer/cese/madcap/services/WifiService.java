package org.fraunhofer.cese.madcap.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.widget.Toast;

import javax.inject.Inject;



//package edu.umd.fcmd.sensorlisteners.model.network;

/**
 * Factory class for creating networking-related probes
 */
@SuppressWarnings("MethodMayBeStatic")
public class WifiService extends Service {

    Context context;

    @SuppressWarnings("RedundantNoArgConstructor")
    @Inject
    public WifiService(Context context) {
        this.context = context;


    }
    public class LocalBinder extends Binder {
        WifiService getService() {
            return WifiService.this;
        }
    }

    @Override
    public void onCreate() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        // Tell the user we stopped.
        Toast.makeText(this, "Stop!!!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IBinder mBinder = new LocalBinder();

    @NonNull
    public void createWiFiProbe( String currentSsid, Iterable<ScanResult> networkList) {

        String security = "-";
        for (ScanResult network : networkList) {

            //check if current connected SSID.
            if (currentSsid.equals('\"' + network.SSID + '\"')) {
                //get capabilities of current connection
                String capabilities = network.capabilities;

                if (capabilities.toUpperCase().contains("OPEN")) {
                    security = "OPEN";
                }
            }
        }
        if (security == "OPEN") {
            context.startService(new Intent(context, NotificationService.class));
        }

    }
}


