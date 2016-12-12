package edu.umd.fcmd.sensorlisteners.listener.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.awareness.SnapshotApi;
import com.google.android.gms.awareness.snapshot.DetectedActivityResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

import edu.umd.fcmd.sensorlisteners.model.ActivityProbe;

/**
 * Created by MMueller on 12/9/2016.
 *
 * Get the activity in a certain time period and evaultes if there
 * is a significant change to the last detected activity.
 */
class TimedActivityTask extends AsyncTask<Void, ActivityRecognitionResult, Void> {
    private final String TAG = getClass().getSimpleName();
    private static final int ACTIVITY_SLEEP_TIME = 5000;

    private final ActivityListener activityListener;
    private final SnapshotApi snapshotApi;

    private ActivityProbe lastActivityProbe;

    TimedActivityTask(ActivityListener activityListener, SnapshotApi snapshotApi) {
        this.activityListener = activityListener;
        this.snapshotApi = snapshotApi;
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
        Log.d(TAG, "Doing in background");
        while (!isCancelled()) {
            snapshotApi.getDetectedActivity(activityListener.getGoogleApiClient())
                    .setResultCallback(new ResultCallback<DetectedActivityResult>() {
                        @Override
                        public void onResult(@NonNull DetectedActivityResult detectedActivityResult) {
                            if (!detectedActivityResult.getStatus().isSuccess()) {
                                Log.e(TAG, "Could not get the current activity.");
                                return;
                            }
                            ActivityRecognitionResult ar = detectedActivityResult.getActivityRecognitionResult();
                            publishProgress(ar);
                        }
                    });
            Log.d(TAG, "Attempt to get activity");
            try {
                //Log.d(TAG, "Sleep now");
                //noinspection BusyWait
                Thread.sleep((long) ACTIVITY_SLEEP_TIME);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
                Log.d(TAG, "Sleep has been tried to interrupt, but thread interrupted the interrupting Thread.");
            }
        }

        return null;
    }

    /**
     * Runs on the UI thread after {@link #publishProgress} is invoked.
     * The specified values are the values passed to {@link #publishProgress}.
     *
     * @param values The values indicating progress.
     * @see #publishProgress
     * @see #doInBackground
     */
    @Override
    protected void onProgressUpdate(ActivityRecognitionResult... values) {
        ActivityRecognitionResult activityRecognitionResult = values[0];
        List<DetectedActivity> activityList = activityRecognitionResult.getProbableActivities();
        //Log.d(TAG, "List "+activityList.toString()+" has size "+activityList.size());

        ActivityProbe activityProbe = new ActivityProbe();
        activityProbe.setDate(System.currentTimeMillis());

        for(DetectedActivity detectedActivity : activityList){
            int type = detectedActivity.getType();

            switch(type){
                case DetectedActivity.IN_VEHICLE:
                    activityProbe.setInVehicle(((double) detectedActivity.getConfidence())/100);
                    break;
                case DetectedActivity.ON_BICYCLE:
                    activityProbe.setOnBicycle(((double) detectedActivity.getConfidence())/100);
                    break;
                case DetectedActivity.ON_FOOT:
                    activityProbe.setOnFoot(((double) detectedActivity.getConfidence())/100);
                    break;
                case DetectedActivity.RUNNING:
                    activityProbe.setRunning(((double) detectedActivity.getConfidence())/100);
                    break;
                case DetectedActivity.STILL:
                    activityProbe.setStill(((double) detectedActivity.getConfidence())/100);
                    break;
                case DetectedActivity.TILTING:
                    activityProbe.setTilting(((double) detectedActivity.getConfidence())/100);
                    break;
                case DetectedActivity.WALKING:
                    activityProbe.setWalking(((double) detectedActivity.getConfidence())/100);
                    break;
                case DetectedActivity.UNKNOWN:
                    activityProbe.setUnknown(((double) detectedActivity.getConfidence())/100);
                    break;
                case -1000:
                    break;
                default:
                    Log.e(TAG, "Incorrect Activity detected");
                    break;
            }
        }

        createIfSignificant(activityProbe, lastActivityProbe);

    }

    /**
     * Method for checking if there is a siginificant deviation from
     * the last detected activity.
     * @param newActivityProbe the last detected activity.
     * @param oldActivityProbe the currently detected activity.
     */
    private void createIfSignificant(ActivityProbe newActivityProbe, ActivityProbe oldActivityProbe){
        if(!newActivityProbe.equals(oldActivityProbe)){
            lastActivityProbe = newActivityProbe;
            activityListener.onUpdate(newActivityProbe);
        }
    }
}
