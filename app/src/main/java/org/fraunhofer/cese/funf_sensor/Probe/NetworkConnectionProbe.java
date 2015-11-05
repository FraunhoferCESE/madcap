
package org.fraunhofer.cese.funf_sensor.Probe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
 * A probetype that gives information about the wifi connection of the user.
 * Generally, this probe type is intent driven, but it also sends initial information upon creation.
 * I hope you believe in creation.
 */
public class NetworkConnectionProbe extends Probe.Base implements Probe.PassiveProbe {


    /**
     * Receiver to handle incoming intents
     */
    private BroadcastReceiver receiver;

    /**
     * only used in Log statements
     */
    private static final String TAG = "NetworkProbe: ";


    /**
     * Executed when probe is registered.
     * Sets up and registers the filter for wifi-specific intents.
     * Triggers the sending of an initial probeEntry.
     */
    @Override
    protected void onEnable() {
        super.onStart();
        receiver = new ConnectionInfoReceiver(this);

        //defining the filter
        IntentFilter intentFilter = new IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
//        intentFilter.addAction("android.net.wifi.RSSI_CHANGED");
        intentFilter.addAction("android.net.wifi.STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.NETWORK_STATE_CHANGED_ACTION");
        intentFilter.addAction("android.net.wifi.supplicant.STATE_CHANGED");

        getContext().registerReceiver(receiver, intentFilter);

        Log.i(TAG, "enabled.");

        sendInitialProbe();
    }

    /**
     * Executed when probe is unregistered.
     * Unregisters the related receiver.
     */
    @Override
    protected void onDisable() {
        super.onStop();
        getContext().unregisterReceiver(receiver);
    }

    /**
     * Sends an intent as a JsonObject.
     * Triggers the onDataReceived of the GoogleAppEnginePipeline.
     * @param intent
     */
    private void sendData(Intent intent) {
        sendData(getGson().toJsonTree(intent).getAsJsonObject());
    }

    /**
     * Creates an Intent object that contains the initial wifi state.
     * Uses sendData(Intent intent) to send the intent as a JsonObject.
     */
    private void sendInitialProbe() {

        Intent intent = new Intent();

        intent.putExtra("Initial cellular data network state: ", getCellDataState());
        intent.putExtra("Initial WifiState: ", getWifiState(intent));
        intent.putExtra("Initial connection quality: ", intent.getIntExtra(WifiManager.EXTRA_NEW_RSSI, 0));

        WifiManager wifiManager = (WifiManager)getContext().getSystemService(Context.WIFI_SERVICE);
        intent.putExtra("WifiInfo: ", wifiManager.getConnectionInfo().toString());
        intent.putExtra("IP-Address: ", getIpAddress(wifiManager.getConnectionInfo().getIpAddress()));
        intent.putExtra("SSID: ", wifiManager.getConnectionInfo().getSSID());
        sendData(intent);

        Log.i(TAG, "initial probe sent.");
    }



    private static final boolean isLittleEndian = ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN);
    private String getIpAddress(int ipAddress) {
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
        private final String STATE = "new Connection State: ";
        private final String PREVIOUS_STATE = "previous Connection State: ";

        /**
         * callback object to use the sendData(Intent intent) on.
         */
        public NetworkConnectionProbe callback;


        /**
         * Creator for an ConnectionInfoReceiver. Takes a NetworkConnectionProbe used for callbacks.
         * @param callback
         */
        public ConnectionInfoReceiver(NetworkConnectionProbe callback) {
            this.callback = callback;
        }

        /**
         * Called when one of the wifi specific intents occurs.
         * Adds additional information to the intent object and initialises the sending of that object
         * @param context
         * @param intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()) {
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
                    if (supplicantState.toString().equals("COMPLETED")){
                        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
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

            Log.i(TAG, "NetworkConnectionProbe sent");
        }
    }

    /**
     * Returns the state of the cellular network as a string for adding it to an intent.
     * @return cellular network state
     */
    private String getCellDataState(){

        String result;
        TelephonyManager telephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);

        switch(telephonyManager.getDataState()){
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
     * @param intent Current wifi state
     * @return
     */
    private String getWifiState(Intent intent){

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
     * @param intent
     * @return previous wifi state
     */
    private String getPreviousWifiState(Intent intent){

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
