package edu.umd.fcmd.sensorlisteners.listener.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.awareness.SnapshotApi;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.snapshot.DetectedActivityResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

/**
 * Created by MMueller on 12/8/2016.
 */
public class ActivityFenceReceiver extends BroadcastReceiver {
    private final String TAG = getClass().getSimpleName();

    private SnapshotApi snapshotApi;
    private ActivityListener activityListener;
    private GoogleApiClient googleApiClient;

    public ActivityFenceReceiver(SnapshotApi snapshotApi, ActivityListener activityListener, GoogleApiClient googleApiClient){
        this.snapshotApi = snapshotApi;
        this.activityListener = activityListener;
        this.googleApiClient = googleApiClient;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        FenceState fenceState = FenceState.extract(intent);

        Log.d(TAG, "new activity received");

        snapshotApi.getDetectedActivity(googleApiClient)
                .setResultCallback(new ResultCallback<DetectedActivityResult>() {
                    @Override
                    public void onResult(@NonNull DetectedActivityResult detectedActivityResult) {
                        if (!detectedActivityResult.getStatus().isSuccess()) {
                            Log.e(TAG, "Could not get the current activity.");
                            return;
                        }
                        ActivityRecognitionResult ar = detectedActivityResult.getActivityRecognitionResult();
                        DetectedActivity probableActivity = ar.getMostProbableActivity();
                        Log.i(TAG, "Detected Activity "+probableActivity.toString());
                    }
                });
    }
}
