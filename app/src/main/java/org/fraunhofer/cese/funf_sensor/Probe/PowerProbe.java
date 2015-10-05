package org.fraunhofer.cese.funf_sensor.Probe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

import edu.mit.media.funf.probe.Probe;

/**
 *PowerProbe can tell the current battery level and if it's charging right now
 */
public class PowerProbe extends Probe.Base implements Probe.PassiveProbe {

    private static final String TAG = "PowerProbe";

    /**
     * the internal class PowerInform... is on the bottom of this class
     */
    private BroadcastReceiver receiver;

    @Override
    protected void onEnable() {
        super.onStart();
        receiver = new PowerInformationReceiver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.ACTION_POWER_CONNECTED");
        intentFilter.addAction("android.intent.action.ACTION_POWER_DISCONNECTED");
        intentFilter.addAction("android.intent.action.ACTION_BATTERY_LOW");
        intentFilter.addAction("android.intent.action.ACTION_BATTERY_OKAY");
        intentFilter.addAction("android.intent.action.ACTION_BATTERY_CHANGED");
        getContext().registerReceiver(receiver, intentFilter);
        Log.i("PowerProbe.class: ", "PowerProbe enabled");

    }

    private void sendData(Intent intent) {
        sendData(getGson().toJsonTree(intent).getAsJsonObject());
        Log.i("PowerProbe.class: ", "PowerProbe sent");
    }

    @Override
    protected void onDisable() {
        super.onStop();
        getContext().unregisterReceiver(receiver);
    }


    public class PowerInformationReceiver extends BroadcastReceiver {

        public PowerProbe callback;
        public PowerInformationReceiver (PowerProbe callback){this.callback=callback;}

        @Override
        public void onReceive(Context context, Intent intent) {

            intent = addChargingStatus(intent);
            intent = addPlugStatus(intent);
            intent = addBatteryLevel(intent);

            callback.sendData(intent);


        }

        private Intent addChargingStatus(Intent intent) {

            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

            if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                intent.putExtra("Battery Status: ", "Charging");
            }

            if (status == BatteryManager.BATTERY_STATUS_FULL) {
                intent.putExtra("Battery Status: ", "Full");
            }

            return intent;
        }

        private Intent addPlugStatus(Intent intent) {

            int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            if (chargePlug == BatteryManager.BATTERY_PLUGGED_AC) {
                intent.putExtra("Plugged to: ", "AC");
            }

            if (chargePlug == BatteryManager.BATTERY_PLUGGED_USB) {
                intent.putExtra("Plugged to: ", "USB");
            }

            return intent;
        }

        private Intent addBatteryLevel(Intent intent) {

            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

            float batteryPct = level / (float) scale;

            intent.putExtra("Battery Status: ", batteryPct + "%");

            return intent;
        }
    }
}
