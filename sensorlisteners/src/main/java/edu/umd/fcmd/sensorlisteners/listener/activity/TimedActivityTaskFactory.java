package edu.umd.fcmd.sensorlisteners.listener.activity;

import com.google.android.gms.awareness.SnapshotApi;

/**
 * Created by MMueller on 12/12/2016.
 *
 * TimedActivityTaskFactory followeing the well known
 * factory pattern.
 */
public class TimedActivityTaskFactory {

    TimedActivityTask create(ActivityListener activityListener, SnapshotApi snapshotApi){
        return new TimedActivityTask(activityListener, snapshotApi);
    }
}
