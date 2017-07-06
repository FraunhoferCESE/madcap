package edu.umd.fcmd.sensorlisteners.listener.applications;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import edu.umd.fcmd.sensorlisteners.NoSensorFoundException;
import edu.umd.fcmd.sensorlisteners.issuehandling.PermissionsManager;
import edu.umd.fcmd.sensorlisteners.listener.Listener;
import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;

/**
 * Created by MMueller on 11/17/2016.
 *
 * Application Listener class to listen to events when a certain app goes to
 * foreground or to background.
 */

public class ApplicationsListener implements Listener {
    private final String TAG = getClass().getSimpleName();

    private final Context context;
    private final ProbeManager<Probe> probeProbeManager;
    private final TimedApplicationTaskFactory timedApplicationTaskFactory;
    private TimedApplicationTask timedApplicationTask;
    private final PermissionsManager permissionsManager;
    private boolean runningStatus;

    @Inject
    public ApplicationsListener(Context context,
                                ProbeManager<Probe> probeProbeManager,
                                TimedApplicationTaskFactory timedApplicationTaskFactory,
                                PermissionsManager permissionsManager){
        this.context = context;
        this.probeProbeManager = probeProbeManager;
        this.timedApplicationTaskFactory = timedApplicationTaskFactory;
        this.permissionsManager = permissionsManager;
    }

    @Override
    public void onUpdate(Probe state) {
        probeProbeManager.save(state);
    }

    @Override
    public void startListening() {
        if (!runningStatus) {
            if (isPermittedByUser()) {
                timedApplicationTask = timedApplicationTaskFactory.create(this, context, permissionsManager);
                timedApplicationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                timedApplicationTask.sendInitialProbes();
                runningStatus = true;
            } else {
                Log.i(TAG, "Application listener NOT listening");
                permissionsManager.requestPermissionFromNotification();

                runningStatus = false;
            }
        }
    }

    @Override
    public void stopListening() {
        if(timedApplicationTask != null && runningStatus){
            Log.d(TAG, "Timed application task is not null");
            timedApplicationTask.cancel(true);
        }
        runningStatus = false;
    }

    @Override
    public boolean isPermittedByUser() {
        if (permissionsManager.isUsageStatsPermitted())
        {
            Log.e(TAG,"Usage access setting access permitted by user");
            return true;
        }else {
            Log.v(TAG,"Usage access setting access NOT permitted by user");
            return false;
        }
    }
}
