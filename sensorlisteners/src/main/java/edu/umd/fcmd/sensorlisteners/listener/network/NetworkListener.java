package edu.umd.fcmd.sensorlisteners.listener.network;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.List;

import edu.umd.fcmd.sensorlisteners.NoSensorFoundException;
import edu.umd.fcmd.sensorlisteners.issuehandling.PermissionDeniedHandler;
import edu.umd.fcmd.sensorlisteners.listener.Listener;
import edu.umd.fcmd.sensorlisteners.model.network.CallStateProbe;
import edu.umd.fcmd.sensorlisteners.model.network.CellLocationProbe;
import edu.umd.fcmd.sensorlisteners.model.network.CellProbe;
import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.model.network.WiFiProbe;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by MMueller on 12/2/2016.
 *
 * Listener Class for everything network related like:
 *
 * Internet, Calls, SMS
 *
 */
public class NetworkListener implements Listener {
    private final String TAG = getClass().getSimpleName();
    private boolean runningStatus;

    private BroadcastReceiver receiver;
    private Context context;
    private ProbeManager<Probe> probeProbeManager;
    private ConnectionInfoReceiverFactory connectionInfoReceiverFactory;
    private PermissionDeniedHandler permissionDeniedHandler;
    private TelephonyManager telephonyManager;
    private TelephonyListenerFactory telephonyListenerFactory;
    private TelephonyListener telephonyListener;

    private static final boolean isLittleEndian = ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN);

    public NetworkListener(Context context,
                           ProbeManager<Probe> probeProbeManager,
                           ConnectionInfoReceiverFactory connectionInfoReceiverFactory,
                           TelephonyListenerFactory telephonyListenerFactory,
                           PermissionDeniedHandler permissionDeniedHandler){
        this.context = context;
        this.probeProbeManager = probeProbeManager;
        this.connectionInfoReceiverFactory = connectionInfoReceiverFactory;
        this.telephonyListenerFactory = telephonyListenerFactory;
        this.permissionDeniedHandler = permissionDeniedHandler;
    }


    @Override
    public void onUpdate(Probe state) {
        probeProbeManager.save(state);
    }

    @Override
    public void startListening() throws NoSensorFoundException {
        if(!runningStatus){
            instanciateNetworkReceiver(this, context);
            instaciateTelephonyListener();

            sendInitalProbes();
        }

        runningStatus = true;
    }

    @Override
    public void stopListening() {
        if(runningStatus){
            context.unregisterReceiver(receiver);
            telephonyManager.listen(telephonyListener, PhoneStateListener.LISTEN_NONE);
        }
        runningStatus = false;
    }

    @Override
    public boolean isRunning() {
        return runningStatus;
    }

    private void instaciateTelephonyListener() {
        telephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);

        telephonyListener = telephonyListenerFactory.create(context, this);

        telephonyManager.listen(telephonyListener,
                PhoneStateListener.LISTEN_CALL_STATE
                        | PhoneStateListener.LISTEN_CELL_INFO
                        | PhoneStateListener.LISTEN_CELL_LOCATION
                        | PhoneStateListener.LISTEN_DATA_ACTIVITY
                        | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE
                        | PhoneStateListener.LISTEN_SERVICE_STATE
                        | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                        | PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR
                        | PhoneStateListener.LISTEN_MESSAGE_WAITING_INDICATOR);

    }

    /**
     * Instanciation of the receiver.
     * Seperation of this into another method due to reduction of complexity and
     * for better testing.
     * @param networkListener
     * @param context
     */
    private void instanciateNetworkReceiver(NetworkListener networkListener, Context context){
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
        //WiFi
        WiFiProbe wiFiProbe = new WiFiProbe();
        wiFiProbe.setDate(System.currentTimeMillis());
        wiFiProbe.setIp(getIpAddress());
        wiFiProbe.setNetworkSecurity(getCurrentWiFiInfo());
        wiFiProbe.setState(getWifiState(((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getWifiState()));
        wiFiProbe.setSsid(getCurrentSSID());

        onUpdate(wiFiProbe);

        //Cell
        CellProbe cellProbe = telephonyListener.createNewCellularProbe();

        onUpdate(cellProbe);

        //TelecomService
        ServiceState serviceState;

        //CallState
        CallStateProbe callStateProbe = telephonyListener.createNewCallStateProbe(telephonyManager.getCallState(), "");

        onUpdate(callStateProbe);

        //CellLocation
        CellLocation cellLoc = telephonyManager.getCellLocation();
        CellLocationProbe cellLocationProbe = telephonyListener.createCellLocationProbe(cellLoc);
        onUpdate(cellLocationProbe);
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
                Log.d(TAG, "Something went wrong");
                result = "-";
                break;
        }
        return result;
    }

    String getCurrentSecurityLevel() {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> networkList = wifi.getScanResults();

        //get current connected SSID for comparison to ScanResult
        WifiInfo wi = wifi.getConnectionInfo();
        String currentSSID = wi.getSSID();

        //Log.d(TAG, "Own ssid "+currentSSID);

        if (networkList != null) {
            for (ScanResult network : networkList) {
                //check if current connected SSID.
                //Log.d(TAG, "Other ssid "+network.SSID);
                if (currentSSID.equals("\""+network.SSID+"\"")) {
                    //get capabilities of current connection
                    String capabilities = network.capabilities;
                    Log.d(TAG, network.SSID + " capabilities : " + capabilities);

                    if (capabilities.contains("WPA2")) {
                        return  "WPA2";
                    } else if (capabilities.contains("WPA")) {
                        return "WPA2";
                    } else if (capabilities.contains("WEP")) {
                        return "WEP";
                    }else if(capabilities.contains("Open")){
                        return "OPEN";
                    }
                }
            }
        }
        return "-";
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
                Log.d(TAG, "Something went wrong");
                result = "-";
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
