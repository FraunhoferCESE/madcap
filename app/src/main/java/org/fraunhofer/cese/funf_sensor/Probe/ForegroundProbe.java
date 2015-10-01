package org.fraunhofer.cese.funf_sensor.Probe;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import edu.mit.media.funf.probe.Probe;


public class ForegroundProbe extends Probe.Base implements Probe.ContinuousProbe {
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

        Log.d(TAG, "ForegroundProbe starting");
        super.onStart();
        final Gson gson = getGson();
        final ActivityManager am = (ActivityManager) getContext().getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);

        foregroundProbeDeliverer = createForegroundProbeDeliverer(gson, am);

        foregroundProbeDeliverer.start();


//        Log.d(TAG, "ForegroundProbe starting");
//        super.onStart();
//        Gson gson = getGson();
//        ActivityManager am = (ActivityManager) getContext().getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
//        ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
//        String foregroundTaskPackageName = foregroundTaskInfo .topActivity.getPackageName();
//        Log.i("ForegroundProbe.class","ForegroundProbe started.");
//        Log.i("ForegroundProbe",foregroundTaskPackageName);
//        //Log.wtf("ForegroundProbe","Package name" + foregroundTaskPackageName);
//        sendData(gson.toJsonTree(foregroundTaskInfo).getAsJsonObject());
//        stop();
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
                while (run) {
                    ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
                    String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();
                    Log.i("ForegroundProbe.class", "ForegroundProbe started.");
                    Log.i("ForegroundProbe", foregroundTaskPackageName);
                    //Log.wtf("ForegroundProbe","Package name" + foregroundTaskPackageName);
                    sendData(gson.toJsonTree(foregroundTaskInfo).getAsJsonObject());
                    Log.i("ForegroundProbe.class", "ForegroundProbe sent.");
                    try {
                        Thread.sleep(15000l);
                    } catch (InterruptedException e) {
                        run = false;
                        Log.i("ForegroundProbe.Class", "ForegroundProbe interrupted.");
                    }
                }
            }
        });
        return foregroundProbeDeliverer;
    }


}
