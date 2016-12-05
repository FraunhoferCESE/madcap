package edu.umd.fcmd.sensorlisteners.listener.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import edu.umd.fcmd.sensorlisteners.NoSensorFoundException;
import edu.umd.fcmd.sensorlisteners.issuehandling.PermissionDeniedHandler;
import edu.umd.fcmd.sensorlisteners.listener.IntentFilterFactory;
import edu.umd.fcmd.sensorlisteners.listener.Listener;
import edu.umd.fcmd.sensorlisteners.model.BluetoothStateProbe;
import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;

import static java.security.AccessController.getContext;

/**
 * Created by MMueller on 12/2/2016.
 */

public class BluetoothListener implements Listener {
    private final String TAG = getClass().getSimpleName();

    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private ProbeManager<Probe> probeManager;
    private PermissionDeniedHandler permissionDeniedHandler;
    private BluetoothInformationReceiverFactory bluetoothInformationReceiverFactory;
    private IntentFilterFactory intenFilterFactory;
    private BluetoothInformationReceiver receiver;

    private boolean runningState;

    public BluetoothListener(Context context,
                             ProbeManager<Probe> probeManager,
                             BluetoothAdapter bluetoothAdapter,
                             PermissionDeniedHandler permissionDeniedHandler,
                             BluetoothInformationReceiverFactory bluetoothInformationReceiverFactory,
                             IntentFilterFactory intenFilterFactory) {
        this.context = context;
        this.bluetoothAdapter = bluetoothAdapter;
        this.probeManager = probeManager;
        this.permissionDeniedHandler = permissionDeniedHandler;
        this.bluetoothInformationReceiverFactory = bluetoothInformationReceiverFactory;
        this.intenFilterFactory = intenFilterFactory;
    }

    @Override
    public void onUpdate(Probe state) {
        probeManager.save(state);
    }

    @Override
    public void startListening() throws NoSensorFoundException {
        if(!runningState && bluetoothAdapter != null){
            receiver = bluetoothInformationReceiverFactory.create(this);
            IntentFilter intentFilter = intenFilterFactory.create();
            intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
            intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            intentFilter.addAction(BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED);
            intentFilter.addAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            intentFilter.addAction(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
            intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            context.registerReceiver(receiver, intentFilter);

            createInitialProbes();
        }
        runningState = true;
    }

    @Override
    public void stopListening() {
        if(runningState){
            context.unregisterReceiver(receiver);
            receiver = null;
        }
        runningState = false;
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    /**
     * Creates the initial probes.
     */
    private void createInitialProbes() {
        //Bluetooth State Probe
        BluetoothStateProbe bluetoothStateProbe = new BluetoothStateProbe();
        bluetoothStateProbe.setDate(System.currentTimeMillis());
        bluetoothStateProbe.setState(getState());
        onUpdate(bluetoothStateProbe);

    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    /**
     * Gets the state from the BluetoothAdapter
     * @return
     */
    public int getState(){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED){
            return bluetoothAdapter.getState();
        }else{
            permissionDeniedHandler.onPermissionDenied(Manifest.permission.BLUETOOTH);
            return 0;
        }
    }
}
