package edu.umd.fcmd.sensorlisteners.listener.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import javax.inject.Inject;

import edu.umd.fcmd.sensorlisteners.listener.Listener;
import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.model.network.NetworkProbeFactory;
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
    private final NetworkProbeFactory factory;
    private final ConnectivityManager connectivityManager;

    @Inject
    ConnectivityListener(Context context, ProbeManager<Probe> probeManager, NetworkProbeFactory factory, ConnectivityManager connectivityManager) {
        mContext = context;
        this.probeManager = probeManager;
        this.factory = factory;
        this.connectivityManager = connectivityManager;
    }

    @Override
    public void onUpdate(Probe state) {
        probeManager.save(state);
    }

    @Override
    public void startListening() {
        if (!isRunning) {
            Timber.d("startListening");
            onUpdate(factory.createNetworkProbe(connectivityManager.getActiveNetworkInfo()));
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
        onUpdate(factory.createNetworkProbe(intent));
    }
}
