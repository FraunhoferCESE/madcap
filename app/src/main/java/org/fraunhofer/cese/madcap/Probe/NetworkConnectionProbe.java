package org.fraunhofer.cese.madcap.Probe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

import edu.mit.media.funf.probe.Probe;

/**
 *
 */
public class NetworkConnectionProbe extends Probe.Base implements Probe.PassiveProbe {

    private BroadcastReceiver receiver;
    private static final String TAG = "NetworkProbe: ";


    @Override
    protected void onEnable() {
        onStart();
        receiver = new ConnectionInfoReceiver(this);
        IntentFilter intentFilter = new IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
//        intentFilter.addAction("android.net.wifi.RSSI_CHANGED");
        intentFilter.addAction("android.net.wifi.STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.NETWORK_STATE_CHANGED_ACTION");
        intentFilter.addAction("android.net.wifi.supplicant.STATE_CHANGED");
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.net.conn.CONNECTIVITY_ACTION");

        getContext().registerReceiver(receiver, intentFilter);

        Log.i(TAG, "enabled.");

        sendInitialProbe();
    }

    @Override
    protected void onDisable() {
        onStop();
        getContext().unregisterReceiver(receiver);
    }

    private void sendData(Intent intent) {
        sendData(getGson().toJsonTree(intent).getAsJsonObject());
    }

    private void sendInitialProbe() {

        Intent intent = new Intent();

        intent.putExtra("Initial cellular data network state: ", getCellDataState());
        intent.putExtra("Initial WifiState: ", getWifiState(intent));
        intent.putExtra("Initial connection quality: ", intent.getIntExtra(WifiManager.EXTRA_NEW_RSSI, 0));
        WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        intent.putExtra("WifiInfo: ", wifiInfo.toString());
        intent.putExtra("IP-Address: ", getIpAddress(wifiManager.getConnectionInfo().getIpAddress()));
        intent.putExtra("SSID: ", wifiInfo.getSSID());

        sendData(intent);

        Log.i(TAG, "initial probe sent.");
    }

    private static final boolean isLittleEndian = ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN);

    private static String getIpAddress(int ipAddress) {
        // Convert little-endian to big-endianif needed

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

    /**
     * A specific receiver class for wifi- and network-related intents.
     */
    private class ConnectionInfoReceiver extends BroadcastReceiver {
        /**
         * callback object to use the sendData(Intent intent) on.
         */
        public NetworkConnectionProbe callback;


        /**
         * Creator for an ConnectionInfoReceiver. Takes a NetworkConnectionProbe used for callbacks.
         *
         * @param callback
         */
        ConnectionInfoReceiver(NetworkConnectionProbe callback) {
            this.callback = callback;
        }

        @Override
        public void onReceive(Context context, Intent intent) {


            switch (intent.getAction()) {
                case ConnectivityManager.CONNECTIVITY_ACTION:
                    intent.putExtra("new Connectivity state: ", intent.getStringExtra(ConnectivityManager.EXTRA_EXTRA_INFO));
                    intent.putExtra("noConnectivity: ", intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false));
                    intent.putExtra("cellular data network state: ", getCellDataState());
                    callback.sendData(intent);
                    break;
                case WifiManager.WIFI_STATE_CHANGED_ACTION:
                    intent.putExtra("New Wifi State: ", getWifiState(intent));
                    intent.putExtra("Previous Wifi State: ", getPreviousWifiState(intent));
                    intent.putExtra("cellular data network state: ", getCellDataState());
                    callback.sendData(intent);
                    break;
                case WifiManager.RSSI_CHANGED_ACTION:
                    intent.putExtra("new connection quality: ", intent.getIntExtra(WifiManager.EXTRA_NEW_RSSI, 0));
                    intent.putExtra("cellular data network state: ", getCellDataState());
                    callback.sendData(intent);
                    break;
                case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                    NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    intent.putExtra("new network state: ", networkInfo.toString());
                    intent.putExtra("cellular data network state: ", getCellDataState());
                    callback.sendData(intent);
                    break;
                case WifiManager.SUPPLICANT_STATE_CHANGED_ACTION:
                    SupplicantState supplicantState = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
                    intent.putExtra("new supplicant state: ", supplicantState.toString());
                    intent.putExtra("cellular data network state: ", getCellDataState());
                    if (supplicantState.toString().equals("COMPLETED")) {
                        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                        intent.putExtra("WifiInfo: ", wifiInfo.toString());
                        intent.putExtra("IP-Address: ", getIpAddress(wifiInfo.getIpAddress()));
                        intent.putExtra("SSID: ", wifiInfo.getSSID());
                    }
                    callback.sendData(intent);
                    break;
                default:
                    intent.putExtra(TAG, "something went wrong.");
                    callback.sendData(intent);
                    break;
            }
        }
    }

    /**
     * Returns the state of the cellular network as a string for adding it to an intent.
     *
     * @return cellular network state
     */
    private String getCellDataState() {

        String result;
        TelephonyManager telephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);

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
    private String getWifiState(Intent intent) {

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

    /**
     * Returns the wifi state from before a change as a String for adding that information to an intent object.
     *
     * @param intent
     * @return previous wifi state
     */
    private String getPreviousWifiState(Intent intent) {

        String result;

        switch (intent.getIntExtra(WifiManager.EXTRA_PREVIOUS_WIFI_STATE, 0)) {
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

}
