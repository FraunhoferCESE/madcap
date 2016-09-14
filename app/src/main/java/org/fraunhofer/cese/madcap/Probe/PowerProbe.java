package org.fraunhofer.cese.madcap.Probe;

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
    private static int powerLevel;

    /**
     * Sets up the IntentFilter for the PowerProbe and sends an initial power status
     */
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

        sendInitialProbe();
        Log.i("PowerProbe: ", "Initial PowerProbe sent.");
    }


    private void sendData(Intent intent) {
        sendData(getGson().toJsonTree(intent).getAsJsonObject());
    }

    @Override
    protected void onDisable() {
        super.onStop();
        getContext().unregisterReceiver(receiver);
    }

    /**
     * Receiver-class for power-related intents
     */
    public class PowerInformationReceiver extends BroadcastReceiver {

        public PowerProbe callback;

        public PowerInformationReceiver(PowerProbe callback) {
            this.callback = callback;
        }

        /**
         * Adds information to the intent according to the intent itself and sends the intent
         * @param context
         * @param intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()) {
                case Intent.ACTION_POWER_CONNECTED:
                    String plug = "";
                    int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                    if (chargePlug == BatteryManager.BATTERY_PLUGGED_AC) {
                        plug = "AC";
                    } else if (chargePlug == BatteryManager.BATTERY_PLUGGED_USB) {
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
                    if ((intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)) != powerLevel) {
                        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                        powerLevel = level;
                        intent.putExtra("PowerProbe: ", "Battery at " + level + " %");
                        callback.sendData(intent);
                    }
                    break;
                default:
                    intent.putExtra("PowerProbe: ", "Something went wrong.");
                    callback.sendData(intent);
                    break;
            }
        }
    }

    /**
     * Sends the initial power information. Should only be called from onEnable().
     */
    private void sendInitialProbe() {

        Intent intent = new Intent();

        Intent batteryIntent = getContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = -1;
        int plugged = -1;
        if(batteryIntent != null) {
            level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            plugged = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        }
        intent.putExtra("Initial PowerLevel: ", level + " %");


        switch (plugged){
            case 0:
                intent.putExtra("Initial PlugState: ", "not plugged");
                break;
            case BatteryManager.BATTERY_PLUGGED_AC:
                intent.putExtra("Initial PlugState: ", "plugged to AC");
                break;
            case BatteryManager.BATTERY_PLUGGED_USB:
                intent.putExtra("Initial PlugState: ", "plugged to USB");
                break;
            default:
                intent.putExtra("Initial PlugState: ", "plugged to unknown");
                break;
        }

        sendData(intent);
    }
}
