package edu.umd.fcmd.sensorlisteners.service;

import edu.umd.fcmd.sensorlisteners.model.Probe;

/**
 * Interface for saving Probe data.
 *
 * Created by ANepaul on 10/28/2016.
 */

public interface ProbeManager<T extends Probe> {
    void save(T state);
}
