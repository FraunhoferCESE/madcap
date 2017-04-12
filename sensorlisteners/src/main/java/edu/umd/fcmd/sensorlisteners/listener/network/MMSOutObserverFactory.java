package edu.umd.fcmd.sensorlisteners.listener.network;

import javax.inject.Inject;

/**
 * Created by MMueller on 12/28/2016.
 *
 * Factory class for SMSOutObserver
 */
public class MMSOutObserverFactory {

    @Inject
    MMSOutObserverFactory() {}


    public MMSOutObserver create(NetworkListener networkListener){
        return new MMSOutObserver(null, networkListener);
    }
}
