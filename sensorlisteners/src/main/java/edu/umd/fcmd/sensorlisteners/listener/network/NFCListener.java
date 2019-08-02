package edu.umd.fcmd.sensorlisteners.listener.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.tech.NfcA;
import android.net.wifi.WifiManager;
import android.os.Build;
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
        NfcManager nfcMan = (NfcManager) mContext.getSystemService(Context.NFC_SERVICE);
        this.nfcAdapter = nfcMan.getDefaultAdapter();
        this.factory = factory;
        this.isRunning = false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        onUpdate(factory.createNfcProbe(intent));
    }

    @Override
    public void onUpdate(Probe state) {
        probeManager.save(state);
    }

    @Override
    public void startListening() {
        if (!isRunning) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED);
            intentFilter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
            intentFilter.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
            intentFilter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                // needs android API level 28 at least
                intentFilter.addAction(NfcAdapter.ACTION_TRANSACTION_DETECTED);
            }

            //mContext.registerReceiver(this, new IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED));
            mContext.registerReceiver(this, intentFilter);

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
