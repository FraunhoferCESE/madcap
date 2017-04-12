package edu.umd.fcmd.sensorlisteners.model.system;

/**
 * Created by llayman on 4/12/2017.
 */

public interface BuildVersionProvider {

    /**
     * Returns the current build version of the Android app to be uploaded with the system listener
     *
     * @return A string indicating the build version
     */
    public String getBuildVersion();
}
