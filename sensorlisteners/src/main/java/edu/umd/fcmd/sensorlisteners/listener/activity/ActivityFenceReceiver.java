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

import java.util.List;

import edu.umd.fcmd.sensorlisteners.model.ActivityProbe;

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

        if (TextUtils.equals(fenceState.getFenceKey(), ActivityListener.STILL_KEY)) {
            snapshotApi.getDetectedActivity(googleApiClient)
                    .setResultCallback(new ResultCallback<DetectedActivityResult>() {
                        @Override
                        public void onResult(@NonNull DetectedActivityResult detectedActivityResult) {
                            if (!detectedActivityResult.getStatus().isSuccess()) {
                                Log.e(TAG, "Could not get the current activity.");
                                return;
                            }
                            ActivityRecognitionResult ar = detectedActivityResult.getActivityRecognitionResult();
                            activityListener.onUpdate(createProbeFromActivityResult(ar));
                        }
                    });
        }
    }

    public ActivityProbe createProbeFromActivityResult(ActivityRecognitionResult activityRecognitionResult){
        List<DetectedActivity> activityList = activityRecognitionResult.getProbableActivities();
        Log.d(TAG, "List "+activityList.toString()+" has size "+activityList.size());

        ActivityProbe activityProbe = new ActivityProbe();
        activityProbe.setDate(System.currentTimeMillis());

        for(DetectedActivity detectedActivity : activityList){
            int type = detectedActivity.getType();

            switch(type){
                case DetectedActivity.IN_VEHICLE:
                    activityProbe.setInVehicle((double) detectedActivity.getConfidence()/100);
                    break;
                case DetectedActivity.ON_BICYCLE:
                    activityProbe.setOnBicycle((double) detectedActivity.getConfidence()/100);
                    break;
                case DetectedActivity.ON_FOOT:
                    activityProbe.setOnFoot((double) detectedActivity.getConfidence()/100);
                    break;
                case DetectedActivity.RUNNING:
                    activityProbe.setRunning((double) detectedActivity.getConfidence()/100);
                    break;
                case DetectedActivity.STILL:
                    activityProbe.setStill((double) detectedActivity.getConfidence()/100);
                    break;
                case DetectedActivity.TILTING:
                    activityProbe.setTilting((double) detectedActivity.getConfidence()/100);
                    break;
                case DetectedActivity.WALKING:
                    activityProbe.setWalking((double) detectedActivity.getConfidence()/100);
                    break;
                case DetectedActivity.UNKNOWN:
                    activityProbe.setUnknown((double) detectedActivity.getConfidence()/100);
                    break;
                case -1000:
                    break;
                default:
                    Log.e(TAG, "Incorrect Activity detected");
                    break;
            }
        }

        return activityProbe;
    }
}
