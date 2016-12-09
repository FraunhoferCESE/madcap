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

    private final Context context;
    private final ProbeManager<Probe> probeManager;
    private final GoogleApiClient googleApiClient;
    private final PermissionDeniedHandler permissionDeniedHandler;
    private final FenceApi fenceApi;
    private final SnapshotApi snapshotApi;

    private ActivityFenceReceiverFactory activityFenceReceiverFactory;
    private ActivityFenceReceiver activityFenceReceiver;

    private final String FENCE_RECEIVER_ACTION = BuildConfig.APPLICATION_ID + "FENCE_RECEIVER_ACTION";

    private final AwarenessFence stillFence = DetectedActivityFence.during(DetectedActivityFence.STILL);
    private final AwarenessFence inVehicleFence = DetectedActivityFence.during(DetectedActivityFence.IN_VEHICLE);
    private final AwarenessFence onBicycleFence = DetectedActivityFence.during(DetectedActivityFence.ON_BICYCLE);
    private final AwarenessFence onFootFence = DetectedActivityFence.during(DetectedActivityFence.ON_FOOT);
    private final AwarenessFence runningFence = DetectedActivityFence.during(DetectedActivityFence.RUNNING);
    private final AwarenessFence tiltingFence = DetectedActivityFence.during(DetectedActivityFence.TILTING);
    private final AwarenessFence walkingFence = DetectedActivityFence.during(DetectedActivityFence.WALKING);
    private final AwarenessFence unknownFence = DetectedActivityFence.during(DetectedActivityFence.UNKNOWN);

    static final String STILL_KEY = "STILL";
    static final String IN_VEHICLE_KEY = "IN_VEHICLE";
    static final String ON_BICYCLE_KEY = "ON_BICYCLE";
    static final String ON_FOOT_KEY = "ON_FOOT";
    static final String RUNNING_KEY = "RUNNING";
    static final String TILTING_KEY = "TILTING";
    static final String WALKING_KEY = "WALKING";
    static final String UNKOWN_KEY = "UNKNOWN";

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
            unregisterFence(ON_BICYCLE_KEY, googleApiClient);
            unregisterFence(IN_VEHICLE_KEY, googleApiClient);
            unregisterFence(ON_FOOT_KEY, googleApiClient);
            unregisterFence(RUNNING_KEY, googleApiClient);
            unregisterFence(TILTING_KEY, googleApiClient);
            unregisterFence(WALKING_KEY, googleApiClient);
            unregisterFence(UNKOWN_KEY, googleApiClient);

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
        registerFence(ON_BICYCLE_KEY, onBicycleFence, googleApiClient, mPendingIntent);
        registerFence(IN_VEHICLE_KEY, inVehicleFence, googleApiClient, mPendingIntent);
        registerFence(ON_FOOT_KEY, onFootFence, googleApiClient, mPendingIntent);
        registerFence(RUNNING_KEY, runningFence, googleApiClient, mPendingIntent);
        registerFence(TILTING_KEY, tiltingFence, googleApiClient, mPendingIntent);
        registerFence(WALKING_KEY, walkingFence, googleApiClient, mPendingIntent);
        registerFence(UNKOWN_KEY, unknownFence, googleApiClient, mPendingIntent);

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
