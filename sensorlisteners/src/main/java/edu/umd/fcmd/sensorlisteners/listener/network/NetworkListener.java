package edu.umd.fcmd.sensorlisteners.listener.network;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

import edu.umd.fcmd.sensorlisteners.NoSensorFoundException;
import edu.umd.fcmd.sensorlisteners.issuehandling.PermissionDeniedHandler;
import edu.umd.fcmd.sensorlisteners.listener.Listener;
import edu.umd.fcmd.sensorlisteners.listener.applications.TimedApplicationTaskFactory;
import edu.umd.fcmd.sensorlisteners.model.CellularProbe;
import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.model.WiFiProbe;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;

import static java.security.AccessController.getContext;

/**
 * Created by MMueller on 12/2/2016.
 */

public class NetworkListener implements Listener {
    private final String TAG = getClass().getSimpleName();
    private boolean runningStatus;

    private BroadcastReceiver receiver;
    private Context context;
    private ProbeManager<Probe> probeProbeManager;
    private ConnectionInfoReceiverFactory connectionInfoReceiverFactory;
    private PermissionDeniedHandler permissionDeniedHandler;

    private static final boolean isLittleEndian = ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN);

    public NetworkListener(Context context,
                           ProbeManager<Probe> probeProbeManager,
                           ConnectionInfoReceiverFactory connectionInfoReceiverFactory,
                           PermissionDeniedHandler permissionDeniedHandler){
        this.context = context;
        this.probeProbeManager = probeProbeManager;
        this.connectionInfoReceiverFactory = connectionInfoReceiverFactory;
        this.permissionDeniedHandler = permissionDeniedHandler;

        sendInitalProbes();
    }


    @Override
    public void onUpdate(Probe state) {
        probeProbeManager.save(state);
    }

    @Override
    public void startListening() throws NoSensorFoundException {
        if(!runningStatus){
            instanciateReceiver(this, context);
        }

        runningStatus = true;
    }

    @Override
    public void stopListening() {
        if(runningStatus){
            context.unregisterReceiver(receiver);
        }
        runningStatus = false;
    }

    @Override
    public boolean isRunning() {
        return runningStatus;
    }

    /**
     * Instanciation of the receiver.
     * Seperation of this into another method due to reduction of complexity and
     * for better testing.
     * @param networkListener
     * @param context
     */
    private void instanciateReceiver(NetworkListener networkListener, Context context){
        receiver = connectionInfoReceiverFactory.create(networkListener);

        IntentFilter intentFilter = new IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.RSSI_CHANGED");
        intentFilter.addAction("android.net.wifi.STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.NETWORK_STATE_CHANGED_ACTION");
        intentFilter.addAction("android.net.wifi.supplicant.STATE_CHANGED");
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.net.conn.CONNECTIVITY_ACTION");

        context.registerReceiver(receiver, intentFilter);
    }

    /**
     * Sends the inital probes. Should be triggered on the start listening event.
     */
    private void sendInitalProbes() {
        WiFiProbe wiFiProbe = new WiFiProbe();
        wiFiProbe.setDate(System.currentTimeMillis());
        wiFiProbe.setIp(getIpAddress());
        wiFiProbe.setInfo(getCurrentWiFiInfo());
        wiFiProbe.setState(getWifiState(((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getWifiState()));
        wiFiProbe.setSsid(getCurrentSSID());

        onUpdate(wiFiProbe);

        CellularProbe cellularProbe = new CellularProbe();
        cellularProbe.setDate(System.currentTimeMillis());
        cellularProbe.setState(getCellDataState());

        onUpdate(cellularProbe);

    }

    /**
     * Returns the state of the cellular network as a string.
     *
     * @return cellular network state
     */
    String getCellDataState() {
        String result;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        switch (telephonyManager.getDataState()) {
            case TelephonyManager.DATA_CONNECTED:
                result = "connected.";
                break;
            case TelephonyManager.DATA_DISCONNECTED:
                result = "disconnected.";
                break;
            case TelephonyManager.DATA_CONNECTING:
                result = "connecting.";
                break;
            case TelephonyManager.DATA_SUSPENDED:
                result = "suspended.";
                break;
            default:
                result = "Something went wrong.";
                break;
        }

        return result;
    }

    /**
     * Returns the current wifi state as a String for adding it to an intent.
     *
     * @param intent Current wifi state
     * @return
     */
    String getWifiState(Intent intent) {
        String result;

        switch (intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0)) {
            case WifiManager.WIFI_STATE_ENABLED:
                result = "enabled.";
                break;
            case WifiManager.WIFI_STATE_DISABLED:
                result = "disabled.";
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                result = "enabling.";
                break;
            case WifiManager.WIFI_STATE_DISABLING:
                result = "disabling";
                break;
            case WifiManager.WIFI_STATE_UNKNOWN:
                result = "unknown.";
                break;
            default:
                result = "Something went wrong";
                break;
        }
        return result;
    }

    String getCurrentWiFiInfo(){
        String permissionName = "android.permission.ACCESS_WIFI_STATE";
        int permission = context.checkCallingOrSelfPermission(permissionName);

        if(permission == PackageManager.PERMISSION_GRANTED){
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();

            return wifiInfo.toString();
        }else{
            permissionDeniedHandler.onPermissionDenied(Manifest.permission.ACCESS_WIFI_STATE);
            return "";
        }
    }

    /**
     * Returns the current wifi state as a String form an int.
     *
     * @param i Current wifi state.
     * @return
     */
    String getWifiState(int i) {
        String result;

        switch (i) {
            case WifiManager.WIFI_STATE_ENABLED:
                result = "enabled.";
                break;
            case WifiManager.WIFI_STATE_DISABLED:
                result = "disabled.";
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                result = "enabling.";
                break;
            case WifiManager.WIFI_STATE_DISABLING:
                result = "disabling";
                break;
            case WifiManager.WIFI_STATE_UNKNOWN:
                result = "unknown.";
                break;
            default:
                result = "Something went wrong";
                break;
        }
        return result;
    }

    /**
     * Method for getting the current used IP-Address.
     *
     * @return current Ip-Address.
     */
    String getIpAddress() {
        // Convert little-endian to big-endianif needed

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

        if (isLittleEndian) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }

        String ipAddressString;

        try {
            ipAddressString = InetAddress.getByAddress(BigInteger.valueOf(ipAddress).toByteArray()).getHostAddress();
        } catch (UnknownHostException ex) {
            Log.d(TAG, "Unable to get host address.");
            ipAddressString = "N/A";
        }

        return ipAddressString;
    }

    String getCurrentSSID(){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getSSID();
    }

}
