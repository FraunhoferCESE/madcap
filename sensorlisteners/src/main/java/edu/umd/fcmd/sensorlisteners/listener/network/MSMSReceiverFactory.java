package edu.umd.fcmd.sensorlisteners.listener.network;

import javax.inject.Inject;

/**
 * Created by MMueller on 12/28/2016.
 *
 * Factory class for MSMSReceiver following the factory pattern.
 */
public class MSMSReceiverFactory {

    @Inject
    public MSMSReceiverFactory() {}


    public MSMSReceiver create(NetworkListener networkListener){
        return new MSMSReceiver(networkListener);
    }
}
