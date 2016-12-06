package edu.umd.fcmd.sensorlisteners.listener.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import edu.umd.fcmd.sensorlisteners.model.BluetoothStateProbe;
import edu.umd.fcmd.sensorlisteners.model.BluetoothStaticAttributesProbe;

import static android.content.ContentValues.TAG;

/**
 * Created by MMueller on 12/2/2016.
 */

public class BluetoothInformationReceiver extends BroadcastReceiver {
    private final BluetoothListener bluetoothListener;

    public BluetoothInformationReceiver(BluetoothListener bluetoothListener) {
        this.bluetoothListener = bluetoothListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (bluetoothListener.getBluetoothAdapter() != null) {
            switch (intent.getAction()) {
                case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:
//                    intent = bluetoothListener.getConnectionStateCInformation(intent);
//                    sendData(intent);
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
//                    intent.putExtra(bluetoothListener.getTAG(), "searching for remote devices.");
//                    sendData(intent);
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
//                    intent.putExtra(bluetoothListener.getTAG(), "search for devices finished.");
//                    sendData(intent);
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
//                    intent.putExtra(bluetoothListener.getTAG(), "discoverability requested.");
//                    sendData(intent);
                    break;
                case BluetoothAdapter.ACTION_REQUEST_ENABLE:
//                    intent.putExtra(bluetoothListener.getTAG(), "user asked to enable Bluetooth");
//                    sendData(intent);
                    break;
                case BluetoothAdapter.ACTION_SCAN_MODE_CHANGED:
//                    intent = bluetoothListener.getScanModeChangeInformation(intent);
//                    sendData(intent);
                    break;
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    Log.d(TAG, "Bluetooth State changed.");
                    BluetoothStateProbe bluetoothStateProbe = new BluetoothStateProbe();
                    bluetoothStateProbe.setDate(System.currentTimeMillis());
                    bluetoothStateProbe.setState(bluetoothListener.getState());
                    bluetoothListener.onUpdate(bluetoothStateProbe);
                    break;
                default:
//                    intent.putExtra(bluetoothListener.getTAG(), intent.getAction());
//                    sendData(intent);
                    break;
            }
        }
    }

}

