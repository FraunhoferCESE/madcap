package edu.umd.fcmd.sensorlisteners.listener.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import edu.umd.fcmd.sensorlisteners.model.BluetoothConnectionProbe;
import edu.umd.fcmd.sensorlisteners.model.BluetoothDiscoveryProbe;
import edu.umd.fcmd.sensorlisteners.model.BluetoothRequestProbe;
import edu.umd.fcmd.sensorlisteners.model.BluetoothScanModeProbe;
import edu.umd.fcmd.sensorlisteners.model.BluetoothStateProbe;
import edu.umd.fcmd.sensorlisteners.model.BluetoothStaticAttributesProbe;

import static android.bluetooth.BluetoothAdapter.EXTRA_CONNECTION_STATE;
import static android.content.ContentValues.TAG;

/**
 * Created by MMueller on 12/2/2016.
 */

public class BluetoothInformationReceiver extends BroadcastReceiver {
    private final String TAG = getClass().getSimpleName();
    private final BluetoothListener bluetoothListener;
    private final String STARTED = "STARTED";
    private final String FINISHED = "FINISHED";

    public BluetoothInformationReceiver(BluetoothListener bluetoothListener) {
        this.bluetoothListener = bluetoothListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (bluetoothListener.getBluetoothAdapter() != null) {
            switch (intent.getAction()) {
                case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:
                    Log.d(TAG, "Bluetooth Connection state changed.");
                    String state = intent.getStringExtra(EXTRA_CONNECTION_STATE);
                    //Bundle deviceBundle = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Bundle deviceBundle = intent.getExtras();
                    BluetoothDevice device = deviceBundle.getParcelable("android.bluetooth.BluetoothDevice");
                    BluetoothConnectionProbe bluetoothConnectionProbe = new BluetoothConnectionProbe();
                    bluetoothConnectionProbe.setDate(System.currentTimeMillis());
                    bluetoothConnectionProbe.setState(state);
                    if (device != null) {
                        bluetoothConnectionProbe.setForeignAddress(device.getAddress());
                        bluetoothConnectionProbe.setForeignName(device.getName());
                    }
                    bluetoothListener.onUpdate(bluetoothConnectionProbe);
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    Log.d(TAG, "Bluetooth Discovery started.");
                    BluetoothDiscoveryProbe bluetoothDiscoveryProbe = new BluetoothDiscoveryProbe();
                    bluetoothDiscoveryProbe.setDate(System.currentTimeMillis());
                    bluetoothDiscoveryProbe.setState(STARTED);
                    bluetoothListener.onUpdate(bluetoothDiscoveryProbe);
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Log.d(TAG, "Bluetooth Discovery started.");
                    BluetoothDiscoveryProbe bluetoothDiscoveryProbe2 = new BluetoothDiscoveryProbe();
                    bluetoothDiscoveryProbe2.setDate(System.currentTimeMillis());
                    bluetoothDiscoveryProbe2.setState(FINISHED);
                    bluetoothListener.onUpdate(bluetoothDiscoveryProbe2);
                    break;
                case BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED:
                    Log.d(TAG, "Bluetooth Name changed.");
                    BluetoothStaticAttributesProbe bluetoothStaticAttributesProbe = new BluetoothStaticAttributesProbe();
                    bluetoothStaticAttributesProbe.setDate(System.currentTimeMillis());
                    bluetoothStaticAttributesProbe.setAddress(bluetoothListener.getBluetoothAdapter().getAddress());
                    bluetoothStaticAttributesProbe.setName(bluetoothListener.getBluetoothAdapter().getName());
                    bluetoothListener.onUpdate(bluetoothStaticAttributesProbe);
                    break;
                case BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE:
                    Log.d(TAG, "Bluetooth discoverable requested.");
                    BluetoothRequestProbe bluetoothRequestProbe = new BluetoothRequestProbe();
                    bluetoothRequestProbe.setDate(System.currentTimeMillis());
                    bluetoothRequestProbe.setKind("DISCOVERABLE");
                    bluetoothListener.onUpdate(bluetoothRequestProbe);
                    break;
                case BluetoothAdapter.ACTION_REQUEST_ENABLE:
                    Log.d(TAG, "Bluetooth enable requested.");
                    BluetoothRequestProbe bluetoothRequestProbe2 = new BluetoothRequestProbe();
                    bluetoothRequestProbe2.setDate(System.currentTimeMillis());
                    bluetoothRequestProbe2.setKind("ENABLE");
                    bluetoothListener.onUpdate(bluetoothRequestProbe2);
                    break;
                case BluetoothAdapter.ACTION_SCAN_MODE_CHANGED:
                    Log.d(TAG, "Bluetooth scan mode changed.");
                    BluetoothScanModeProbe bluetoothScanModeProbe = new BluetoothScanModeProbe();
                    bluetoothScanModeProbe.setDate(System.currentTimeMillis());
                    switch (intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, 0)) {
                        case BluetoothAdapter.SCAN_MODE_NONE:
                            bluetoothScanModeProbe.setState("INVISIBLE");
                            break;
                        case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                            bluetoothScanModeProbe.setState("INVISABLE BUT CONNECTABLE");
                            break;
                        case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                            bluetoothScanModeProbe.setState("VISIBLE");
                            break;
                        default:
                            Log.d(TAG, "Not switched intent cached.");
                            break;
                    }
                    bluetoothListener.onUpdate(bluetoothScanModeProbe);
                    break;
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    Log.d(TAG, "Bluetooth State changed.");
                    BluetoothStateProbe bluetoothStateProbe = new BluetoothStateProbe();
                    bluetoothStateProbe.setDate(System.currentTimeMillis());
                    bluetoothStateProbe.setState(bluetoothListener.getState());
                    bluetoothListener.onUpdate(bluetoothStateProbe);
                    break;
                default:
                    Log.d(TAG, "Unknown Bluetooth intent caught.");
                    break;
            }
        }
    }

}

