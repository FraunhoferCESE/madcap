package org.fraunhofer.cese.madcap.services;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import javax.inject.Inject;

import edu.umd.fcmd.sensorlisteners.listener.Listener;
import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.model.network.WiFiProbe;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;

/**
 * Created by MMueller on 12/2/2016.
 * <p>
 * Listener Class for everything network related like:
 * <p>
 * Internet, Calls, SMS
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
       // wifiService.onCreate();


        this.probeProbeManager = probeProbeManager;
        this.wifiManager = wifiManager;
       // context.startService(new Intent(context, NotificationService.class));
        //wifiService.startService(new Intent(context, WifiService.class));
       // Looper looper = null;

        //context.startService(
    //   Intent intent = new Intent(context, WifiService.class);


       // Intent myIntent = new Intent(context, WifiService.class);
       // context.startService(myIntent);

        this.wifiService = wifiService;
        this.wifiService.context = context;
        //this.wifiService.startService(new Intent(context, WifiService.class));

        int number = 10;

    }


    @Override
    public void onUpdate(Probe state) {
        probeProbeManager.save(state);
    }

    @Override
    public void startListening() {

       // mContext.startService(new Intent(this, WifiService.class));

       // mContext.startService(new Intent(mContext, WifiService.class));
        if (!runningStatus) {
            Log.i("Tobias123", "StartListening");

            IntentFilter intentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
            intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
            intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);

            mContext.registerReceiver(this, intentFilter);
            onUpdate(wifiService.createWiFiProbe(wifiManager.getConnectionInfo().getIpAddress(),
                                             wifiManager.getConnectionInfo().getSSID(),
                                             wifiManager.getScanResults(),
                                             wifiManager.getWifiState()));

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
        Log.i("Tobias123", "OnReceive");
        //Intent startServiceIntent = new Intent(context, WifiService.class);


        WiFiProbe probe = wifiService.createWiFiProbe(wifiManager.getConnectionInfo().getIpAddress(),
                wifiManager.getConnectionInfo().getSSID(),
                wifiManager.getScanResults(),
                intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0));

        onUpdate(probe);
    }


}
