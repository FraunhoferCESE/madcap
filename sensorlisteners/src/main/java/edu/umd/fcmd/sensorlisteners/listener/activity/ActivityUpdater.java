package edu.umd.fcmd.sensorlisteners.listener.activity;

import android.os.Handler;
import android.support.annotation.NonNull;

import com.google.android.gms.awareness.SnapshotApi;
import com.google.android.gms.awareness.snapshot.DetectedActivityResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;
import javax.inject.Named;

import edu.umd.fcmd.sensorlisteners.model.activity.ActivityProbe;
import edu.umd.fcmd.sensorlisteners.model.activity.ActivityProbeFactory;
import timber.log.Timber;

/**
 * Class to handle periodic calls to the AwarenessApi
 */
class ActivityUpdater implements Runnable {

    private static final long ACTIVITY_UPDATE_INTERVAL = 15000L;

    private final SnapshotApi snapshotApi;
    private final GoogleApiClient googleApiClient;
    private final ActivityProbeFactory factory;
    private final Handler handler;

    private ActivityProbe lastActivityProbe;

    @Inject
    ActivityUpdater(SnapshotApi snapshotApi,
                    @Named("AwarenessApi") GoogleApiClient googleApiClient,
                    ActivityProbeFactory factory,
                    @Named("ActivityUpdateHandler") Handler handler) {
        this.snapshotApi = snapshotApi;
        this.googleApiClient = googleApiClient;
        this.factory = factory;
        this.handler = handler;
    }

    @Override
    public void run() {
        snapshotApi.getDetectedActivity(googleApiClient)
                .setResultCallback(new ResultCallback<DetectedActivityResult>() {
                    @Override
                    public void onResult(@NonNull DetectedActivityResult r) {
                        if (r.getStatus().isSuccess()) {
                            ActivityProbe probe = factory.createActivityProbe(r.getActivityRecognitionResult().getProbableActivities());
                            if (!probe.equals(lastActivityProbe)) {
                                lastActivityProbe = probe;
                                EventBus.getDefault().post(probe);
                            }
                        } else {
                            Timber.w("Could not get the current activity. Status: " + r.getStatus());
                        }
                    }
                });

        handler.postDelayed(this, ACTIVITY_UPDATE_INTERVAL);
    }
}
