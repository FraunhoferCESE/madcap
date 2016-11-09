package edu.umd.fcmd.sensorlisteners.listener;

import android.Manifest;
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
import com.google.android.gms.awareness.snapshot.internal.Snapshot;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;

import edu.umd.fcmd.sensorlisteners.NoSensorFoundException;
import edu.umd.fcmd.sensorlisteners.model.LocationState;
import edu.umd.fcmd.sensorlisteners.service.StateManager;

/**
 * Created by MMueller on 11/4/2016.
 */

public class LocationListener implements Listener<LocationState>, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = LocationListener.class.getSimpleName();

    private Context context;
    private final StateManager<LocationState> mStateManager;
    private GoogleApiClient mGoogleApiClient;
    private LocationListener.TimedLocationTask timedLocationTask;
    private TimedLocationTaskFactory timedLocationTaskFactory = new TimedLocationTaskFactory();

    /**
     * Default constructor used in projcet
     *
     * @param context       the app context.
     * @param mStateManager the StateManager to connect to.
     */
    public LocationListener(Context context, StateManager<LocationState> mStateManager) {
        this.context = context;
        this.mStateManager = mStateManager;

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Awareness.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();
    }

    /**
     * Custom constructor for testing purposes only.
     *
     * @param context          the app context.
     * @param mStateManager    the StateManager to connect to.
     * @param mGoogleApiClient a googleApi client from API Awareness.API.
     * @deprecated
     */
    public LocationListener(Context context,
                            StateManager<LocationState> mStateManager,
                            GoogleApiClient mGoogleApiClient,
                            TimedLocationTaskFactory timedLocationTaskFactory) {
        this.context = context;
        this.mStateManager = mStateManager;
        this.mGoogleApiClient = mGoogleApiClient;
        this.mGoogleApiClient.connect();
        this.timedLocationTaskFactory = timedLocationTaskFactory;
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
        timedLocationTask = timedLocationTaskFactory.getNew();
        timedLocationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void stopListening() {
        if(timedLocationTask != null){
            timedLocationTask.cancel(true);
        }
    }

    /**
     * Factory class for timed location tasks.
     */
    public class TimedLocationTaskFactory {

        /**
         * Creation method according to factory pattern.
         * @return a new TimedLocationTask.
         */
        public TimedLocationTask getNew() {
            return new LocationListener.TimedLocationTask();
        }
    }

    /**
     * A asynchronous timed location task being started to retrieve
     * a location update every 15 seconds.
     */
    public class TimedLocationTask extends AsyncTask<Void, Location, Void> {
        private SnapshotApi snapshotApi = Awareness.SnapshotApi;
        private boolean runUntilCancelled = true;

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Void doInBackground(Void... params) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                do {
                    Log.d(TAG, "Retrieving play location");
                    snapshotApi.getLocation(mGoogleApiClient)
                            .setResultCallback(new ResultCallback<LocationResult>() {
                                @Override
                                public void onResult(@NonNull LocationResult locationResult) {
                                    if (!locationResult.getStatus().isSuccess()) {
                                        Log.e(TAG, "Could not detect user location");
                                    }
                                    //Log.d(TAG, "Received update from Google "+locationResult.getLocation().toString());
                                    publishProgress(locationResult.getLocation());
                                }
                            });
                    try {
                        Thread.sleep(15000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }while (runUntilCancelled);
                return null;
            } else {
                Log.e(TAG, "NO PERMISSION");
                return null;
            }
        }

        protected void onProgressUpdate(Location... progress) {
            Location location = progress[0];
            LocationState state = new LocationState();
            state.setDate(System.currentTimeMillis());
            state.setAccuracy(location.getAccuracy());
            state.setAltitude(location.getAltitude());
            state.setBearing(location.getBearing());
            state.setExtras(location.getExtras());
            state.setLatitude(location.getLatitude());
            state.setLongitude(location.getLongitude());
            onUpdate(state);
        }

        /**
         * Setter for the Google Snapshot API.
         * @deprecated for testing purposes only.
         * @param snapshotApi
         */
        public void setSnapshotApi(SnapshotApi snapshotApi) {
            this.snapshotApi = snapshotApi;
        }

        /**
         * Setter for stopping the task after the first loop iteration.
         * @deprecated for testing purposes only.
         * @param runUntilCancelled if false, it will stop after first iteration.
         */
        public void setRunUntilCancelled(boolean runUntilCancelled) {
            this.runUntilCancelled = runUntilCancelled;
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

    public Context getContext() {
        return context;
    }

    public StateManager<LocationState> getmStateManager() {
        return mStateManager;
    }

    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }
}
