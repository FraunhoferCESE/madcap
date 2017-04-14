package edu.umd.fcmd.sensorlisteners.listener.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.awareness.SnapshotApi;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import javax.inject.Inject;
import javax.inject.Named;

import edu.umd.fcmd.sensorlisteners.NoSensorFoundException;
import edu.umd.fcmd.sensorlisteners.listener.Listener;
import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;

/**
 * Created by MMueller on 12/8/2016.
 *
 * Listener for activity regonition.
 */
public class ActivityListener implements Listener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private final String TAG = getClass().getSimpleName();
    private boolean runningState;

    private final ProbeManager<Probe> probeManager;
    private final GoogleApiClient googleApiClient;
    private final SnapshotApi snapshotApi;

    private final TimedActivityTaskFactory timedActivityTaskFactory;
    private TimedActivityTask timedActivityTask;

    @Inject
    public ActivityListener(ProbeManager<Probe> probeManager,
                            @Named("AwarenessApi") GoogleApiClient googleApiClient,
                            SnapshotApi snapshotApi,
                            TimedActivityTaskFactory timedActivityTaskFactory){
        this.probeManager = probeManager;
        this.googleApiClient = googleApiClient;
        this.snapshotApi = snapshotApi;
        this.timedActivityTaskFactory = timedActivityTaskFactory;
    }

    @Override
    public void onUpdate(Probe state) {
        probeManager.save(state);
    }

    @Override
    public void startListening() throws NoSensorFoundException {
        if(!runningState){
            googleApiClient.registerConnectionCallbacks(this);
            googleApiClient.registerConnectionFailedListener(this);

            googleApiClient.connect();
        }

        runningState = true;
    }

    @Override
    public void stopListening() {
        if(runningState){
            timedActivityTask.cancel(true);

            googleApiClient.disconnect();
        }
        runningState = false;
    }

    @Override
    public boolean isRunning() {
        return runningState;
    }

    @Override
    public boolean isPermittedByUser() {
        return false;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        timedActivityTask = timedActivityTaskFactory.create(this, snapshotApi);
        timedActivityTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "GoogleApiClient connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "GoogleApiClient connection failed");
    }

    GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }
}
