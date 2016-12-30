package edu.umd.fcmd.sensorlisteners.listener.system;

import android.content.Context;
import android.content.IntentFilter;

import edu.umd.fcmd.sensorlisteners.NoSensorFoundException;
import edu.umd.fcmd.sensorlisteners.listener.Listener;
import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.model.system.DreamingModeProbe;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;

/**
 * Created by MMueller on 12/30/2016.
 *
 * Listener to certain system events as:
 * - Dreaming mode
 * - Screen state
 */
public class SystemListener implements Listener {
    private final String TAG = getClass().getSimpleName();

    private boolean runningState;

    private final Context context;
    private final ProbeManager<Probe> probeManager;
    private SystemReceiverFactory systemReceiverFactory;

    private SystemReceiver systemReceiver;

    public SystemListener(Context context,
                          ProbeManager<Probe> probeManager,
                          SystemReceiverFactory systemReceiverFactory){
        this.context = context;
        this.probeManager = probeManager;
        this.systemReceiverFactory = systemReceiverFactory;

    }

    @Override
    public void onUpdate(Probe state) {
        probeManager.save(state);
    }

    @Override
    public void startListening() throws NoSensorFoundException {
        if(!runningState){
            systemReceiver = systemReceiverFactory.create(this, context);

            IntentFilter systemFilter = new IntentFilter();

            systemFilter.addAction("android.intent.action.AIRPLANE_MODE");
            systemFilter.addAction("android.intent.action.BOOT_COMPLETED");
            systemFilter.addAction("android.intent.action.ACTION_SHUTDOWN");
            systemFilter.addAction("android.intent.action.USER_PRESENT");
            systemFilter.addAction("android.intent.action.DREAMING_STARTED");
            systemFilter.addAction("android.intent.action.DREAMING_STOPPED");
            systemFilter.addAction("android.intent.action.HEADSET_PLUG");

            context.registerReceiver(systemReceiver, systemFilter);

            sendInitalProbes();
        }

    }

    /**
     * Creates and sends the needed initial probes.
     *
     * Device dreaming has NO initial probe.
     */
    private void sendInitalProbes() {
        // start DEBUG
        DreamingModeProbe dreamingModeProbeOn = new DreamingModeProbe();
        dreamingModeProbeOn.setDate(System.currentTimeMillis());
        dreamingModeProbeOn.setState(DreamingModeProbe.ON);
        onUpdate(dreamingModeProbeOn);

        DreamingModeProbe dreamingModeProbeOff = new DreamingModeProbe();
        dreamingModeProbeOff.setDate(System.currentTimeMillis());
        dreamingModeProbeOff.setState(DreamingModeProbe.OFF);
        onUpdate(dreamingModeProbeOff);
        // end DEBUG

    }

    @Override
    public void stopListening() {
        if(runningState){
            if(systemReceiver != null){
                context.unregisterReceiver(systemReceiver);
            }
        }

    }

    @Override
    public boolean isRunning() {
        return runningState;
    }
}
