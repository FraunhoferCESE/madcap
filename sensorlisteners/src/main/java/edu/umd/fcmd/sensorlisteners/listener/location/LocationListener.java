package edu.umd.fcmd.sensorlisteners.listener.location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.SnapshotApi;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.ActivityRecognition;

import edu.umd.fcmd.sensorlisteners.NoSensorFoundException;
import edu.umd.fcmd.sensorlisteners.listener.Listener;
import edu.umd.fcmd.sensorlisteners.model.LocationState;
import edu.umd.fcmd.sensorlisteners.service.StateManager;

/**
 * Created by MMueller on 11/4/2016.
 */

public class LocationListener implements Listener<LocationState>, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = LocationListener.class.getSimpleName();

    private final Context context;
    private final StateManager<LocationState> mStateManager;
    private SnapshotApi snapshotApi;

    private TimedLocationTask timedLocationTask;
    private final GoogleApiClient mGoogleApiClient;

    /**
     * Default constructor which should be used.
     *
     * @param context          the app context.
     * @param mStateManager    the StateManager to connect to.
     * @param builder          a googleApiCient builder
     * @param snapshotApi      usally the Awarness.SnapshotsApi
     */
    public LocationListener(Context context,
                            StateManager<LocationState> mStateManager,
                            GoogleApiClient.Builder builder,
                            SnapshotApi snapshotApi) {
        this.context = context;
        this.mStateManager = mStateManager;
        mGoogleApiClient = builder
                .addApi(Awareness.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
        this.snapshotApi = snapshotApi;
    }

    /**
     * Being called when a new update is available.
     * @param state location update state.
     */
    @Override
    public void onUpdate(LocationState state) {
        mStateManager.save(state);
    }

    /**
     * Starts listening to frquent location updates.
     * @throws NoSensorFoundException when the connection to the GoogleApi client fails.
     */
    @Override
    public void startListening() throws NoSensorFoundException {
        timedLocationTask = TimedLocationTask.create(this, snapshotApi);
        timedLocationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void stopListening() {
        if(timedLocationTask != null){
            timedLocationTask.cancel(true);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e(TAG, "Conntected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "Conntection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Conntection failed");
    }

    protected Context getContext() {
        return context;
    }

    protected StateManager<LocationState> getmStateManager() {
        return mStateManager;
    }

    protected GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }
}
