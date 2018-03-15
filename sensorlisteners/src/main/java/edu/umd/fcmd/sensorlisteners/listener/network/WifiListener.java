package edu.umd.fcmd.sensorlisteners.listener.network;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v4.content.ContextCompat;

import javax.inject.Inject;

import edu.umd.fcmd.sensorlisteners.listener.Listener;
import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.model.network.NetworkProbeFactory;
import edu.umd.fcmd.sensorlisteners.model.network.WiFiProbe;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;
import edu.umd.fcmd.sensorlisteners.service.WifiServiceNotification;

/**
 * Created by MMueller on 12/2/2016.
 * <p>
 * Listener Class for everything network related like:
 * <p>
 * Internet, Calls, SMS
 */
public class WifiListener extends BroadcastReceiver implements Listener {
    private boolean runningStatus;

    private final Context mContext;
    private final ProbeManager<Probe> probeProbeManager;
    private final WifiManager wifiManager;
    private final NetworkProbeFactory factory;
    private final WifiServiceNotification wifiServiceNotification;

    @Inject
    public WifiListener(Context context,
                        ProbeManager<Probe> probeProbeManager,
                        WifiManager wifiManager,
                         WifiServiceNotification wifiServiceNotification,
                        NetworkProbeFactory factory) {
        mContext = context;

         this.probeProbeManager = probeProbeManager;
        this.wifiManager = wifiManager;
        this.factory = factory;
        this.wifiServiceNotification = wifiServiceNotification;
        this.wifiServiceNotification.context = context;


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

            wifiServiceNotification.createWiFiProbe(wifiManager.getConnectionInfo().getSSID(),
                                                    wifiManager.getScanResults());


            mContext.registerReceiver(this, intentFilter);
            onUpdate(factory.createWiFiProbe(wifiManager.getConnectionInfo().getIpAddress(),
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

        WiFiProbe probe = factory.createWiFiProbe(wifiManager.getConnectionInfo().getIpAddress(),
                wifiManager.getConnectionInfo().getSSID(),
                wifiManager.getScanResults(),
                intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0));

        wifiServiceNotification.createWiFiProbe(wifiManager.getConnectionInfo().getSSID(),
                wifiManager.getScanResults());

        onUpdate(probe);
    }

    public String securityCheck(){


        String currentSsid = wifiManager.getConnectionInfo().getSSID();
        Iterable<ScanResult> networkList = wifiManager.getScanResults();

        String security = "-";
        String SSID = "-";
        // context.startService(new Intent(context, NotificationPostingService.class));

        for (ScanResult network : networkList) {

            //check if current connected SSID.
            if (currentSsid.equals('\"' + network.SSID + '\"')) {
                //get capabilities of current connection
                String capabilities = network.capabilities;

                if (capabilities.toUpperCase().contains("WPA2")) {
                    security = "WPA2";
                    SSID = network.SSID;
                  //  Security = "WPA2";
                }
                else{
                    security = capabilities;
                    SSID = network.SSID;
                }
            }
        }
        //This if statement is used to make sure that the same notification is
        // not posted more than once.
      //  if (!(OldSSID.equals(SSID))) {

         //   if (security == "WPA2" ) {
                //       context.startService(new Intent(context, NotificationPostingService.class));
       //         return  "WPA2";
       //     } else{

          //      return security;
        //    }

     //   }
        return security;



    }


}
