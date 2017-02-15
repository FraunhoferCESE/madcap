package edu.umd.fcmd.sensorlisteners.model.util;

import edu.umd.fcmd.sensorlisteners.model.Probe;

/**
 * Created by MMueller on 12/13/2016.
 * <p>
 * Model class that indicates if DataCollection is
 * running or not.
 */
public class DataCollectionProbe extends Probe {
    public static final String ON = "ON";
    public static final String OFF = "OFF";
    private final String state;


    public DataCollectionProbe(String state) {
        this.state = state;
    }

    /**
     * Gets the state.
     *
     * @return DataCollectionProbe.ON or DataCollectionProbe.OFF
     */
    public String getState() {
        return state;
    }

    /**
     * Gets the type of an state e.g. Accelerometer
     *
     * @return the type of state.
     */
    @Override
    public String getType() {
        return "DataCollection";
    }

    @Override
    public String toString() {
        return "{\"state\": " + state +
                '}';
    }
}
