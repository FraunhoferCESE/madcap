package edu.umd.fcmd.sensorlisteners.listener.power;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import javax.inject.Inject;

import edu.umd.fcmd.sensorlisteners.listener.Listener;
import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.model.power.PowerProbeFactory;
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
    private final PowerProbeFactory factory;

    private int powerLevel = -1;
    private boolean runningState;

    @Inject
    public PowerListener(Context context, ProbeManager<Probe> probeManager, PowerProbeFactory factory) {
        mContext = context;
        this.probeManager = probeManager;
        this.factory = factory;
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
            if (batteryStatus != null) {
                Timber.d("Sending initial probe");
                onUpdate(factory.createPowerProbe(batteryStatus));
                onUpdate(factory.createChargingProbe(batteryStatus));
            }

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


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED) || intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
            onUpdate(factory.createChargingProbe(intent));
        } else if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED) && (intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) != powerLevel)) {
            powerLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            onUpdate(factory.createPowerProbe(intent));
        }
    }
}
