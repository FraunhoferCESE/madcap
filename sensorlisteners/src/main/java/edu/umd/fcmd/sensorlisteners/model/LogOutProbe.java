package edu.umd.fcmd.sensorlisteners.model;

/**
 * Created by MMueller on 12/13/2016.
 * <p>
 * Model class created when the user logs out.
 */
public class LogOutProbe extends Probe {


    public LogOutProbe() {
    }

    /**
     * Gets the type of an state e.g. Accelerometer
     *
     * @return the type of state.
     */
    @Override
    public String getType() {
        return "LogOut";
    }

    @Override
    public String toString() {
        return "";
    }
}
