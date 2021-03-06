package edu.umd.fcmd.sensorlisteners.model.activity;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Factory class for creating ACtivity probes based on results from the SnapshotApi.
 */
@SuppressWarnings("MethodMayBeStatic")
public class ActivityProbeFactory {

    private static final double PERCENT = 100.0;


    /**
     * Explicit no-arg constructor is necessary for Dagger2 dependency injection.
     */
    @SuppressWarnings("RedundantNoArgConstructor")
    @Inject
    public ActivityProbeFactory() {
    }

    /**
     * Creates an activity probe based on a list of detected activities. }
     * Possible activity probes: vehicle, bicycle, on foot, running, still, walking and unknown.
     * @param activityList the list of detected activities. see See {@link ActivityRecognitionResult#getProbableActivities()
     * @return the new activity probe
     */
    public ActivityProbe createActivityProbe(Iterable<DetectedActivity> activityList) {

        ActivityProbe activityProbe = new ActivityProbe();
        activityProbe.setDate(System.currentTimeMillis());
        Timber.d("Fraunhofer/madcap/PhysicalActivity/probefactory/time: "+activityProbe.getDate());

        for (DetectedActivity detectedActivity : activityList) {
            int type = detectedActivity.getType();
            Timber.d("Fraunhofer/madcap/PhysicalActivity/detectedType: "+type);
            switch (type) {
                case DetectedActivity.IN_VEHICLE:
                    activityProbe.setInVehicle((double) detectedActivity.getConfidence() / PERCENT);
                    break;
                case DetectedActivity.ON_BICYCLE:
                    activityProbe.setOnBicycle((double) detectedActivity.getConfidence() / PERCENT);
                    break;
                case DetectedActivity.ON_FOOT:
                    activityProbe.setOnFoot((double) detectedActivity.getConfidence() / PERCENT);
                    break;
                case DetectedActivity.RUNNING:
                    activityProbe.setRunning((double) detectedActivity.getConfidence() / PERCENT);
                    break;
                case DetectedActivity.STILL:
                    activityProbe.setStill((double) detectedActivity.getConfidence() / PERCENT);
                    break;
                case DetectedActivity.TILTING:
                    activityProbe.setTilting((double) detectedActivity.getConfidence() / PERCENT);
                    break;
                case DetectedActivity.WALKING:
                    activityProbe.setWalking((double) detectedActivity.getConfidence() / PERCENT);
                    break;
                case DetectedActivity.UNKNOWN:
                    activityProbe.setUnknown((double) detectedActivity.getConfidence() / PERCENT);
                    break;
                case -1000:
                    break;
                default:
                    Timber.w("Nonspecific activity detected");
                    break;
            }
        }
        Timber.d("Fraunhofer/madcap/PhysicalActivity/probe: "+activityProbe);
        return activityProbe;
    }
}
