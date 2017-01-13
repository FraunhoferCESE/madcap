package edu.umd.fcmd.sensorlisteners.listener.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.provider.VoicemailContract;
import android.util.Log;

import edu.umd.fcmd.sensorlisteners.model.network.NetworkProbe;
import edu.umd.fcmd.sensorlisteners.model.network.VoicemailProbe;
import edu.umd.fcmd.sensorlisteners.model.network.WiFiProbe;

import static android.content.ContentValues.TAG;

/**
 * Created by MMueller on 12/2/2016.
 *
 * Receiver for the Network Connections
 */
public class ConnectionInfoReceiver extends BroadcastReceiver {
    private final NetworkListener networkListener;

    ConnectionInfoReceiver(NetworkListener networkListener){
        this.networkListener = networkListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case ConnectivityManager.CONNECTIVITY_ACTION:
                // Any network connection changed
                networkListener.onUpdate(createCurrentNetworkProbe(intent));
                //networkListener.onUpdate(createNewCellularProbe());
                break;
            case WifiManager.WIFI_STATE_CHANGED_ACTION:
                // WIFI enabled/disabled/enabling/disabling/... change
                WiFiProbe wiFiProbe1 = new WiFiProbe();
                wiFiProbe1.setDate(System.currentTimeMillis());
                wiFiProbe1.setIp(networkListener.getIpAddress());
                wiFiProbe1.setState(networkListener.getWifiState(intent));
                wiFiProbe1.setNetworkSecurity(networkListener.getCurrentSecurityLevel());
                wiFiProbe1.setSsid(networkListener.getCurrentSSID());
                networkListener.onUpdate(wiFiProbe1);
                break;
            case WifiManager.NETWORK_STATE_CHANGED_ACTION:
                // WiFi Network connection changed
                WiFiProbe wiFiProbe3 = new WiFiProbe();
                wiFiProbe3.setDate(System.currentTimeMillis());
                wiFiProbe3.setIp(networkListener.getIpAddress());
                wiFiProbe3.setState(networkListener.getWifiState(intent));
                wiFiProbe3.setNetworkSecurity(networkListener.getCurrentSecurityLevel());
                wiFiProbe3.setSsid(networkListener.getCurrentSSID());
                if(intent.hasExtra(WifiManager.EXTRA_NETWORK_INFO)){
                    NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    wiFiProbe3.setNetworkState(networkInfo.toString());
                }
                networkListener.onUpdate(wiFiProbe3);
                //networkListener.onUpdate(createNewCellularProbe());
                networkListener.onUpdate(createCurrentNetworkProbe(intent));
                break;
            case WifiManager.SUPPLICANT_STATE_CHANGED_ACTION:
                // wifi trying to establish a connection changed.
                WiFiProbe wiFiProbe4 = new WiFiProbe();
                wiFiProbe4.setDate(System.currentTimeMillis());
                wiFiProbe4.setIp(networkListener.getIpAddress());
                wiFiProbe4.setState(networkListener.getWifiState(intent));
                wiFiProbe4.setNetworkSecurity(networkListener.getCurrentSecurityLevel());
                wiFiProbe4.setSsid(networkListener.getCurrentSSID());
                if(intent.hasExtra(WifiManager.EXTRA_NETWORK_INFO)){
                    NetworkInfo networkInfo2 = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    wiFiProbe4.setNetworkState(networkInfo2.toString());
                }
                networkListener.onUpdate(wiFiProbe4);
                //networkListener.onUpdate(createNewCellularProbe());
                break;
            case VoicemailContract.ACTION_FETCH_VOICEMAIL:
                VoicemailProbe voicemailProbeFetch = new VoicemailProbe();
                voicemailProbeFetch.setKind(VoicemailProbe.FETCH_VOICEMAIL);
                networkListener.onUpdate(voicemailProbeFetch);
                break;
            case VoicemailContract.ACTION_NEW_VOICEMAIL:
                VoicemailProbe voicemailProbeNew = new VoicemailProbe();
                voicemailProbeNew.setKind(VoicemailProbe.NEW_VOICEMAIL);
                networkListener.onUpdate(voicemailProbeNew);
                break;
            default:
                Log.e(TAG, "something went wrong.");
                break;
        }
    }

    /**
     * Creates a Network Probe.
     * @return the created Probe.
     */
    private static NetworkProbe createCurrentNetworkProbe(Intent intent){
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
