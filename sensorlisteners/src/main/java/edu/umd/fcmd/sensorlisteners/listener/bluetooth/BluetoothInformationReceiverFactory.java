package edu.umd.fcmd.sensorlisteners.listener.bluetooth;

/**
 * Created by MMueller on 12/5/2016.
 *
 * Factory for BluetoothInformationReceiver following the well
 * known factory pattern.
 */

public class BluetoothInformationReceiverFactory {

    /**
     * Create method.
     *
     * @param bluetoothListener the Listener.
     * @return a new instance.
     */
    public BluetoothInformationReceiver create(BluetoothListener bluetoothListener){
        return new BluetoothInformationReceiver(bluetoothListener);
    }
}
