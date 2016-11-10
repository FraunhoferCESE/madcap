package edu.umd.fcmd.sensorlisteners.listener.location;

/**
 * Created by MMueller on 11/10/2016.
 */

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pools;
import android.util.Log;

import com.google.android.gms.awareness.SnapshotApi;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.common.api.ResultCallback;

import edu.umd.fcmd.sensorlisteners.model.LocationState;

import static com.google.android.gms.internal.zzs.TAG;

import java.util.logging.Logger;

/**
 * A asynchronous timed location task being started to retrieve
 * a location update every 15 seconds.
 */
class TimedLocationTask extends AsyncTask<Void, Location, Void> {
    private final String TAG = this.getClass().getSimpleName();
    public static final int LOCATION_SLEEP_TIME = 1500;
    private SnapshotApi snapshotApi;
    private boolean runUntilCancelled = true;
    private LocationListener locationListener;

    private Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    static TimedLocationTask create(LocationListener locationListener, SnapshotApi snapshotApi) {
        return new TimedLocationTask(locationListener, snapshotApi);
    }

    private TimedLocationTask(LocationListener locationListener, SnapshotApi snapshotApi) {
        if (locationListener != null && snapshotApi != null) {
            this.locationListener = locationListener;
            this.snapshotApi = snapshotApi;
        } else {
            throw new NullPointerException();
        }
    }

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
        if (ContextCompat.checkSelfPermission(locationListener.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            do {
                snapshotApi.getLocation(locationListener.getmGoogleApiClient())
                        .setResultCallback(new ResultCallback<LocationResult>() {
                            @Override
                            public void onResult(@NonNull LocationResult locationResult) {
                                if (!locationResult.getStatus().isSuccess()) {
                                    publishProgress(locationResult.getLocation());
                                    Log.d(TAG, "here");
                                }
                            }
                        });
                try {
                    Thread.sleep(LOCATION_SLEEP_TIME);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } while (runUntilCancelled);
            return null;
        } else {
            Log.d(TAG, "No permission");
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
        locationListener.onUpdate(state);
    }

    /**
     * Setter for stopping the task after the first loop iteration.
     *
     * @param runUntilCancelled if false, it will stop after first iteration.
     * @deprecated for testing purposes only.
     */
    protected void setRunUntilCancelled(boolean runUntilCancelled) {
        this.runUntilCancelled = runUntilCancelled;
    }

}