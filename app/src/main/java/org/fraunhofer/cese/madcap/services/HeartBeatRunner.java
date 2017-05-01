package org.fraunhofer.cese.madcap.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;

import org.fraunhofer.cese.madcap.R;
import org.fraunhofer.cese.madcap.cache.CacheFactory;

import javax.inject.Inject;
import javax.inject.Named;

import edu.umd.fcmd.sensorlisteners.model.util.ReverseHeartBeatProbe;
import timber.log.Timber;

/**
 * Creates a Heartbeat to help identify time periods where MADCAP was unexpectedly terminated, e.g., when killed by the Android OS or an unexpected shutdown.
 * The class saves a probes indicating when "Death" occurred and the app "Started" again.
 */
class HeartBeatRunner implements Runnable {
    private static final int DELTA = 240000;
    private static final long HEARTBEAT_INTERVAL = 60000L;

    private final Context context;
    private final CacheFactory cacheFactory;
    private final Handler handler;

    @Inject
    HeartBeatRunner(Context context, @Named("HeartbeatHandler") Handler handler, CacheFactory cacheFactory) {
        this.context = context;
        this.handler = handler;
        this.cacheFactory = cacheFactory;
    }

    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {

        long now = System.currentTimeMillis();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        long lastHearthbeat = prefs.getLong(context.getString(R.string.last_hearthbeat), now);


        Timber.d("last: " + lastHearthbeat + ", now: " + now + ", diff: " + (now - lastHearthbeat) + ", DELTA: " + (long) DELTA + ", interval: " + HEARTBEAT_INTERVAL);
        boolean isIntervalTooLong;
        if (lastHearthbeat < (now - HEARTBEAT_INTERVAL - (long) DELTA)) {
            Timber.d("HeartBeat skipped at least one beat");
            isIntervalTooLong = true;
        } else {
            Timber.d("HeartBeat o.k.");
            isIntervalTooLong = false;
        }

        if (isIntervalTooLong) {
            ReverseHeartBeatProbe deathStart = new ReverseHeartBeatProbe(ReverseHeartBeatProbe.DEATH_START);
            deathStart.setDate(lastHearthbeat);
            cacheFactory.save(deathStart);

            ReverseHeartBeatProbe deathEnd = new ReverseHeartBeatProbe(ReverseHeartBeatProbe.DEATH_END);
            deathEnd.setDate(now);
            cacheFactory.save(deathEnd);
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(context.getString(R.string.last_hearthbeat), System.currentTimeMillis());
        editor.apply();

        handler.postDelayed(this, HEARTBEAT_INTERVAL);

    }
}
