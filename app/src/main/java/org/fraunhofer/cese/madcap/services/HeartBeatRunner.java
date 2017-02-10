package org.fraunhofer.cese.madcap.services;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.fraunhofer.cese.madcap.MyApplication;
import org.fraunhofer.cese.madcap.R;
import org.fraunhofer.cese.madcap.cache.Cache;
import org.fraunhofer.cese.madcap.util.ManualProbeUploader;

import java.util.concurrent.ScheduledExecutorService;

import edu.umd.fcmd.sensorlisteners.model.util.ReverseHeartBeatProbe;

/**
 * Created by MMueller on 2/8/2017.
 */

public class HeartBeatRunner implements Runnable {
    private final String TAG = getClass().getSimpleName();
    private static final int delta = 20000;
    private final Application application;
    private final Cache cache;
    private final ManualProbeUploader manualProbeUploader;
    private final DataCollectionService dataCollectionService;
    private final long interval;
    private final ScheduledExecutorService scheduledExecutorService;

    public HeartBeatRunner(Application application, DataCollectionService dataCollectionService, ScheduledExecutorService scheduledExecutorService, Cache cache, ManualProbeUploader manualProbeUploader, long interval){
        this.application = application;
        this.dataCollectionService = dataCollectionService;
        this.cache = cache;
        this.manualProbeUploader = manualProbeUploader;
        this.scheduledExecutorService = scheduledExecutorService;
        this.interval = interval;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
            long currentTime = System.currentTimeMillis();

            if (dataCollectionService.getLifeSign()) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(application.getApplicationContext());
                long lastHearthbeat = prefs.getLong(application.getString(R.string.last_hearthbeat), currentTime);

                if (intervallTooLong(lastHearthbeat, currentTime, delta, interval)) {

                    ReverseHeartBeatProbe deathStart = new ReverseHeartBeatProbe(ReverseHeartBeatProbe.DEATH_START);
                    deathStart.setDate(lastHearthbeat);
                    manualProbeUploader.uploadManual(deathStart, application, cache);

                    ReverseHeartBeatProbe deathEnd = new ReverseHeartBeatProbe(ReverseHeartBeatProbe.DEATH_END);
                    deathEnd.setDate(currentTime);
                    manualProbeUploader.uploadManual(deathEnd, application, cache);
                }

                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong(application.getString(R.string.last_hearthbeat), System.currentTimeMillis());
                editor.commit();
            } else {
                scheduledExecutorService.shutdown();
            }
    }



    /**
     * Checks if the captured hearthbeat intervall is exceeded and
     * any probe could have been missed.
     * @param last the last hearthbeat timestamp.
     * @param now the new timestamp.
     * @param delta the delta.
     * @return true if too long, false else.
     */
    private boolean intervallTooLong(long last, long now, long delta, long interval ){
        Log.d(TAG, "last: "+last+", now: "+now+", diff: "+(now-last)+", delta: "+delta+", interval: "+interval);
        if(last < now - interval - delta){
            Log.d(TAG, "HeartBeat skipped at least one beat");
            return true;
        }else{
            Log.d(TAG, "HeartBeat o.k.");
            return false;
        }
    }
}
