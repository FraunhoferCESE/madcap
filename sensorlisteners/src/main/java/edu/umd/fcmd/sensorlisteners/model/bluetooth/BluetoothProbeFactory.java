package edu.umd.fcmd.sensorlisteners.model.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;

import javax.inject.Inject;

import timber.log.Timber;

import static android.bluetooth.BluetoothAdapter.EXTRA_CONNECTION_STATE;

/**
 * Factory class for creating bluetooth-related probes
 */
@SuppressWarnings("MethodMayBeStatic")
public class BluetoothProbeFactory {

    /**
     * Default constructor needed for dependency injection with Dagger2
     */
    @Inject
    public BluetoothProbeFactory() {
    }

    /**
     * Probe capturing the state of the Bluetooth adapter
     *
     * @param state {@link BluetoothAdapter#getState()}
     * @return a new Bluetooth state probe
     */
    public BluetoothStateProbe createBluetoothStateProbe(int state) {
        BluetoothStateProbe bluetoothStateProbe = new BluetoothStateProbe();
        bluetoothStateProbe.setDate(System.currentTimeMillis());
        bluetoothStateProbe.setState(state);
        return bluetoothStateProbe;
    }

    /**
     * Creates a probe containing information on which bluetooth device the phone was connected to
     *
     * @param intent see {@link BluetoothAdapter#ACTION_CONNECTION_STATE_CHANGED}
     * @return a probe containing the bluetooth device connected
     */
    public BluetoothConnectionProbe createBluetoothConnectionProbe(Intent intent) {
        BluetoothConnectionProbe probe = new BluetoothConnectionProbe();

        probe.setDate(System.currentTimeMillis());
//        probe.setState(intent.getStringExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE));
//        probe.setState(intent.getStringExtra(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED));

        int stateInt = intent.getExtras().getInt(BluetoothAdapter.EXTRA_CONNECTION_STATE);
//        Timber.d("BT Connection probe State: "+stateInt);
        switch (stateInt) {
            case 0:
                probe.setState(BluetoothConnectionProbe.DISCONNECTED);
                break;
            case 1:
                probe.setState(BluetoothConnectionProbe.CONNECTING);
                break;
            case 2:
                probe.setState(BluetoothConnectionProbe.CONNECTED);
                break;
            case 3:
                probe.setState(BluetoothConnectionProbe.DISCONNECTING);
                break;
            default:
                probe.setState(BluetoothConnectionProbe.UNKNOWN);
                break;
        }

        BluetoothDevice device = intent.getExtras().getParcelable(BluetoothDevice.EXTRA_DEVICE);
        if (device != null) {
            probe.setForeignAddress(device.getAddress());
            probe.setForeignName(device.getName());
        }
        Timber.d("BT probe/Connection: "+probe.toString());
        Timber.d("BT probe/Connection/probe: "+probe);
        return probe;
    }


    /**
     * Creates a probe that records changes in the Bluetooth discovery process
     *
     * @param intent see {@link BluetoothAdapter#ACTION_DISCOVERY_STARTED} and {@link BluetoothAdapter#ACTION_DISCOVERY_FINISHED}
     * @return a new bluetooth discovery probe
     */
    public BluetoothDiscoveryProbe createBluetoothDiscoveryProbe(Intent intent) {
        BluetoothDiscoveryProbe probe = new BluetoothDiscoveryProbe();
        probe.setDate(System.currentTimeMillis());
        switch (intent.getAction()) {
            case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                probe.setState(BluetoothDiscoveryProbe.STARTED);
                break;
            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                probe.setState(BluetoothDiscoveryProbe.FINISHED);
                break;
            default:
                probe.setState(BluetoothDiscoveryProbe.UNKNOWN);
        }
        Timber.d("BT probe/Discovery: "+probe.toString());
        return probe;
    }

    /**
     * Creates a probe to record actions that request changes to the Bluetooth discoverability state
     *
     * @param intent the intent that launched the action. See {@link BluetoothAdapter#ACTION_REQUEST_DISCOVERABLE} and {@link BluetoothAdapter#ACTION_REQUEST_ENABLE}
     * @return a new Bluetooth discovery probe
     */
    public BluetoothRequestProbe createBluetoothRequestProbe(Intent intent) {
        BluetoothRequestProbe probe = new BluetoothRequestProbe();
        probe.setDate(System.currentTimeMillis());
        switch (intent.getAction()) {
            case BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE:
                probe.setKind(BluetoothRequestProbe.DISCOVERABLE);
                break;
            case BluetoothAdapter.ACTION_REQUEST_ENABLE:
                probe.setKind(BluetoothRequestProbe.ENABLE);
                break;
            default:
                probe.setKind(BluetoothRequestProbe.UNKNOWN);
        }
        Timber.d("BT probe/Request: "+probe.toString());
        return probe;
    }

    /**
     * Creates a probe that captures changed in the Bluetooth adapter's scanning mode
     *
     * @param intent see {@link BluetoothAdapter#ACTION_SCAN_MODE_CHANGED}
     * @return a new probe
     */
    public BluetoothScanModeProbe createBluetoothScanModeProbe(Intent intent) {
        BluetoothScanModeProbe probe = new BluetoothScanModeProbe();
        probe.setDate(System.currentTimeMillis());
        switch (intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, 0)) {
            case BluetoothAdapter.SCAN_MODE_NONE:
                probe.setState(BluetoothScanModeProbe.INVISIBLE);
                break;
            case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                probe.setState(BluetoothScanModeProbe.CONNECTABLE);
                break;
            case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                probe.setState(BluetoothScanModeProbe.VISIBLE);
                break;
            default:
                probe.setState(BluetoothScanModeProbe.UNKNOWN);
        }
        Timber.d("BT probe/Scan mode : "+probe.toString());
        return probe;
    }
}
