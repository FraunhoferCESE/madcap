package edu.umd.fcmd.sensorlisteners.listener.power;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

import edu.umd.fcmd.sensorlisteners.NoSensorFoundException;
import edu.umd.fcmd.sensorlisteners.listener.Listener;
import edu.umd.fcmd.sensorlisteners.model.ChargingProbe;
import edu.umd.fcmd.sensorlisteners.model.PowerProbe;
import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;

import static android.R.attr.level;

/**
 * Created by MMueller on 12/12/2016.
 *
 * Listener for the Power of a smartphone.
 */
public class PowerListener implements Listener {
    private final String TAG = getClass().getSimpleName();

    private Context context;
    private ProbeManager<Probe> probeManager;

    private PowerInformationReceiver receiver;

    private boolean runningState;

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
        if(!runningState){
            Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            receiver = new PowerInformationReceiver(this, getInitialPowerLevel(batteryIntent));
            context.registerReceiver(receiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        }
        runningState = true;
    }

    @Override
    public void stopListening() {
        if(runningState){
            context.unregisterReceiver(receiver);
        }
        runningState = false;
    }

    @Override
    public boolean isRunning() {
        return runningState;
    }

    /**
     * Gets the initial power level.
     * @param batteryIntent
     * @return
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
        int level = -1;
        int plugged = -1;

        level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        PowerProbe powerProbe = new PowerProbe();
        powerProbe.setDate(System.currentTimeMillis());
        powerProbe.setRemainingPower(level);

        onUpdate(powerProbe);

        ChargingProbe chargingProbe = new ChargingProbe();
        chargingProbe.setDate(System.currentTimeMillis());
        plugged = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
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
