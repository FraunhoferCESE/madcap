package edu.umd.fcmd.sensorlisteners.listener.power;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

import edu.umd.fcmd.sensorlisteners.model.ChargingProbe;
import edu.umd.fcmd.sensorlisteners.model.PowerProbe;

/**
 * Created by MMueller on 12/12/2016.
 *
 * A broadcast receiver for handeling battery related events.
 * Pluggin in an out of any charger and changes in actual battery state.
 */
public class PowerInformationReceiver extends BroadcastReceiver {
    private final String TAG = getClass().getSimpleName();

    public PowerListener powerListener;
    private int powerLevel;

    public PowerInformationReceiver(PowerListener powerListener, int initialPowerLevel) {
        this.powerListener = powerListener;
        powerLevel = initialPowerLevel;
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
                ChargingProbe chargingProbe = new ChargingProbe();
                chargingProbe.setDate(System.currentTimeMillis());
                chargingProbe.setCharging(getPluggedState(context, intent));
                powerListener.onUpdate(chargingProbe);
                break;
            case Intent.ACTION_POWER_DISCONNECTED:
                ChargingProbe chargingProbe2 = new ChargingProbe();
                chargingProbe2.setDate(System.currentTimeMillis());
                chargingProbe2.setCharging(ChargingProbe.NONE);
                powerListener.onUpdate(chargingProbe2);
                break;
            case Intent.ACTION_BATTERY_CHANGED:
                if (intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) != powerLevel) {
                    int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                    int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                    PowerProbe powerProbe = new PowerProbe();
                    powerProbe.setDate(System.currentTimeMillis());
                    powerProbe.setRemainingPower(level / (double)scale);
                    powerProbe.setVoltage(intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1));
                    powerProbe.setTemperature(intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1));
                    powerProbe.setHealth(intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1));
                    powerLevel = level;
                    powerListener.onUpdate(powerProbe);
                }
                break;
            default:
                Log.e(TAG, "Something went wrong.");
                break;
        }
    }

    /**
     * Checks the plug state from a charging device.
     * @param context to check.
     * @param intent which triggered the Broadcastreceiver
     * @return the plugged state
     */
    private String getPluggedState(Context context, Intent intent) {
        Intent chargingIntent = context.registerReceiver(null, new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED));
        int pluggedState = 0;

        if (chargingIntent != null) {
            pluggedState = chargingIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        }

        switch (pluggedState) {
            case 0:
                return ChargingProbe.NONE;
            case BatteryManager.BATTERY_PLUGGED_AC:
                return ChargingProbe.AC;
            case BatteryManager.BATTERY_PLUGGED_USB:
                return ChargingProbe.USB;
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                return ChargingProbe.WIRELESS;
            default:
                return ChargingProbe.NONE;
        }
    }
}
