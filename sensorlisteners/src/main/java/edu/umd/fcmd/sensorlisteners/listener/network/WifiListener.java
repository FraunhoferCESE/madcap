package edu.umd.fcmd.sensorlisteners.listener.network;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v4.content.ContextCompat;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.inject.Inject;

import edu.umd.fcmd.sensorlisteners.listener.Listener;
import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.model.network.WiFiProbe;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;
import timber.log.Timber;

/**
 * Created by MMueller on 12/2/2016.
 * <p>
 * Listener Class for everything network related like:
 * <p>
 * Internet, Calls, SMS
 */
public class WifiListener extends BroadcastReceiver implements Listener {
    private boolean runningStatus;

    private BroadcastReceiver networkReceiver;
    private final Context mContext;
    private final ProbeManager<Probe> probeProbeManager;
    private final WifiManager wifiManager;

    @Inject
    public WifiListener(Context context,
                        ProbeManager<Probe> probeProbeManager,
                        WifiManager wifiManager) {
        mContext = context;
        this.probeProbeManager = probeProbeManager;
        this.wifiManager = wifiManager;
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

            WiFiProbe wiFiProbe = new WiFiProbe();
            wiFiProbe.setDate(System.currentTimeMillis());
            wiFiProbe.setIp(getIpAddress(wifiManager.getConnectionInfo().getIpAddress()));
            wiFiProbe.setNetworkSecurity(getCurrentSecurityLevel(wifiManager.getConnectionInfo().getSSID(), wifiManager.getScanResults()));
            wiFiProbe.setState(getWifiState(wifiManager.getWifiState()));
            wiFiProbe.setSsid(wifiManager.getConnectionInfo().getSSID());
            onUpdate(wiFiProbe);

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

    /**
     * Returns the current wifi state as a String form an int.
     *
     * @param i Current wifi state.
     * @return a String representation of hte current Wifi state
     */
    private static String getWifiState(int i) {
        String result;

        switch (i) {
            case WifiManager.WIFI_STATE_ENABLED:
                result = "ENABLED";
                break;
            case WifiManager.WIFI_STATE_DISABLED:
                result = "DISABLED";
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                result = "ENABLING";
                break;
            case WifiManager.WIFI_STATE_DISABLING:
                result = "DISABLING";
                break;
            case WifiManager.WIFI_STATE_UNKNOWN:
                result = "UNKNOWN";
                break;
            default:
                result = "-";
                break;
        }
        return result;
    }

    private static String getCurrentSecurityLevel(String currentSSID, Iterable<ScanResult> networkList) {
        //get current connected SSID for comparison to ScanResult
        String result = "-";

        for (ScanResult network : networkList) {
            //check if current connected SSID.
            if (currentSSID.equals('\"' + network.SSID + '\"')) {
                //get capabilities of current connection
                String capabilities = network.capabilities;
                Timber.d(network.SSID + " capabilities : " + capabilities);

                if (capabilities.contains("WPA2")) {
                    result = "WPA2";
                } else if (capabilities.contains("WPA")) {
                    result = "WPA";
                } else if (capabilities.contains("WEP")) {
                    result = "WEP";
                } else if (capabilities.contains("Open")) {
                    result = "OPEN";
                }
            }
        }

        return result;
    }

    /**
     * Method for getting the current used IP-Address.
     *
     * @return current Ip-Address.
     */
    private static String getIpAddress(int ipAddress) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.order(ByteOrder.nativeOrder());
        byteBuffer.putInt(ipAddress);

        String ipAddressString;

        try {
            ipAddressString = InetAddress.getByAddress(byteBuffer.array()).getHostAddress();
        } catch (UnknownHostException ex) {
            Timber.d("Unable to convert host address", ex);
            ipAddressString = "N/A";
        }

        return ipAddressString;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        WiFiProbe wiFiProbe = new WiFiProbe();
        wiFiProbe.setDate(System.currentTimeMillis());
        wiFiProbe.setIp(getIpAddress(wifiManager.getConnectionInfo().getIpAddress()));
        wiFiProbe.setState(getWifiState(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0)));
        wiFiProbe.setNetworkSecurity(getCurrentSecurityLevel(wifiManager.getConnectionInfo().getSSID(), wifiManager.getScanResults()));
        wiFiProbe.setSsid(wifiManager.getConnectionInfo().getSSID());

        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            if (intent.hasExtra(WifiManager.EXTRA_NETWORK_INFO)) {
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                wiFiProbe.setNetworkState(networkInfo.toString());
            }
        }

        onUpdate(wiFiProbe);

    }


}
