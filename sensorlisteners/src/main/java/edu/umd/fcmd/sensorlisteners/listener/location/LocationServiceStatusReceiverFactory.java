package edu.umd.fcmd.sensorlisteners.listener.location;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by MMueller on 11/14/2016.
 */

public class LocationServiceStatusReceiverFactory {

    public LocationServiceStatusReceiver create(LocationListener locationListener, GoogleApiClient mGoogleApiClient){
        return new LocationServiceStatusReceiver(locationListener, mGoogleApiClient);
    }
}
