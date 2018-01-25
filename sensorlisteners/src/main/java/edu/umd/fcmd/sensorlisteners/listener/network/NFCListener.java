package edu.umd.fcmd.sensorlisteners.listener.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import edu.umd.fcmd.sensorlisteners.listener.Listener;
import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.model.network.NetworkProbeFactory;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;

/**
 * Provides a listener for near-field communication turning on/off
 */
public class NFCListener extends BroadcastReceiver implements Listener {

    private boolean isRunning;

    private final Context mContext;
    private final ProbeManager<Probe> probeManager;
    @Nullable private final NfcAdapter nfcAdapter;
    private final NetworkProbeFactory factory;

    @Inject
    NFCListener (Context context, ProbeManager<Probe> probeManager, @Nullable NfcAdapter nfcAdapter, NetworkProbeFactory factory) {
        mContext = context;
        this.probeManager = probeManager;
        this.nfcAdapter = nfcAdapter;
        this.factory = factory;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        int state = intent.getIntExtra(NfcAdapter.EXTRA_ADAPTER_STATE, -1);
        System.out.print(context);
        System.out.print(intent);
        if (state == NfcAdapter.STATE_OFF || state == NfcAdapter.STATE_ON) {
            onUpdate(factory.createNfcProbe(intent));
        }
    }

    @Override
    public void onUpdate(Probe state) {
        probeManager.save(state);
    }

    @Override
    public void startListening() {
        if (!isRunning) {
            mContext.registerReceiver(this, new IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED));
            onUpdate(factory.createNfcProbe(nfcAdapter));
            isRunning = true;
        }
    }

    @Override
    public void stopListening() {
        if (isRunning) {
            mContext.unregisterReceiver(this);
            isRunning = false;
        }

    }

    @Override
    public boolean isPermittedByUser() {
        return true;
    }
}
