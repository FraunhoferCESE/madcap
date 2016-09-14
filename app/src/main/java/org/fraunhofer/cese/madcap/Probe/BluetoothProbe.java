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
    public static final String ON = "ON";
    public static final String TURNING_OFF = "Turning OFF";
    public static final String TURNING_ON = "Turning ON";
    static private BroadcastReceiver receiver;
    static private final String TAG = "BluetoothProbe: ";
    private final BluetoothAdapter bluetoothAdapter;

    private JsonObjectFactory jsonObjectFactory;
    private Intent lastSentIntent;


    public BluetoothProbe(BluetoothAdapter bluetoothAdapter, Context context, JsonObjectFactory jsonObjectFactory, BroadcastReceiver receiver) {
        super(context);
        this.bluetoothAdapter = bluetoothAdapter;
        this.jsonObjectFactory = jsonObjectFactory;
        this.receiver = receiver;
    }

    public BluetoothProbe(BluetoothAdapter bluetoothAdapter, Context context) {
        this(bluetoothAdapter, context, null, null);

    }

    public BluetoothProbe() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public Context getContext(){
        return super.getContext();
    }

    protected void onEnable() {

        super.onStart();

        receiver = new BluetoothInformationReceiver(this, this);

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

    public JsonObjectFactory getJsonObjectFactory() {
        return jsonObjectFactory;
    }

    public static BroadcastReceiver getReceiver() {
        return receiver;
    }

    protected void onDisable() {
        super.onStop();
        getContext().unregisterReceiver(receiver);
    }

    protected void sendData(Intent intent) {
        lastSentIntent = intent;
        if (jsonObjectFactory != null)
            sendData(jsonObjectFactory.createJsonObject(intent));
        else
            sendData(getGson().toJsonTree(intent).getAsJsonObject());

    }



    private static String getDeviceName(Intent intent) {
        BluetoothDevice device = intent
                .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        return device == null ? "N/A" : device.getName();
    }

    protected Intent getConnectionStateChangedInformation(Intent intent) {
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

    protected Intent getScanModeChangeInformation(Intent intent) {
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

    protected Intent getStateChangeInformation(Intent intent) {
        if (bluetoothAdapter != null) {
            intent.putExtra(TAG, "State changed.");

            switch (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)) {
                case BluetoothAdapter.STATE_OFF:
                    intent.putExtra("new State: ", OFF);
                    break;
                case BluetoothAdapter.STATE_ON:
                    intent.putExtra("new State: ", ON);
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    intent.putExtra("new State: ", TURNING_OFF);
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    intent.putExtra("new State: ", TURNING_ON);
                    break;
                default:
                    intent.putExtra("new State: ", intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0));
                    break;
            }

            switch (intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, 0)) {
                case BluetoothAdapter.STATE_OFF:
                    intent.putExtra("previous State: ", OFF);
                    break;
                case BluetoothAdapter.STATE_ON:
                    intent.putExtra("previous State: ", ON);
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    intent.putExtra("previous State: ", TURNING_OFF);
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    intent.putExtra("previous State: ", TURNING_ON);
                    break;
                default:
                    intent.putExtra("previous State: ", intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, 0));
                    break;
            }
        }
        return intent;
    }

    protected static String getTAG() {
        return TAG;
    }

    protected BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
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

    public void setReceiver(BluetoothInformationReceiver receiver) {
        this.receiver = receiver;
    }
}

