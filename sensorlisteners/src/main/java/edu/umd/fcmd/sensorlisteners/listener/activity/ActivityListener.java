package edu.umd.fcmd.sensorlisteners.listener.activity;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.android.gms.common.ConnectionResult;
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

public class ActivityListener implements Listener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private final String TAG = getClass().getSimpleName();
    private boolean runningState;

    private Context context;
    private ProbeManager<Probe> probeManager;
    private GoogleApiClient googleApiClient;
    private PermissionDeniedHandler permissionDeniedHandler;
    private FenceApi fenceApi;
    private SnapshotApi snapshotApi;

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
                            SnapshotApi snapshotApi,
                            ActivityFenceReceiverFactory activityFenceReceiverFactory,
                            PermissionDeniedHandler permissionDeniedHandler){
        this.context = context;
        this.probeManager = probeManager;
        this.googleApiClient = googleApiClient;
        this.permissionDeniedHandler = permissionDeniedHandler;
        this.fenceApi = fenceApi;
        this.snapshotApi = snapshotApi;
        this.activityFenceReceiverFactory = activityFenceReceiverFactory;
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
            context.unregisterReceiver(activityFenceReceiver);
            unregisterFence(STILL_KEY, googleApiClient);

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
        fenceApi.queryFences(mGoogleApiClient,
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "GoogleApiClient connected for fences");

        // Set up the PendingIntent that will be fired when the fence is triggered.
        Intent intent = new Intent(FENCE_RECEIVER_ACTION);
        mPendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        // The broadcast receiver that will receive intents when a fence is triggered.
        activityFenceReceiver = activityFenceReceiverFactory.create(snapshotApi, this, googleApiClient);
        registerFence(STILL_KEY, stillFence, googleApiClient, mPendingIntent);
        //TODO add here more fences
        context.registerReceiver(activityFenceReceiver, new IntentFilter(FENCE_RECEIVER_ACTION));
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "GoogleApiClient connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "GoogleApiClient connection failed");
    }
}
