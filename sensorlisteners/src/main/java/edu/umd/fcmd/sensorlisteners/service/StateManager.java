package edu.umd.fcmd.sensorlisteners.service;

import java.util.List;

import edu.umd.fcmd.sensorlisteners.model.State;

/**
 * Created by ANepaul on 10/28/2016.
 */

public interface StateManager<T extends State> {
    void save(T state);
    List<T> getAll();
}
