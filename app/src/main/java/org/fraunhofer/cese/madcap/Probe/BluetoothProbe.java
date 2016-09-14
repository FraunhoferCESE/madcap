package org.fraunhofer.cese.madcap.Probe;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import org.fraunhofer.cese.madcap.JsonObjectFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.mit.media.funf.probe.Probe;

/**
 *
 */
public class BluetoothProbe extends Probe.Base implements Probe.PassiveProbe {

    public static final String OFF = "off.";

    static private BroadcastReceiver receiver;
    static private final String TAG = "BluetoothProbe: ";
    private final BluetoothAdapter bluetoothAdapter;
    private JsonObjectFactory jsonObjectFactory;
    private Intent lastSentIntent;


    public BluetoothProbe(BluetoothAdapter bluetoothAdapter, Context context, JsonObjectFactory jsonObjectFactory) {
        super(context);
        this.bluetoothAdapter = bluetoothAdapter;
        this.jsonObjectFactory = jsonObjectFactory;
    }

    public BluetoothProbe(BluetoothAdapter bluetoothAdapter, Context context) {
        this(bluetoothAdapter, context, null);
    }

    public BluetoothProbe() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    protected void onEnable() {

        super.onStart();

        receiver = new BluetoothInformationReceiver(this);

        IntentFilter intentFilter = new IntentFilter();

        if (bluetoothAdapter != null) {
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

            Intent intent = new Intent();
            intent.putExtra(TAG, "Initial Probe!");
            intent.putExtra("State: ", getBluetoothState());
            intent.putExtra("Address: ", bluetoothAdapter.getAddress());
            intent.putExtra("Name: ", bluetoothAdapter.getName());
            Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
            List<String> bondedDeviceNames = new ArrayList<>();
            for (BluetoothDevice device : bondedDevices) {
                bondedDeviceNames.add(device.getName());
            }
            intent.putExtra("Bonded devices: ", bondedDeviceNames.toString());

            sendData(intent);

            Log.i(TAG, "Initial state sent");
        }

    }

    public Intent getLastSentIntent() {
        return lastSentIntent;
    }

    public class BluetoothInformationReceiver extends BroadcastReceiver {

        public BluetoothProbe callback;

        public BluetoothInformationReceiver(BluetoothProbe callback) {
            this.callback = callback;
        }

        @Override
        public void onReceive(Context context, Intent intent) {


//            List<BluetoothDevice> deviceList = ((BluetoothManager)context.getSystemService(Context.BLUETOOTH_SERVICE)).getConnectedDevices(BluetoothProfile.GATT);
//            deviceList.addAll(((BluetoothManager)context.getSystemService(Context.BLUETOOTH_SERVICE)).getConnectedDevices(BluetoothProfile.GATT_SERVER));
//            String devices = "";
//            if(deviceList.isEmpty()){
//                devices = "no devices.";
//            }
//            else {
//                for (BluetoothDevice bluetoothDevice : deviceList) {
//                    devices = devices + bluetoothDevice.getName() + ";; ";
//                }
//            }
//            intent.putExtra("Connected devices:", devices);
            if (bluetoothAdapter != null) {
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
                        intent.putExtra(TAG, intent.getAction());
                        callback.sendData(intent);
                        break;
                }
            }


        }
    }


    protected void onDisable() {
        super.onStop();
        getContext().unregisterReceiver(receiver);
    }

    private void sendData(Intent intent) {
        lastSentIntent = intent;
        if (jsonObjectFactory != null)
            sendData(jsonObjectFactory.createJsonObject(intent));
        else
            sendData(getGson().toJsonTree(intent).getAsJsonObject());

    }

    private static String getDeviceName(Intent intent) {
        BluetoothDevice device = intent
                .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        return (device == null) ? "N/A" : device.getName();
    }

    private Intent getConnectionStateChangedInformation(Intent intent) {
        if (bluetoothAdapter != null) {
            intent.putExtra(TAG, "ConnectionState changed");

            final int intExtra = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, 0);
            switch (intExtra) {
                case BluetoothAdapter.STATE_CONNECTED:
                    intent.putExtra("new ConnectionState: ", "connected");
                    intent.putExtra("connected device:", getDeviceName(intent));
                    break;
                case BluetoothAdapter.STATE_CONNECTING:
                    intent.putExtra("new ConnectionState: ", "connecting");
                    break;
                case BluetoothAdapter.STATE_DISCONNECTED:
                    intent.putExtra("new ConnectionState: ", "disconnected");
                    intent.putExtra("disconnected device:", getDeviceName(intent));
                    break;
                case BluetoothAdapter.STATE_DISCONNECTING:
                    intent.putExtra("new ConnectionState: ", "cacheClosing");
                    break;
                default:
                    intent.putExtra("new ConnectionState: ", intExtra);
                    break;
            }

            int extraPrevious = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_CONNECTION_STATE, 0);
            switch (extraPrevious) {
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
                    intent.putExtra("previous ConnectionState: ", extraPrevious);
                    break;
            }
        }
        return intent;
    }

    private Intent getScanModeChangeInformation(Intent intent) {
        if (bluetoothAdapter != null) {
            intent.putExtra(TAG, "ScanMode changed");

            switch (intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, 0)) {
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
                    intent.putExtra("new ScanMode: ", intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, 0));
                    break;
            }

            switch (intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_SCAN_MODE, 0)) {
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
                    intent.putExtra("previous ScandMode: ", intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_SCAN_MODE, 0));
                    break;
            }
        }
        return intent;
    }

    private Intent getStateChangeInformation(Intent intent) {
        if (bluetoothAdapter != null) {
            intent.putExtra(TAG, "State changed.");

            switch (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)) {
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
                    intent.putExtra("new State: ", intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0));
                    break;
            }

            switch (intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, 0)) {
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
                    intent.putExtra("previous State: ", intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, 0));
                    break;
            }
        }
        return intent;
    }


    private String getBluetoothState() {
        String result = "";
        if (bluetoothAdapter != null) {
            switch (bluetoothAdapter.getState()) {
                case BluetoothAdapter.STATE_OFF:
                    result = OFF;
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
                    result = Integer.toString(bluetoothAdapter.getState());
                    break;
            }
        }
        return result;
    }
}

