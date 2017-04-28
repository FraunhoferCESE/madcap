package edu.umd.fcmd.sensorlisteners.listener.power;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

import javax.inject.Inject;

import edu.umd.fcmd.sensorlisteners.NoSensorFoundException;
import edu.umd.fcmd.sensorlisteners.listener.Listener;
import edu.umd.fcmd.sensorlisteners.model.power.ChargingProbe;
import edu.umd.fcmd.sensorlisteners.model.power.PowerProbe;
import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;

/**
 * Created by MMueller on 12/12/2016.
 *
 * Listener for the Power of a smartphone.
 */
public class PowerListener implements Listener {
    private final String TAG = getClass().getSimpleName();

    private final Context context;
    private final ProbeManager<Probe> probeManager;

    private PowerInformationReceiver receiver;

    private boolean runningState;

    @Inject
    public PowerListener(Context context,
                         ProbeManager<Probe> probeManager){
        this.context = context;
        this.probeManager = probeManager;
    }

    @Override
    public void onUpdate(Probe state) {
        probeManager.save(state);
    }

    @Override
    public void startListening() throws NoSensorFoundException {
        if (!runningState) {
            if (isPermittedByUser()) {
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("android.intent.action.ACTION_POWER_CONNECTED");
                intentFilter.addAction("android.intent.action.ACTION_POWER_DISCONNECTED");
                intentFilter.addAction("android.intent.action.BATTERY_CHANGED");

                Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                receiver = new PowerInformationReceiver(this, getInitialPowerLevel(batteryIntent));
                context.registerReceiver(receiver, intentFilter);
                runningState = true;
            } else {
//                permissionsManager.requestPermissionFromNotification();
                Log.i(TAG,"Location listener NOT listening");
                runningState = false;
            }
        }
    }

    @Override
    public void stopListening() {
        if(runningState){
            if(receiver != null){
                context.unregisterReceiver(receiver);
            }
        }
        runningState = false;
    }

    @Override
    public boolean isRunning() {
        return runningState;
    }

    @Override
    public boolean isPermittedByUser() {
        //TODO: system check for permission
        //non dangerous permission
        return true;
    }

    /**
     * Gets the initial power level.
     * @param batteryIntent the passed battery intentn.
     * @return the initial power level.
     */
    private int getInitialPowerLevel(Intent batteryIntent){
        int level = -1;
        if(batteryIntent != null) {
            level = sendInitialProbes(batteryIntent);
        }
        return level;
    }

    /**
     * Sends inital probes.
     * @param batteryIntent the intent to get the information from.
     * @return the initial batterylevel.
     */
    private int sendInitialProbes(Intent batteryIntent){
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        PowerProbe powerProbe = new PowerProbe();
        powerProbe.setDate(System.currentTimeMillis());
        powerProbe.setRemainingPower(level / (double)scale);
        powerProbe.setVoltage(batteryIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1));
        powerProbe.setTemperature(batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1));
        powerProbe.setHealth(batteryIntent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1));

        onUpdate(powerProbe);

        ChargingProbe chargingProbe = new ChargingProbe();
        chargingProbe.setDate(System.currentTimeMillis());
        int plugged = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        switch (plugged){
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
