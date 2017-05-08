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
import edu.umd.fcmd.sensorlisteners.model.network.NFCProbe;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;

/**
 * Provides a listener for near-field communication turning on/off
 */
public class NFCListener extends BroadcastReceiver implements Listener {

    private boolean isRunning;

    private final Context mContext;
    private final ProbeManager<Probe> probeManager;
    @Nullable private final NfcAdapter nfcAdapter;

    @Inject
    NFCListener(Context context, ProbeManager<Probe> probeManager, @Nullable NfcAdapter nfcAdapter) {
        mContext = context;
        this.probeManager = probeManager;
        this.nfcAdapter = nfcAdapter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        NFCProbe nfcProbe = new NFCProbe();
        nfcProbe.setState(getCurrentNFCState());
        nfcProbe.setDate(System.currentTimeMillis());
        onUpdate(nfcProbe);
    }

    @Override
    public void onUpdate(Probe state) {
        probeManager.save(state);

    }

    @Override
    public void startListening() {
        if (!isRunning) {
            mContext.registerReceiver(this, new IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED));

            // NFC
            NFCProbe nfcProbe = new NFCProbe();
            nfcProbe.setDate(System.currentTimeMillis());
            nfcProbe.setState(getCurrentNFCState());
            onUpdate(nfcProbe);

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


    /**
     * Gets the NFC state of the device.
     *
     * @return nfc state.
     */
    String getCurrentNFCState() {
        return ((nfcAdapter != null) && nfcAdapter.isEnabled()) ? NFCProbe.ON : NFCProbe.OFF;
    }
}
