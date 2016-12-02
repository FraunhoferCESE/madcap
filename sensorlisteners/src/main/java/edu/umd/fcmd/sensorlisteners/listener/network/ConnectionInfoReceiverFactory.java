package edu.umd.fcmd.sensorlisteners.listener.network;

/**
 * Created by MMueller on 12/2/2016.
 */

public class ConnectionInfoReceiverFactory {

    public ConnectionInfoReceiver create(NetworkListener networkListener){
        return  new ConnectionInfoReceiver(networkListener);
    }
}
