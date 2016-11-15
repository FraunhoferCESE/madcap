package edu.umd.fcmd.sensorlisteners.listener.location;


/**
 * Created by MMueller on 11/14/2016.
 */

public class LocationServiceStatusReceiverFactory {

    /**
     * Create method following the factory pattern.
     * @param locationListener the locationListener
     * @return
     */
    public LocationServiceStatusReceiver create(LocationListener locationListener){
        return new LocationServiceStatusReceiver(locationListener);
    }
}
