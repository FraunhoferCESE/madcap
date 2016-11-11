package edu.umd.fcmd.sensorlisteners.listener.location;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.awareness.SnapshotApi;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import edu.umd.fcmd.sensorlisteners.NoSensorFoundException;
import edu.umd.fcmd.sensorlisteners.listener.Listener;
import edu.umd.fcmd.sensorlisteners.model.LocationState;
import edu.umd.fcmd.sensorlisteners.service.StateManager;

/**
 * Created by MMueller on 11/4/2016.
 *
 * A listener for Locations. Retrieving updates in a certain defined period.
 */

@SuppressWarnings("ClassNamePrefixedWithPackageName")
public class LocationListener implements Listener<LocationState>, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = LocationListener.class.getSimpleName();

    private final Context context;
    private final StateManager<LocationState> mStateManager;
    private final SnapshotApi snapshotApi;

    private final TimedLocationTaskFactory timedLocationTaskFactory;
    private TimedLocationTask timedLocationTask;
    private final GoogleApiClient mGoogleApiClient;

    /**
     * Default constructor which should be used.
     *
     * @param context          the app context.
     * @param mStateManager    the StateManager to connect to.
     * @param mGoogleApiClient  a GoogleApi client.
     * @param snapshotApi      usally the Awarness.SnapshotsApi
     */
    public LocationListener(Context context,
                            StateManager<LocationState> mStateManager,
                            GoogleApiClient mGoogleApiClient,
                            SnapshotApi snapshotApi,
                            TimedLocationTaskFactory timedTaskFactory) {
        this.context = context;
        this.mStateManager = mStateManager;
        this.mGoogleApiClient = mGoogleApiClient;
        this.snapshotApi = snapshotApi;
        timedLocationTaskFactory = timedTaskFactory;
        //noinspection ThisEscapedInObjectConstruction
        mGoogleApiClient.registerConnectionCallbacks(this);
        //noinspection ThisEscapedInObjectConstruction
        mGoogleApiClient.registerConnectionFailedListener(this);

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
     * Starts listening to frequent location updates.
     * @throws NoSensorFoundException when the connection to the GoogleApi client fails.
     */
    @Override
    public void startListening() throws NoSensorFoundException {
        mGoogleApiClient.connect();
        timedLocationTask = timedLocationTaskFactory.create(this, snapshotApi);
        timedLocationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void stopListening() {
        if (timedLocationTask != null) {
            Log.d(TAG, "Timed location task is not null");
            timedLocationTask.cancel(true);
            mGoogleApiClient.disconnect();
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

    protected GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }
}
