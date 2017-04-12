package edu.umd.fcmd.sensorlisteners.listener.network;

import android.content.Context;

import javax.inject.Inject;

/**
 * Created by MMueller on 12/28/2016.
 *
 * Factory class for SMSOutObserver
 */
public class SMSOutObserverFactory {

    @Inject
    SMSOutObserverFactory() {}

    public SMSOutObserver create(NetworkListener networkListener, Context context){
        return new SMSOutObserver(null, networkListener, context);
    }
}
