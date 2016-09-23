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
import java.util.Collection;
import java.util.Set;

import edu.mit.media.funf.probe.Probe.Base;
import edu.mit.media.funf.probe.Probe.PassiveProbe;

/**
 *
 */
public class BluetoothProbe extends Base implements PassiveProbe {

    public static final String OFF = "OFF";
    public static final String ON = "ON";
    public static final String TURNING_OFF = "Turning OFF";
    public static final String TURNING_ON = "Turning ON";
    private static final String TAG = "BluetoothProbe: ";
    private static BroadcastReceiver receiver;
    private final BluetoothAdapter bluetoothAdapter;
    private JsonObjectFactory jsonObjectFactory;
    private Intent lastSentIntent;


    /**
     * Constructor only for testing purposes.
     * @param bluetoothAdapter BluetoothAdapter being used.
     * @param context Context being used.
     * @param jsonObjectFactory jsonObjectFactory being used.
     * @param receiver Receiver being used.
     */
    public BluetoothProbe(BluetoothAdapter bluetoothAdapter, Context context, JsonObjectFactory jsonObjectFactory, BroadcastReceiver receiver) {
        super(context);
        this.bluetoothAdapter = bluetoothAdapter;
        this.jsonObjectFactory = jsonObjectFactory;
        BluetoothProbe.receiver = receiver;
    }

    /**
     * Constructor only for testing purposes.
     * @param bluetoothAdapter BluetoothAdapter being used.
     * @param context Context being used.
     */
    public BluetoothProbe(BluetoothAdapter bluetoothAdapter, Context context) {
        this(bluetoothAdapter, context, null, null);
    }

    /**
     * Constructor actually in use. Takes the default bluetooth adapter of the device as bluetoothadapter.
     */
    public BluetoothProbe() {
        super();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * Getter for Receiver.
     * @return current bond receiver.
     */
    public static BroadcastReceiver getReceiver() {
        return receiver;
    }

    /**
     * Setter for receiver.
     * @param receiver BluetoothInformationReceiver to be set.
     */
    public static void setReceiver(BluetoothInformationReceiver receiver) {
        BluetoothProbe.receiver = receiver;
    }

    /**
     * Gets tje current device name for intent.
     * @param intent to get the device name from.
     * @return "N/A" if no device bond, else the devices name.
     */
    private static String getDeviceName(Intent intent) {
        BluetoothDevice device = intent
                .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        return (device == null) ? "N/A" : device.getName();
    }

    /**
     * Getter for the TAG
     * @return TAG
     */
    protected static String getTAG() {
        return TAG;
    }

    /**
     * {@inheritDoc}
     *
     * @return context.
     */
    @Override
    public final Context getContext() {
        return super.getContext();
    }

    /**
     * {@inheritDoc}
     * Instanciates new BluetoothInformationReceiver context.
     * Registers the receiver with additional information (
     * ACTION_CONNECTION_STATE_CHANGED,
     * ACTION_DISCOVERY_STARTED,
     * ACTION_DISCOVERY_FINISHED,
     * ACTION_LOCAL_NAME_CHANGED,
     * ACTION_REQUEST_DISCOVERABLE,
     * ACTION_REQUEST_ENABLE,
     * ACTION_SCAN_MODE_CHANGED,
     * ACTION_STATE_CHANGED) as an intent filter.
     * Puts extra information (
     * TAG, "Initial Probe!",
     * "State: ",
     * "Address: ",
     * "Name: ",
     * "Bonded devices: ")
     * into the intent which is send by sendData(intent).
     */
    @Override
    protected final void onEnable() {
        onStart();

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
            Collection<String> bondedDeviceNames = new ArrayList<>();
            for (BluetoothDevice device : bondedDevices) {
                bondedDeviceNames.add(device.getName());
            }
            intent.putExtra("Bonded devices: ", bondedDeviceNames.toString());

            sendData(intent);
            lastSentIntent = intent;

            Log.i(TAG, "Initial state sent");
        }

    }

    /**
     * Getter for cached intent.
     * @return the last sent intent.
     */
    public final Intent getLastSentIntent() {
        return lastSentIntent;
    }

    /**
     * Unregisters the BluetoothInformationReceiver.
     */
    @Override
    protected final void onDisable() {
        onStop();
        if(bluetoothAdapter != null){
            getContext().unregisterReceiver(receiver);
        }

    }

    /**
     * Calls inherited funf method for sending data.
     * @param intent with the data to be send.
     */
    protected final void sendData(Intent intent) {
        lastSentIntent = intent;
        if (jsonObjectFactory != null) {
            sendData(jsonObjectFactory.createJsonObject(intent));
        } else
        // could not be tested due to funf
        {
            sendData(getGson().toJsonTree(intent).getAsJsonObject());
        }

    }

    /**
     * Gets connection state changed information.
     * Collects them and adds them to the intent
     * @param intent to get the connection state changed information from.
     * @return intent with additional connection state
     * changed information.
     */
    protected static Intent getConnectionStateCInformation(Intent intent) {
        intent.putExtra(TAG, "ConnectionState changed");
        int intExtra = intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, 0);
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
        return intent;
    }

    /**
     * Gets scan state changed information.
     * Collects them and adds them to the intent
     * @param intent to get the scan state changed information from.
     * @return intent with additional information
     * about the scan change.
     */
    protected static Intent getScanModeChangeInformation(Intent intent) {
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

        return intent;
    }

    /**
     * Gets  state changed information.
     * Collects them and adds them to the intent.
     * @param intent to get the state changed information from.
     * @return intent with additional state
     * changed information.
     */
    protected final Intent getStateChangeInformation(Intent intent) {
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

        return intent;
    }

    /**
     * Getter for the current bond bluetoot adapter.
     * @return current bond bluetoot adapter
     */
    protected final BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    protected final String getBluetoothState() {
        String result = "";
        if (bluetoothAdapter != null) {
            switch (bluetoothAdapter.getState()) {
                case BluetoothAdapter.STATE_OFF:
                    result = OFF;
                    break;
                case BluetoothAdapter.STATE_ON:
                    result = ON;
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    result = TURNING_ON;
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    result = TURNING_OFF;
                    break;
                default:
                    result = Integer.toString(bluetoothAdapter.getState());
                    break;
            }
        }
        return result;
    }
}

