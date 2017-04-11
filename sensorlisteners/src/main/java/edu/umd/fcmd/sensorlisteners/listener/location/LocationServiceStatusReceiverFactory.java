package edu.umd.fcmd.sensorlisteners.listener.location;


import javax.inject.Inject;

/**
 * Created by MMueller on 11/14/2016.
 *
 * Factory class for LocationServiceStatusReceiver
 */

@SuppressWarnings("MethodMayBeStatic")
public class LocationServiceStatusReceiverFactory {

    /**
     * No argument constructor required for dependency injection
     */
    @Inject
    public LocationServiceStatusReceiverFactory() {}

    /**
     * Create method following the factory pattern.
     * @param locationListener the locationListener
     * @return the created LocationServiceStatusReceiver.
     */
    public LocationServiceStatusReceiver create(LocationListener locationListener){
        return new LocationServiceStatusReceiver(locationListener);
    }
}
