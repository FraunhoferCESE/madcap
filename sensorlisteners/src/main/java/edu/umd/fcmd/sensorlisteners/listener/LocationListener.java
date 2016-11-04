package edu.umd.fcmd.sensorlisteners.listener;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;

import edu.umd.fcmd.sensorlisteners.NoSensorFoundException;
import edu.umd.fcmd.sensorlisteners.model.AccelerometerState;
import edu.umd.fcmd.sensorlisteners.model.LocationState;
import edu.umd.fcmd.sensorlisteners.model.State;
import edu.umd.fcmd.sensorlisteners.service.StateManager;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by MMueller on 11/4/2016.
 */

public class LocationListener implements Listener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = LocationListener.class.getSimpleName();

    private Context context;
    private final StateManager<LocationState> mStateManager;

    private TimedLocationTask timedLocationTask;
    private GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(context)
            .addApi(Awareness.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build();

    public LocationListener(Context context, StateManager<LocationState> mStateManager){
        this.context = context;
        this.mStateManager = mStateManager;
    }

    @Override
    public void onUpdate(State state) {
        mStateManager.save((LocationState)state);
    }

    @Override
    public void startListening() throws NoSensorFoundException {
        timedLocationTask = new TimedLocationTask();
        timedLocationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void stopListening() {
        timedLocationTask.cancel(true);
    }

    public class TimedLocationTask extends AsyncTask<Void, LocationResult, Void>{

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
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED){
                while(true){
                    Awareness.SnapshotApi.getLocation(mGoogleApiClient)
                            .setResultCallback(new ResultCallback<LocationResult>() {
                                @Override
                                public void onResult(@NonNull LocationResult locationResult) {
                                    publishProgress(locationResult);
                                }
                            });
                    try {
                        Thread.sleep(15000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }else{
                Log.e(TAG, "NO PERMISSION");
                return null;
            }
        }

        protected void onProgressUpdate(LocationResult... progress) {
            Location location = progress[0].getLocation();
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
}
