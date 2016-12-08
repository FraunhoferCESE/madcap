package edu.umd.fcmd.sensorlisteners.listener.activity;

import com.google.android.gms.awareness.SnapshotApi;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by MMueller on 12/8/2016.
 */

public class ActivityFenceReceiverFactory {

    public ActivityFenceReceiver create(SnapshotApi snapshotApi, ActivityListener activityListener, GoogleApiClient googleApiClient){
        return new ActivityFenceReceiver(snapshotApi, activityListener, googleApiClient);
    }
}
