package edu.umd.fcmd.sensorlisteners.listener;

import edu.umd.fcmd.sensorlisteners.NoSensorFoundException;
import edu.umd.fcmd.sensorlisteners.model.Probe;

/**
 * Created by ANepaul on 10/28/2016.
 */

public interface Listener<T extends Probe> {
    void onUpdate(T state);
    void startListening() throws NoSensorFoundException;
    void stopListening();
}

