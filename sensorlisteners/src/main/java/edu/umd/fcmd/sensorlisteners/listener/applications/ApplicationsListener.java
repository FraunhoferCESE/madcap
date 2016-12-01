package edu.umd.fcmd.sensorlisteners.listener.applications;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;

import java.util.Calendar;

import edu.umd.fcmd.sensorlisteners.NoSensorFoundException;
import edu.umd.fcmd.sensorlisteners.issuehandling.PermissionDeniedHandler;
import edu.umd.fcmd.sensorlisteners.listener.Listener;
import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;

/**
 * Created by MMueller on 11/17/2016.
 */

public class ApplicationsListener implements Listener {
    private final String TAG = getClass().getSimpleName();

    private final Context context;
    private final ProbeManager<Probe> probeProbeManager;
    private final TimedApplicationTaskFactory timedApplicationTaskFactory;
    private TimedApplicationTask timedApplicationTask;
    private final PermissionDeniedHandler permissionDeniedHandler;

    public ApplicationsListener(Context context,
                                ProbeManager<Probe> probeProbeManager,
                                TimedApplicationTaskFactory timedApplicationTaskFactory,
                                PermissionDeniedHandler permissionDeniedHandler){
        this.context = context;
        this.probeProbeManager = probeProbeManager;
        this.timedApplicationTaskFactory = timedApplicationTaskFactory;
        this.permissionDeniedHandler = permissionDeniedHandler;
    }

    @Override
    public void onUpdate(Probe state) {
        probeProbeManager.save(state);
    }

    @Override
    public void startListening() throws NoSensorFoundException {
        timedApplicationTask = timedApplicationTaskFactory.create(this, context, permissionDeniedHandler);
        timedApplicationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void stopListening() {

        if(timedApplicationTask != null){
            Log.d(TAG, "Timed apllication task is not null");
            timedApplicationTask.cancel(true);
        }
    }
}