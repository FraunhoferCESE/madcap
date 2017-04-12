package edu.umd.fcmd.sensorlisteners.listener.network;

import javax.inject.Inject;

/**
 * Created by MMueller on 12/2/2016.
 */

public class ConnectionInfoReceiverFactory {

    @Inject
    public ConnectionInfoReceiverFactory() {}


    public ConnectionInfoReceiver create(NetworkListener networkListener){
        return  new ConnectionInfoReceiver(networkListener);
    }
}
