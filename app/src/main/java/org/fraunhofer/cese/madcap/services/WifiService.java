package org.fraunhofer.cese.madcap.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.inject.Inject;

import edu.umd.fcmd.sensorlisteners.model.network.NetworkProbe;
import edu.umd.fcmd.sensorlisteners.model.network.WiFiProbe;
import timber.log.Timber;



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





    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
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

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
   // private final IBinder mBinder = new LocalBinder();
    private final IBinder mBinder = new LocalBinder();


    /**
     * Creates a NetworkProbe that captures general network connection/disconnection activity.
     *
     * @param intent An Intent created from {@link ConnectivityManager#CONNECTIVITY_ACTION}
     * @return a new NetworkProbe
     */
    @NonNull
    public NetworkProbe createNetworkProbe(@NonNull Intent intent) {

        NetworkProbe networkProbe = new NetworkProbe();
        networkProbe.setDate(System.currentTimeMillis());
        networkProbe.setInfo(intent.getStringExtra(ConnectivityManager.EXTRA_EXTRA_INFO));
        if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
            networkProbe.setState(NetworkProbe.NOT_CONNECTED);
        } else {
            networkProbe.setState(NetworkProbe.CONNECTED);
        }
        return networkProbe;
    }


    /**
     * Creates a NetworkProbe that captures general network connection/disconnection activity based on the current network
     *
     * @param networkInfo from {@link ConnectivityManager#getActiveNetworkInfo()}
     * @return a new NetworkProbe
     */



    @NonNull
    public NetworkProbe createNetworkProbe(@Nullable NetworkInfo networkInfo) {
        NetworkProbe networkProbe = new NetworkProbe();
        networkProbe.setDate(System.currentTimeMillis());
        if (networkInfo == null) {
            networkProbe.setState(NetworkProbe.NOT_CONNECTED);
            networkProbe.setInfo(null);
        } else {
            networkProbe.setState(NetworkProbe.CONNECTED);
            networkProbe.setInfo(networkInfo.getExtraInfo());
        }

        return networkProbe;
    }

    @NonNull
    public WiFiProbe createWiFiProbe(int ipAddress, String currentSsid, Iterable<ScanResult> networkList, int wifiState) {

        WiFiProbe wiFiProbe = new WiFiProbe();
        wiFiProbe.setDate(System.currentTimeMillis());
        wiFiProbe.setSsid(currentSsid);

       // checkWifiSecurity(networkList);
        // Convert IP address from int to string
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.order(ByteOrder.nativeOrder());
        byteBuffer.putInt(ipAddress);
        String ipAddressString;
        try {
            ipAddressString = InetAddress.getByAddress(byteBuffer.array()).getHostAddress();
        } catch (UnknownHostException ex) {
            Timber.d("Unable to convert host address", ex);
            //noinspection HardcodedFileSeparator
            ipAddressString = "N/A";
        }
        wiFiProbe.setIp(ipAddressString);

        //get current connected SSID for comparison to ScanResult
        String security = "-";

        for (ScanResult network : networkList) {

            //check if current connected SSID.
            if (currentSsid.equals('\"' + network.SSID + '\"')) {
                //get capabilities of current connection
                String capabilities = network.capabilities;

                //noinspection IfStatementWithTooManyBranches
                if (capabilities.toUpperCase().contains("WPA2")) {
                    security = "WPA2";
                } else if (capabilities.toUpperCase().contains("WPA")) {
                    security = "WPA";

                } else if (capabilities.toUpperCase().contains("WEP")) {
                    security = "WEP";
                } else if (capabilities.toUpperCase().contains("OPEN")) {
                    security = "OPEN";
                } else if (capabilities.toUpperCase().contains("WPS")){
                    security = "WPS";
                  //  startService(new Intent(this, openWifiNotification.class));
                } else {
                    security = capabilities;
                }
            }
        }
        if(!(security == "OPEN")){
            context.startService(new Intent(context, NotificationService.class));
        }
        wiFiProbe.setNetworkSecurity(security);

        // Convert wifi state integer to string
        String state;
        switch (wifiState) {
            case WifiManager.WIFI_STATE_ENABLED:
                state = "ENABLED";
                break;
            case WifiManager.WIFI_STATE_DISABLED:
                state = "DISABLED";
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                state = "ENABLING";
                break;
            case WifiManager.WIFI_STATE_DISABLING:
                state = "DISABLING";
                break;
            case WifiManager.WIFI_STATE_UNKNOWN:
                state = "UNKNOWN";
                break;
            default:
                state = "-";
                break;
        }
        wiFiProbe.setState(state);

        return wiFiProbe;
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
    

}

