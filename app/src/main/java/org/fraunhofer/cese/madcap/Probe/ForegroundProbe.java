package org.fraunhofer.cese.madcap.Probe;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import org.fraunhofer.cese.madcap.MyApplication;

import com.google.gson.Gson;

import org.fraunhofer.cese.madcap.MyApplication;

import edu.mit.media.funf.probe.Probe;

import static android.content.Context.ACTIVITY_SERVICE;


public class ForegroundProbe extends Probe.Base implements Probe.ContinuousProbe {
    private static final long SLEEP_TIME = 15000;
    private static final String TAG = "Fraunhofer." + ForegroundProbe.class.getSimpleName();


    /**
     * Thread that creates and sends a ForegroundProbe every fifteen seconds
     */
    private static Thread foregroundProbeDeliverer;

    @Override
    protected void onDisable() {
        super.onDisable();
        foregroundProbeDeliverer.interrupt();
    }

    /**
     * Starts the foregroundProbeDeliverer Thread.
     * Calls it's create-Method to initialise it.
     */
    @Override
    protected void onEnable() {

        MyApplication.madcapLogger.d(TAG, "ForegroundProbe starting");

        onStart();
        Gson gson = getGson();
        ActivityManager am = (ActivityManager) getContext().getApplicationContext().getSystemService(ACTIVITY_SERVICE);

        foregroundProbeDeliverer = createForegroundProbeDeliverer(gson, am);
        foregroundProbeDeliverer.start();
    }

    /**
     * Initialises the foregroundProbeDeliverer and returns it
     * @param gson
     * @param am
     * @return foregroundProbeDeliverer: Thread
     */
    private Thread createForegroundProbeDeliverer(final Gson gson, final ActivityManager am) {
        foregroundProbeDeliverer = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean run = true;
                while (run && !foregroundProbeDeliverer.isInterrupted()) {
                    ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
                    sendData(gson.toJsonTree(foregroundTaskInfo).getAsJsonObject());
                    try {
                        Thread.sleep(SLEEP_TIME);
                    } catch (InterruptedException e) {
                        run = false;
                        MyApplication.madcapLogger.i("ForegroundProbe.Class", "ForegroundProbe interrupted.");
                    }
                }
            }
        });
        return foregroundProbeDeliverer;
    }


}
