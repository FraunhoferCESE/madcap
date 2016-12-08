package edu.umd.fcmd.sensorlisteners.listener.activity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.FenceApi;
import com.google.android.gms.awareness.SnapshotApi;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.DetectedActivityFence;
import com.google.android.gms.awareness.fence.FenceQueryRequest;
import com.google.android.gms.awareness.fence.FenceQueryResult;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.awareness.fence.FenceStateMap;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.fence.LocationFence;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;

import java.util.Arrays;

import edu.umd.fcmd.sensorlisteners.BuildConfig;
import edu.umd.fcmd.sensorlisteners.NoSensorFoundException;
import edu.umd.fcmd.sensorlisteners.issuehandling.PermissionDeniedHandler;
import edu.umd.fcmd.sensorlisteners.issuehandling.SensorNoAnswerReceivedHandler;
import edu.umd.fcmd.sensorlisteners.listener.Listener;
import edu.umd.fcmd.sensorlisteners.listener.location.LocationServiceStatusReceiverFactory;
import edu.umd.fcmd.sensorlisteners.listener.location.TimedLocationTaskFactory;
import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;

import static android.provider.Settings.System.DATE_FORMAT;

/**
 * Created by MMueller on 12/8/2016.
 */

public class ActivityListener implements Listener {
    private final String TAG = getClass().getSimpleName();
    private boolean runningState;

    private Context context;
    private ProbeManager<Probe> probeManager;
    private GoogleApiClient googleApiClient;
    private PermissionDeniedHandler permissionDeniedHandler;
    private FenceApi fenceApi;

    private ActivityFenceReceiverFactory activityFenceReceiverFactory;
    private ActivityFenceReceiver activityFenceReceiver;

    private final String FENCE_RECEIVER_ACTION = BuildConfig.APPLICATION_ID + "FENCE_RECEIVER_ACTION";

    private AwarenessFence stillFence = DetectedActivityFence.during(DetectedActivityFence.STILL);

    public static final String STILL_KEY = "STILL";

    private PendingIntent mPendingIntent;

    public ActivityListener(Context context,
                            ProbeManager<Probe> probeManager,
                            GoogleApiClient googleApiClient,
                            FenceApi fenceApi,
                            ActivityFenceReceiverFactory activityFenceReceiverFactory,
                            PermissionDeniedHandler permissionDeniedHandler){
        this.context = context;
        this.probeManager = probeManager;
        this.googleApiClient = googleApiClient;
        this.permissionDeniedHandler = permissionDeniedHandler;
        this.fenceApi = fenceApi;
        this.activityFenceReceiverFactory = activityFenceReceiverFactory;
    }

    @Override
    public void onUpdate(Probe state) {
        probeManager.save(state);
    }

    @Override
    public void startListening() throws NoSensorFoundException {
        if(!runningState){
            Intent intent = new Intent(FENCE_RECEIVER_ACTION);
            activityFenceReceiver = activityFenceReceiverFactory.create();
            context.registerReceiver(activityFenceReceiver, new IntentFilter(FENCE_RECEIVER_ACTION));

            googleApiClient.connect();
        }
        runningState = true;
    }

    @Override
    public void stopListening() {
        if(runningState){
            context.unregisterReceiver(activityFenceReceiver);
            registerFence(STILL_KEY, stillFence, googleApiClient, mPendingIntent);

            googleApiClient.disconnect();
        }
        runningState = true;
    }

    @Override
    public boolean isRunning() {
        return runningState;
    }

    protected void registerFence(final String fenceKey, final AwarenessFence fence, GoogleApiClient mGoogleApiClient, PendingIntent mPendingIntent) {
        fenceApi.updateFences(mGoogleApiClient,
                new FenceUpdateRequest.Builder()
                        .addFence(fenceKey, fence, mPendingIntent)
                        .build())
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if(status.isSuccess()) {
                            Log.i(TAG, "Fence was successfully registered.");
                            queryFence(fenceKey, googleApiClient);
                        } else {
                            Log.e(TAG, "Fence could not be registered: " + status);
                        }
                    }
                });
    }

    protected void queryFence(final String fenceKey, GoogleApiClient mGoogleApiClient) {
        Awareness.FenceApi.queryFences(mGoogleApiClient,
                FenceQueryRequest.forFences(Arrays.asList(fenceKey)))
                .setResultCallback(new ResultCallback<FenceQueryResult>() {
                    @Override
                    public void onResult(@NonNull FenceQueryResult fenceQueryResult) {
                        if (!fenceQueryResult.getStatus().isSuccess()) {
                            Log.e(TAG, "Could not query fence: " + fenceKey);
                            return;
                        }
                        FenceStateMap map = fenceQueryResult.getFenceStateMap();
                        for (String fenceKey : map.getFenceKeys()) {
                            FenceState fenceState = map.getFenceState(fenceKey);
                            Log.i(TAG, "Fence " + fenceKey + ": "
                                    + fenceState.getCurrentState()
                                    + ", was="
                                    + fenceState.getPreviousState()
                                    + ", lastUpdateTime="
                                    + fenceState.getLastFenceUpdateTimeMillis());
                        }
                    }
                });
    }

    protected void unregisterFence(final String fenceKey, GoogleApiClient mGoogleApiClient) {
        Awareness.FenceApi.updateFences(
                mGoogleApiClient,
                new FenceUpdateRequest.Builder()
                        .removeFence(fenceKey)
                        .build()).setResultCallback(new ResultCallbacks<Status>() {
            @Override
            public void onSuccess(@NonNull Status status) {
                Log.i(TAG, "Fence " + fenceKey + " successfully removed.");
            }

            @Override
            public void onFailure(@NonNull Status status) {
                Log.i(TAG, "Fence " + fenceKey + " could NOT be removed.");
            }
        });
    }
}
