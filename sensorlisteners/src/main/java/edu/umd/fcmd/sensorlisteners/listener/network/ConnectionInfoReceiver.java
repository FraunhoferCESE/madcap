package edu.umd.fcmd.sensorlisteners.listener.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

import edu.umd.fcmd.sensorlisteners.model.CellularProbe;
import edu.umd.fcmd.sensorlisteners.model.NetworkProbe;
import edu.umd.fcmd.sensorlisteners.model.WiFiProbe;

import static android.content.ContentValues.TAG;

/**
 * Created by MMueller on 12/2/2016.
 */

public class ConnectionInfoReceiver extends BroadcastReceiver {
    private NetworkListener networkListener;

    ConnectionInfoReceiver(NetworkListener networkListener){
        this.networkListener = networkListener;
    }

    /**
     * This method is called when the BroadcastReceiver is receiving an Intent
     * broadcast.  During this time you can use the other methods on
     * BroadcastReceiver to view/modify the current result values.  This method
     * is always called within the main thread of its process, unless you
     * explicitly asked for it to be scheduled on a different thread using
     * {@link Context#registerReceiver(BroadcastReceiver,
     * IntentFilter, String, Handler)}. When it runs on the main
     * thread you should
     * never perform long-running operations in it (there is a timeout of
     * 10 seconds that the system allows before considering the receiver to
     * be blocked and a candidate to be killed). You cannot launch a popup dialog
     * in your implementation of onReceive().
     * <p>
     * <p><b>If this BroadcastReceiver was launched through a &lt;receiver&gt; tag,
     * then the object is no longer alive after returning from this
     * function.</b>  This means you should not perform any operations that
     * return a result to you asynchronously -- in particular, for interacting
     * with services, you should use
     * {@link Context#startService(Intent)} instead of
     * {@link Context#bindService(Intent, ServiceConnection, int)}.  If you wish
     * to interact with a service that is already running, you can use
     * {@link #peekService}.
     * <p>
     * <p>The Intent filters used in {@link Context#registerReceiver}
     * and in application manifests are <em>not</em> guaranteed to be exclusive. They
     * are hints to the operating system about how to find suitable recipients. It is
     * possible for senders to force delivery to specific recipients, bypassing filter
     * resolution.  For this reason, {@link #onReceive(Context, Intent) onReceive()}
     * implementations should respond only to known actions, ignoring any unexpected
     * Intents that they may receive.
     *
     * @param context The Context in which the receiver is running.
     * @param intent  The Intent being received.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case ConnectivityManager.CONNECTIVITY_ACTION:
                networkListener.onUpdate(createCurrentNetworkProbe(intent));
                break;
            case WifiManager.WIFI_STATE_CHANGED_ACTION:
                WiFiProbe wiFiProbe = new WiFiProbe();
                wiFiProbe.setDate(System.currentTimeMillis());
                wiFiProbe.setIp(networkListener.getIpAddress());
                wiFiProbe.setState(networkListener.getWifiState(intent));
                wiFiProbe.setInfo(networkListener.getCurrentWiFiInfo());
                wiFiProbe.setSsid(networkListener.getCurrentSSID());
                networkListener.onUpdate(wiFiProbe);
                networkListener.onUpdate(createCurrentCellularProbe());
                break;
            case WifiManager.RSSI_CHANGED_ACTION:
                WiFiProbe wiFiProbe2 = new WiFiProbe();
                wiFiProbe2.setDate(System.currentTimeMillis());
                wiFiProbe2.setIp(networkListener.getIpAddress());
                wiFiProbe2.setState(networkListener.getWifiState(intent));
                wiFiProbe2.setInfo(networkListener.getCurrentWiFiInfo());
                wiFiProbe2.setQuality(intent.getIntExtra(WifiManager.EXTRA_NEW_RSSI, 0)+"");
                wiFiProbe2.setSsid(networkListener.getCurrentSSID());
                networkListener.onUpdate(wiFiProbe2);
                networkListener.onUpdate(createCurrentCellularProbe());
                break;
            case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                WiFiProbe wiFiProbe3 = new WiFiProbe();
                wiFiProbe3.setDate(System.currentTimeMillis());
                wiFiProbe3.setIp(networkListener.getIpAddress());
                wiFiProbe3.setState(networkListener.getWifiState(intent));
                wiFiProbe3.setInfo(networkListener.getCurrentWiFiInfo());
                wiFiProbe3.setSsid(networkListener.getCurrentSSID());
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                wiFiProbe3.setNetworkState(networkInfo.toString());
                networkListener.onUpdate(wiFiProbe3);
                networkListener.onUpdate(createCurrentCellularProbe());
                networkListener.onUpdate(createCurrentNetworkProbe(intent));
                break;
            case WifiManager.SUPPLICANT_STATE_CHANGED_ACTION:
                WiFiProbe wiFiProbe4 = new WiFiProbe();
                wiFiProbe4.setDate(System.currentTimeMillis());
                wiFiProbe4.setIp(networkListener.getIpAddress());
                wiFiProbe4.setState(networkListener.getWifiState(intent));
                wiFiProbe4.setInfo(networkListener.getCurrentWiFiInfo());
                wiFiProbe4.setQuality(intent.getIntExtra(WifiManager.EXTRA_NEW_RSSI, 0)+"");
                wiFiProbe4.setSsid(networkListener.getCurrentSSID());
                NetworkInfo networkInfo2 = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                wiFiProbe4.setNetworkState(networkInfo2.toString());
                networkListener.onUpdate(wiFiProbe4);
                networkListener.onUpdate(createCurrentCellularProbe());
                break;
            default:
                Log.e(TAG, "something went wrong.");
                break;
        }
    }

    /**
     * Creates a Cellular Probe.
     * @return the created Probe.
     */
    private CellularProbe createCurrentCellularProbe(){
        CellularProbe cellularProbe = new CellularProbe();
        cellularProbe.setDate(System.currentTimeMillis());
        cellularProbe.setState(networkListener.getCellDataState());

        return cellularProbe;
    }

    /**
     * Creates a Network Probe.
     * @return the created Probe.
     */
    private NetworkProbe createCurrentNetworkProbe(Intent intent){
        NetworkProbe networkProbe = new NetworkProbe();
        networkProbe.setDate(System.currentTimeMillis());
        networkProbe.setInfo(intent.getStringExtra(ConnectivityManager.EXTRA_EXTRA_INFO));
        if(!intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)){
            networkProbe.setState(NetworkProbe.CONNECTED);
        }else{
            networkProbe.setState(NetworkProbe.NOT_CONNECTED);
        }

        return networkProbe;
    }
}