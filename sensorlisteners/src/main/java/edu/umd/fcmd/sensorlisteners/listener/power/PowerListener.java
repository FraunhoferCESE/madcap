package edu.umd.fcmd.sensorlisteners.listener.power;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

import javax.inject.Inject;

import edu.umd.fcmd.sensorlisteners.listener.Listener;
import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.model.power.ChargingProbe;
import edu.umd.fcmd.sensorlisteners.model.power.PowerProbe;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;
import timber.log.Timber;

/**
 * Created by MMueller on 12/12/2016.
 * <p>
 * Listener for the Power of a smartphone.
 */
public class PowerListener extends BroadcastReceiver implements Listener {
    private final Context mContext;
    private final ProbeManager<Probe> probeManager;
    private final BatteryManager batteryManager;

    private int powerLevel = -1;
    private int pluggedState = -1;
    private boolean runningState;

    @Inject
    public PowerListener(Context context, ProbeManager<Probe> probeManager, BatteryManager batteryManager) {
        mContext = context;
        this.probeManager = probeManager;
        this.batteryManager = batteryManager;
    }

    @Override
    public void onUpdate(Probe state) {
        probeManager.save(state);
    }

    @Override
    public void startListening() {
        if (!runningState && isPermittedByUser()) {
            Timber.d("startListening");
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
            intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
            intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);

            Intent batteryStatus = mContext.registerReceiver(this, intentFilter);

            // TODO: send initial probeas

            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            PowerProbe powerProbe = new PowerProbe();
            powerProbe.setDate(System.currentTimeMillis());
            powerProbe.setRemainingPower(level / (double) scale);
            powerProbe.setVoltage(batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1));
            powerProbe.setTemperature(batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1));
            powerProbe.setHealth(batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1));
            powerLevel = level;
            onUpdate(powerProbe);


            runningState = true;
        }
    }

    @Override
    public void stopListening() {
        if (runningState) {
            Timber.d("stopListening");
            mContext.unregisterReceiver(this);
            runningState = false;
        }
    }

    @Override
    public boolean isPermittedByUser() {
        //non dangerous permission
        return true;
    }

    private String getPluggedState(Context context, Intent intent) {
        Intent chargingIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
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


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED) || intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
            ChargingProbe chargingProbe = new ChargingProbe();
            chargingProbe.setDate(System.currentTimeMillis());
            chargingProbe.setCharging(getPluggedState(context, intent));
            onUpdate(chargingProbe);
        } else if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED) && intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) != powerLevel) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            PowerProbe powerProbe = new PowerProbe();
            powerProbe.setDate(System.currentTimeMillis());
            powerProbe.setRemainingPower(level / (double) scale);
            powerProbe.setVoltage(intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1));
            powerProbe.setTemperature(intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1));
            powerProbe.setHealth(intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1));
            powerLevel = level;
            onUpdate(powerProbe);

        }
    }

    /**
     * Sends inital probes.
     *
     * @param batteryIntent the intent to get the information from.
     * @return the initial batterylevel.
     */

    private int sendInitialProbes(Intent batteryIntent) {
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        PowerProbe powerProbe = new PowerProbe();
        powerProbe.setDate(System.currentTimeMillis());
        powerProbe.setRemainingPower(level / (double) scale);
        powerProbe.setVoltage(batteryIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1));
        powerProbe.setTemperature(batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1));
        powerProbe.setHealth(batteryIntent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1));

        onUpdate(powerProbe);

        ChargingProbe chargingProbe = new ChargingProbe();
        chargingProbe.setDate(System.currentTimeMillis());
        int plugged = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        switch (plugged) {
            case 0:
                chargingProbe.setCharging(ChargingProbe.NONE);
                break;
            case BatteryManager.BATTERY_PLUGGED_AC:
                chargingProbe.setCharging(ChargingProbe.AC);
                break;
            case BatteryManager.BATTERY_PLUGGED_USB:
                chargingProbe.setCharging(ChargingProbe.USB);
                break;
            default:
                Log.e(TAG, "Plugged to unknown");
                break;
        }
        return level;
    }
}
