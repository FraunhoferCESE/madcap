package org.fraunhofer.cese.funf_sensor.Probe;

import edu.mit.media.funf.probe.Probe;

import android.content.BroadcastReceiver;
import android.util.Log;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import com.google.gson.JsonObject;

import java.util.Set;

/**
 *
 */
public class BluetoothProbe extends Probe.Base implements Probe.PassiveProbe {

    static private BroadcastReceiver receiver;
    static private final String TAG = "BluetoothProbe: ";
    static private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    protected void onEnable() {

        super.onStart();

        receiver = new BluetoothInformationReceiver(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intentFilter.addAction(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        getContext().registerReceiver(receiver, intentFilter);

        Log.i(TAG, "BluetoothProbe enabled.");

        sendInitialData();
    }

    public class BluetoothInformationReceiver extends BroadcastReceiver {

        public BluetoothProbe callback;

        public BluetoothInformationReceiver(BluetoothProbe callback) {
            this.callback = callback;
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            Set<BluetoothDevice> deviceSet = bluetoothAdapter.getBondedDevices();
            String devices = "";
            if(deviceSet.isEmpty()){
                devices = "no devices.";
            }
            else {
                for (BluetoothDevice bluetoothDevice : deviceSet) {
                    devices = devices + bluetoothDevice.getName() + " ";
                }
            }
            intent.putExtra("Connected devices:", devices);

            switch (intent.getAction()) {
                case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:
                    intent = getConnectionStateChangedInformation(intent);
                    callback.sendData(intent);
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    intent.putExtra(TAG, "searching for remote devices.");
                    callback.sendData(intent);
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    intent.putExtra(TAG, "search for devices finished.");
                    callback.sendData(intent);
                    break;
                case BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED:
                    intent.putExtra(TAG, "adapter name changed");
                    intent.putExtra("new name: ", bluetoothAdapter.getName());
                    callback.sendData(intent);
                    break;
                case BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE:
                    intent.putExtra(TAG, "discoverability requested.");
                    callback.sendData(intent);
                    break;
                case BluetoothAdapter.ACTION_REQUEST_ENABLE:
                    intent.putExtra(TAG, "user asked to enable Bluetooth");
                    callback.sendData(intent);
                    break;
                case BluetoothAdapter.ACTION_SCAN_MODE_CHANGED:
                    intent = getScanModeChangeInformation(intent);
                    callback.sendData(intent);
                    break;
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    intent = getStateChangeInformation(intent);
                    callback.sendData(intent);
                    break;
                default:
                    intent.putExtra(TAG, "Something went wrong");
                    callback.sendData(intent);
                    break;
            }

        }
    }

    protected void onDisable() {
        super.onStop();
        getContext().unregisterReceiver(receiver);
    }

    private void sendData(Intent intent) {
        sendData(getGson().toJsonTree(intent).getAsJsonObject());
    }

    private Intent getConnectionStateChangedInformation (Intent intent){

        intent.putExtra(TAG, "ConnectionState changed");

        switch (intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, 0)) {
            case BluetoothAdapter.STATE_CONNECTED:
                intent.putExtra("new ConnectionState: ", "connected");
                break;
            case BluetoothAdapter.STATE_CONNECTING:
                intent.putExtra("new ConnectionState: ", "connecting");
                break;
            case BluetoothAdapter.STATE_DISCONNECTED:
                intent.putExtra("new ConnectionState: ", "disconnected");
                break;
            case BluetoothAdapter.STATE_DISCONNECTING:
                intent.putExtra("new ConnectionState: ", "cacheClosing");
                break;
            default:
                intent.putExtra("new ConnectionState: ", "Something went wrong.");
                break;
        }

        switch (intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_CONNECTION_STATE, 0)) {
            case BluetoothAdapter.STATE_CONNECTED:
                intent.putExtra("previous ConnectionState: ", "connected");
                break;
            case BluetoothAdapter.STATE_CONNECTING:
                intent.putExtra("previous ConnectionState: ", "connecting");
                break;
            case BluetoothAdapter.STATE_DISCONNECTED:
                intent.putExtra("previous ConnectionState: ", "disconnected");
                break;
            case BluetoothAdapter.STATE_DISCONNECTING:
                intent.putExtra("previous ConnectionState: ", "cacheClosing");
                break;
            default:
                intent.putExtra("previous ConnectionState: ", "Something went wrong.");
                break;
        }

        return intent;
    }

    private Intent getScanModeChangeInformation(Intent intent){

        intent.putExtra(TAG, "ScanMode changed");

        switch (intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, 0)){
            case BluetoothAdapter.SCAN_MODE_NONE:
                intent.putExtra("new ScanMode: ", "invisible");
                break;
            case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                intent.putExtra("new ScanMode: ", "invisible, but connectable");
                break;
            case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                intent.putExtra("new ScanMode: ", "visible and connectable");
                break;
            default:
                intent.putExtra("new ScanMode: ", "Something went wrong.");
                break;
        }

        switch (intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_SCAN_MODE, 0)){
            case BluetoothAdapter.SCAN_MODE_NONE:
                intent.putExtra("previous ScanMode: ", "invisible");
                break;
            case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                intent.putExtra("previous ScanMode: ", "invisible, but connectable");
                break;
            case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                intent.putExtra("previous ScanMode: ", "visible and connectable");
                break;
            default:
                intent.putExtra("previous ScandMode: ", "Something went wrong.");
                break;
        }

        return intent;
    }

    private Intent getStateChangeInformation (Intent intent) {

        intent.putExtra(TAG, "State changed.");

        switch (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,0)){
            case BluetoothAdapter.STATE_OFF:
                intent.putExtra("new State: ", "OFF");
                break;
            case BluetoothAdapter.STATE_ON:
                intent.putExtra("new State: ", "ON");
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                intent.putExtra("new State: ", "Turning OFF");
                break;
            case BluetoothAdapter.STATE_TURNING_ON:
                intent.putExtra("new State: ", "Turning ON");
                break;
            default:
                intent.putExtra("new State: ", "Something went wrong");
                break;
        }

        switch (intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE,0)){
            case BluetoothAdapter.STATE_OFF:
                intent.putExtra("previous State: ", "OFF");
                break;
            case BluetoothAdapter.STATE_ON:
                intent.putExtra("previous State: ", "ON");
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                intent.putExtra("previous State: ", "Turning OFF");
                break;
            case BluetoothAdapter.STATE_TURNING_ON:
                intent.putExtra("previous State: ", "Turning ON");
                break;
            default:
                intent.putExtra("previous State: ", "Something went wrong");
                break;
        }

        return intent;
    }

    private void sendInitialData(){

        Intent intent = new Intent();

        intent.putExtra(TAG, "Initial Probe!");
        intent.putExtra("State: ", getBluetoothState());
        intent.putExtra("Address: ", bluetoothAdapter.getAddress());
        intent.putExtra("Name: ", bluetoothAdapter.getName());
        intent.putExtra("Bonded devices: ", bluetoothAdapter.getBondedDevices().toString());

        sendData(intent);

        Log.i(TAG, "Initial state sent");
    }

    private String getBluetoothState(){
        String result;
        switch (bluetoothAdapter.getState()) {
            case BluetoothAdapter.STATE_OFF:
                result = "off.";
                break;
            case BluetoothAdapter.STATE_ON:
                result = "on";
                break;
            case BluetoothAdapter.STATE_TURNING_ON:
                result = "turning on.";
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                result = "turning off.";
                break;
            default:
                result = "Something went wrong.";
                break;
        }
        return result;
    }
}