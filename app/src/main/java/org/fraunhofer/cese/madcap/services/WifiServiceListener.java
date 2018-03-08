package org.fraunhofer.cese.madcap.services;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.support.v4.content.ContextCompat;

import javax.inject.Inject;

import edu.umd.fcmd.sensorlisteners.listener.Listener;
import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;

/**
 * Created by Thorfinnur on 3/8/2018.
 * <p>
 * Listener Class for the wifi connection and is only used to check if the phone is
 * connected to an open wifi.
 * <p>
 *
 */
public class WifiServiceListener extends BroadcastReceiver implements Listener {
    private boolean runningStatus;

    private final Context mContext;
    private final ProbeManager<Probe> probeProbeManager;
    private final WifiManager wifiManager;
    private final WifiService wifiService;


    @Inject
    public WifiServiceListener(Context context,
                               ProbeManager<Probe> probeProbeManager,
                               WifiManager wifiManager,
                               WifiService wifiService) {

        mContext = context;

        this.probeProbeManager = probeProbeManager;
        this.wifiManager = wifiManager;
        this.wifiService = wifiService;
        this.wifiService.context = context;
    }


    @Override
    public void onUpdate(Probe state) {
        probeProbeManager.save(state);
    }

    @Override
    public void startListening() {

        if (!runningStatus) {

            IntentFilter intentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
            intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
            intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);

            mContext.registerReceiver(this, intentFilter);
            wifiService.createWiFiProbe(wifiManager.getConnectionInfo().getSSID(),
                                             wifiManager.getScanResults());

            runningStatus = true;
        }
    }
    
    @Override
    public void stopListening() {
        if (runningStatus) {
            mContext.unregisterReceiver(this);
            runningStatus = false;
        }
    }

    @Override
    public boolean isPermittedByUser() {
        return ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
         wifiService.createWiFiProbe(wifiManager.getConnectionInfo().getSSID(),
                wifiManager.getScanResults());
    }


}
