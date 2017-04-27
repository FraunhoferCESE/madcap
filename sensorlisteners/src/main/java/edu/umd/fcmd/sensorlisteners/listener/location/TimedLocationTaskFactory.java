package edu.umd.fcmd.sensorlisteners.listener.location;

import com.google.android.gms.awareness.SnapshotApi;

import edu.umd.fcmd.sensorlisteners.issuehandling.PermissionsManager;
import edu.umd.fcmd.sensorlisteners.issuehandling.SensorNoAnswerReceivedHandler;

/**
 * Created by MMueller on 11/11/2016.
 *
 * A TimedLocationTask Factory following the factory pattern.
 */
public class TimedLocationTaskFactory {

    /**
     * Create method following the factory pattern.
     * @param locationListener the location listeneer to bind to.
     * @param snapshotApi the SnapshotAPI.
     * @return a new TimedLocationTask
     */

    public TimedLocationTask create(LocationListener locationListener, SnapshotApi snapshotApi, PermissionsManager permissionsManager, SensorNoAnswerReceivedHandler sensorNoAnswerReceivedHandler){
        return new TimedLocationTask(locationListener, snapshotApi, permissionsManager, sensorNoAnswerReceivedHandler);
    }
}
