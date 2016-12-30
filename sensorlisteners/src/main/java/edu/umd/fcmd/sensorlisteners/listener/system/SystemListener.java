package edu.umd.fcmd.sensorlisteners.listener.system;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;

import edu.umd.fcmd.sensorlisteners.NoSensorFoundException;
import edu.umd.fcmd.sensorlisteners.listener.Listener;
import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.model.system.AirplaneModeProbe;
import edu.umd.fcmd.sensorlisteners.model.system.DreamingModeProbe;
import edu.umd.fcmd.sensorlisteners.model.system.ScreenProbe;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;

import static android.content.Context.POWER_SERVICE;

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
            systemFilter.addAction("android.intent.action.SCREEN_ON");
            systemFilter.addAction("android.intent.action.SCREEN_OFF");

            context.registerReceiver(systemReceiver, systemFilter);

            sendInitalProbes();
        }

        runningState = true;
    }

    /**
     * Creates and sends the needed initial probes.
     *
     * Device dreaming has NO initial probe.
     */
    private void sendInitalProbes() {
        ScreenProbe screenProbe = new ScreenProbe();
        screenProbe.setDate(System.currentTimeMillis());
        screenProbe.setState(getCurrentScreenStatus());
        onUpdate(screenProbe);

        AirplaneModeProbe airplaneModeProbe = new AirplaneModeProbe();
        airplaneModeProbe.setDate(System.currentTimeMillis());
        airplaneModeProbe.setState(getCurrentScreenStatus());
        Log.d(TAG, "AIRPLANE "+airplaneModeProbe);
        onUpdate(airplaneModeProbe);
    }

    @Override
    public void stopListening() {
        if(runningState){
            if(systemReceiver != null){
                context.unregisterReceiver(systemReceiver);
            }
        }
        runningState = false;
    }

    @Override
    public boolean isRunning() {
        return runningState;
    }

    /**
     * Gets the current screen status.
     * @return either ON of OFF.
     */
    private String getCurrentScreenStatus(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            //API 20+
            DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
            for (Display display : dm.getDisplays()) {
                if (display.getState() != Display.STATE_OFF) {
                    return ScreenProbe.OFF;
                }
            }
            return ScreenProbe.ON;
        }else{
            // API 11+
            PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
            if (powerManager.isScreenOn()){
                return ScreenProbe.OFF;
            }else{
                return ScreenProbe.ON;
            }
        }
    }

    /**
     * Gets the current AirplaneMode state of the phone.
     * @return either ON or OFF.
     */
    String getCurrentAirplaneModeState() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if(Settings.System.getInt(context.getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 0) != 0){
                return AirplaneModeProbe.ON;
            }else{
                return AirplaneModeProbe.OFF;
            }
        } else {
            if(Settings.Global.getInt(context.getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON, 0) != 0){
                return AirplaneModeProbe.ON;
            }else{
                return AirplaneModeProbe.OFF;
            }
        }
    }
}
