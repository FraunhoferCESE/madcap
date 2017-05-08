package edu.umd.fcmd.sensorlisteners.listener.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import javax.inject.Inject;

import edu.umd.fcmd.sensorlisteners.listener.Listener;
import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.model.network.NetworkProbe;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;
import timber.log.Timber;

/**
 * Created by MMueller on 12/2/2016.
 * <p>
 * Receiver for the Network Connections
 */
public class ConnectivityListener extends BroadcastReceiver implements Listener {

    private boolean isRunning;
    private final Context mContext;
    private final ProbeManager<Probe> probeManager;

    @Inject
    ConnectivityListener(Context context, ProbeManager<Probe> probeManager) {
        mContext = context;
        this.probeManager = probeManager;
    }

    @Override
    public void onUpdate(Probe state) {
        probeManager.save(state);
    }

    @Override
    public void startListening() {
        if (!isRunning) {
            Timber.d("startListening");
            mContext.registerReceiver(this, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            isRunning = true;
        }
    }

    @Override
    public void stopListening() {
        if (isRunning) {
            Timber.d("stopListener");
            mContext.unregisterReceiver(this);
            isRunning = false;
        }
    }

    @Override
    public boolean isPermittedByUser() {
        return true;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        NetworkProbe networkProbe = new NetworkProbe();
        networkProbe.setDate(System.currentTimeMillis());
        networkProbe.setInfo(intent.getStringExtra(ConnectivityManager.EXTRA_EXTRA_INFO));
        if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
            networkProbe.setState(NetworkProbe.NOT_CONNECTED);
        } else {
            networkProbe.setState(NetworkProbe.CONNECTED);
        }
        onUpdate(networkProbe);

    }
}
