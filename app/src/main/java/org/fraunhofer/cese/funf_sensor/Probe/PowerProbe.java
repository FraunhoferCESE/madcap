package org.fraunhofer.cese.funf_sensor.Probe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

import edu.mit.media.funf.probe.Probe;

/**
 * PowerProbe can tell the current battery level and if it's charging right now
 */
public class PowerProbe extends Probe.Base implements Probe.PassiveProbe {

    /**
     * the internal class PowerInform... is on the bottom of this class
     */
    private BroadcastReceiver receiver;
    private static int powerLevel=0;

    @Override
    protected void onEnable() {
        super.onStart();
        receiver = new PowerInformationReceiver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.ACTION_POWER_CONNECTED");
        intentFilter.addAction("android.intent.action.ACTION_POWER_DISCONNECTED");
        intentFilter.addAction("android.intent.action.BATTERY_LOW");
        intentFilter.addAction("android.intent.action.BATTERY_OKAY");
        intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
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

        public PowerInformationReceiver(PowerProbe callback) {
            this.callback = callback;
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()) {

                case Intent.ACTION_POWER_CONNECTED:

                    String plug = "";
                    int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                    if (chargePlug == BatteryManager.BATTERY_PLUGGED_AC) {
                        plug = "AC";
                    }else if(chargePlug == BatteryManager.BATTERY_PLUGGED_USB) {
                        plug = "USB";
                    }
                    intent.putExtra("PowerProbe: ", "now connected to" + plug);
                    callback.sendData(intent);
                    break;

                case Intent.ACTION_POWER_DISCONNECTED:

                    intent.putExtra("PowerProbe: ", "now disconnected");
                    callback.sendData(intent);
                    break;

                case Intent.ACTION_BATTERY_LOW:

                    intent.putExtra("PowerProbe: ", "Battery low!");
                    callback.sendData(intent);
                    break;

                case Intent.ACTION_BATTERY_OKAY:

                    intent.putExtra("PowerProbe: ", "Battery okay.");
                    callback.sendData(intent);
                    break;

                case Intent.ACTION_BATTERY_CHANGED:

                    if((intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1))!=powerLevel){
                        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                        powerLevel=level;
                        float batteryPct = level;
                        intent.putExtra("PowerProbe: ", "Battery at " + batteryPct +" %");
                        callback.sendData(intent);
                    }
                    break;

                default:

                    intent.putExtra("PowerProbe: ","Something went wrong.");
                    callback.sendData(intent);
                    break;
            }

//            intent = addChargingStatus(intent);
//            intent = addPlugStatus(intent);
//            intent = addBatteryLevel(intent);

//            callback.sendData(intent);


        }

//        private Intent addChargingStatus(Intent intent) {
//
//            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
//
//            if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
//                intent.putExtra("Battery Status: ", "Charging");
//            }
//
//            if (status == BatteryManager.BATTERY_STATUS_FULL) {
//                intent.putExtra("Battery Status: ", "Full");
//            }
//
//            return intent;
//        }
//
//        private Intent addPlugStatus(Intent intent) {
//
//            int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
//            if (chargePlug == BatteryManager.BATTERY_PLUGGED_AC) {
//                intent.putExtra("Plugged to: ", "AC");
//            }
//
//            if (chargePlug == BatteryManager.BATTERY_PLUGGED_USB) {
//                intent.putExtra("Plugged to: ", "USB");
//            }
//
//            return intent;
//        }
//
//        private Intent addBatteryLevel(Intent intent) {
//
//            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
//            float batteryPct = level;
//
//            intent.putExtra("Battery Status: ", batteryPct +" %");
//
//            return intent;
//        }
    }
}
