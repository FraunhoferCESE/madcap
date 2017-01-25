package org.fraunhofer.cese.madcap.services;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.fraunhofer.cese.madcap.MyApplication;
import org.fraunhofer.cese.madcap.R;
import org.fraunhofer.cese.madcap.cache.Cache;
import org.fraunhofer.cese.madcap.util.ManualProbeUploader;

import java.util.TimerTask;

import edu.umd.fcmd.sensorlisteners.model.util.ReverseHeartBeatProbe;

/**
 * Created by MMueller on 1/25/2017.
 *
 * A hearthbeat task to be scheduled at a fixed rate.
 */
public class HeartBeatTask extends TimerTask {
    private final String TAG = getClass().getSimpleName();

    private Application application;
    private Cache cache;
    private ManualProbeUploader manualProbeUploader;
    private static int delta = 2000;
    private long interval;

    public HeartBeatTask(Application application, Cache cache, ManualProbeUploader manualProbeUploader, long interval){
        this.application = application;
        this.cache = cache;
        this.manualProbeUploader = manualProbeUploader;
        this.interval = interval;
    }

    /**
     * The action to be performed by this timer task.
     */
    @Override
    public void run() {
        MyApplication.madcapLogger.d(TAG, "HeartBeat");
        long currentTime = System.currentTimeMillis();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(application.getApplicationContext());
        long lastHearthbeat = prefs.getLong(application.getString(R.string.last_hearthbeat), currentTime);

        if(intervallTooLong(lastHearthbeat, currentTime, delta, interval)){
            ReverseHeartBeatProbe deathStart = new ReverseHeartBeatProbe();
            deathStart.setDate(lastHearthbeat);
            deathStart.setKind(ReverseHeartBeatProbe.DEATH_START);
            manualProbeUploader.uploadManual(deathStart, application, cache);

            ReverseHeartBeatProbe deathEnd = new ReverseHeartBeatProbe();
            deathEnd.setDate(currentTime);
            deathEnd.setKind(ReverseHeartBeatProbe.DEATH_END);
            manualProbeUploader.uploadManual(deathEnd, application, cache);
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(application.getString(R.string.last_hearthbeat), System.currentTimeMillis());

        if(editor.commit()){
            MyApplication.madcapLogger.d(TAG, "New Hearthbeat saved to disk");
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
        if(last < now - interval - delta){
            Log.d(TAG, "HeartBeat skipped at least one beat");
            return true;
        }else{
            Log.d(TAG, "HeartBeat o.k.");
            return false;
        }
    }
}
