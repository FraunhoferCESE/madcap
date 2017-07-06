package edu.umd.fcmd.sensorlisteners.listener.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;
import javax.inject.Named;

import edu.umd.fcmd.sensorlisteners.listener.Listener;
import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.model.activity.ActivityProbe;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;
import timber.log.Timber;

/**
 * Created by MMueller on 12/8/2016.
 * <p>
 * Listener for activity regonition.
 */
public class ActivityListener implements Listener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final long ACTIVITY_STARTUP_DELAY = 10000L;

    private boolean isRunning;

    private final ProbeManager<Probe> probeManager;
    private final GoogleApiClient googleApiClient;
    private final Handler activityUpdateHandler;
    private final ActivityUpdater activityUpdater;


    @Inject
    public ActivityListener(ProbeManager<Probe> probeManager,
                            @Named("AwarenessApi") GoogleApiClient googleApiClient,
                            ActivityUpdater activityUpdater,
                            @Named("ActivityUpdateHandler") Handler activityUpdateHandler) {
        this.probeManager = probeManager;
        this.googleApiClient = googleApiClient;
        this.activityUpdater = activityUpdater;
        this.activityUpdateHandler = activityUpdateHandler;
    }

    @Override
    public void onUpdate(Probe state) {
        probeManager.save(state);
    }

    @Override
    public void startListening() {
        if (!isRunning) {
            EventBus.getDefault().register(this);

            googleApiClient.registerConnectionCallbacks(this);
            googleApiClient.registerConnectionFailedListener(this);
            googleApiClient.connect();

            isRunning = true;
        }
    }

    @Override
    public void stopListening() {
        if (isRunning) {
            activityUpdateHandler.removeCallbacksAndMessages(null);
            googleApiClient.unregisterConnectionCallbacks(this);
            googleApiClient.unregisterConnectionFailedListener(this);
            googleApiClient.disconnect();

            EventBus.getDefault().unregister(this);
            isRunning = false;
        }
    }

    @Override
    public boolean isPermittedByUser() {
        //non dangerous permission
        return true;
    }

    @Subscribe
    public void handleActivityChange(ActivityProbe probe) {
        onUpdate(probe);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Timber.d("Successfully connected to AwarenessApi");
        activityUpdateHandler.postDelayed(activityUpdater, ACTIVITY_STARTUP_DELAY);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Timber.w("AwarenessApi connection suspended");
        activityUpdateHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Timber.w("AwarenessApi connection failed");
        activityUpdateHandler.removeCallbacksAndMessages(null);
    }
}
