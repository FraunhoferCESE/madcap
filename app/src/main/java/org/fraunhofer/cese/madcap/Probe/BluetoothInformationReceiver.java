package org.fraunhofer.cese.madcap.Probe;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by MMueller on 9/14/2016.
 */
public class BluetoothInformationReceiver extends BroadcastReceiver {

    private BluetoothProbe bluetoothProbe;
    public BluetoothProbe callback;

    public BluetoothInformationReceiver(BluetoothProbe bluetoothProbe, BluetoothProbe callback) {
        this.bluetoothProbe = bluetoothProbe;
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
        if (bluetoothProbe.getBluetoothAdapter() != null) {
            switch (intent.getAction()) {
                case BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED:
                    intent = BluetoothProbe.getConnectionStateCInformation(intent);
                    callback.sendData(intent);
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    intent.putExtra(BluetoothProbe.getTAG(), "searching for remote devices.");
                    callback.sendData(intent);
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    intent.putExtra(BluetoothProbe.getTAG(), "search for devices finished.");
                    callback.sendData(intent);
                    break;
                case BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED:
                    intent.putExtra(BluetoothProbe.getTAG(), "adapter name changed");
                    intent.putExtra("new name: ", bluetoothProbe.getBluetoothAdapter().getName());
                    callback.sendData(intent);
                    break;
                case BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE:
                    intent.putExtra(BluetoothProbe.getTAG(), "discoverability requested.");
                    callback.sendData(intent);
                    break;
                case BluetoothAdapter.ACTION_REQUEST_ENABLE:
                    intent.putExtra(BluetoothProbe.getTAG(), "user asked to enable Bluetooth");
                    callback.sendData(intent);
                    break;
                case BluetoothAdapter.ACTION_SCAN_MODE_CHANGED:
                    intent = BluetoothProbe.getScanModeChangeInformation(intent);
                    callback.sendData(intent);
                    break;
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    intent = bluetoothProbe.getStateChangeInformation(intent);
                    callback.sendData(intent);
                    break;
                default:
                    intent.putExtra(BluetoothProbe.getTAG(), intent.getAction());
                    callback.sendData(intent);
                    break;
            }
        }


    }
}
