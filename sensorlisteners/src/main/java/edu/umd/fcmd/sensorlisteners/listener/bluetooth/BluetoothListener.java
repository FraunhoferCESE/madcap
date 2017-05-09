package edu.umd.fcmd.sensorlisteners.listener.bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import javax.inject.Inject;

import edu.umd.fcmd.sensorlisteners.listener.Listener;
import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.model.bluetooth.BluetoothProbeFactory;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;
import timber.log.Timber;

/**
 * Created by MMueller on 12/2/2016.
 * <p>
 * BluetoothListener listening to certain bluetooth events.
 */
public class BluetoothListener extends BroadcastReceiver implements Listener {

    private final Context mContext;
    private final BluetoothAdapter bluetoothAdapter;
    private final ProbeManager<Probe> probeManager;
    private final BluetoothProbeFactory factory;

    private boolean runningState;

    @Inject
    public BluetoothListener(Context context,
                             ProbeManager<Probe> probeManager,
                             BluetoothAdapter bluetoothAdapter,
                             BluetoothProbeFactory factory) {
        mContext = context;
        this.bluetoothAdapter = bluetoothAdapter;
        this.probeManager = probeManager;
        this.factory = factory;
    }

    @Override
    public void onUpdate(Probe state) {
        probeManager.save(state);
    }

    @SuppressLint("HardwareIds")
    @Override
    public void startListening() {
        if (!runningState && (bluetoothAdapter != null) && isPermittedByUser()) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
            intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            intentFilter.addAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            intentFilter.addAction(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
            intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            mContext.registerReceiver(this, intentFilter);


            // Send initial probes
            onUpdate(factory.createBluetoothStateProbe(bluetoothAdapter.getState()));

            runningState = true;
        }
    }

    @Override
    public void stopListening() {
        if (runningState) {
            mContext.unregisterReceiver(this);
            runningState = false;
        }
    }

    @Override
    public boolean isPermittedByUser() {
        //non dangerous permission
        return true;
    }

    @SuppressLint("HardwareIds")
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:
                onUpdate(factory.createBluetoothConnectionProbe(intent));
                break;
            case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                onUpdate(factory.createBluetoothDiscoveryProbe(intent));
                break;
            case BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE:
            case BluetoothAdapter.ACTION_REQUEST_ENABLE:
                onUpdate(factory.createBluetoothRequestProbe(intent));
                break;
            case BluetoothAdapter.ACTION_SCAN_MODE_CHANGED:
                onUpdate(factory.createBluetoothScanModeProbe(intent));
                break;
            case BluetoothAdapter.ACTION_STATE_CHANGED:
                onUpdate(factory.createBluetoothStateProbe(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)));
                break;
            default:
                Timber.d("Unknown Bluetooth intent caught.");
                break;
        }

    }
}
