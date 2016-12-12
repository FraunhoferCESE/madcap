package edu.umd.fcmd.sensorlisteners.listener.power;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

import edu.umd.fcmd.sensorlisteners.model.ChargingProbe;
import edu.umd.fcmd.sensorlisteners.model.PowerProbe;

/**
 * Created by MMueller on 12/12/2016.
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
                int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                if (chargePlug == BatteryManager.BATTERY_PLUGGED_AC) {
                    chargingProbe.setCharging(ChargingProbe.AC);
                } else if (chargePlug == BatteryManager.BATTERY_PLUGGED_USB) {
                    chargingProbe.setCharging(ChargingProbe.USB);
                }
                powerListener.onUpdate(chargingProbe);
                break;
            case Intent.ACTION_POWER_DISCONNECTED:
                ChargingProbe chargingProbe2 = new ChargingProbe();
                chargingProbe2.setDate(System.currentTimeMillis());
                chargingProbe2.setCharging(ChargingProbe.NONE);
                break;
            case Intent.ACTION_BATTERY_CHANGED:
                if (intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) != powerLevel) {
                    int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                    PowerProbe powerProbe = new PowerProbe();
                    powerProbe.setDate(System.currentTimeMillis());
                    powerProbe.setRemainingPower(level);
                    powerLevel = level;
                    powerListener.onUpdate(powerProbe);
                }
                break;
            default:
                Log.e(TAG, "Something went wrong.");
                break;
        }
    }
}
