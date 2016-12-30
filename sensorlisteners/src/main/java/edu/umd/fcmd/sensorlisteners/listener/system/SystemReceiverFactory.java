package edu.umd.fcmd.sensorlisteners.listener.system;

import android.content.Context;

/**
 * Created by MMueller on 12/30/2016.
 *
 * Factory class of System Receviers
 */
public class SystemReceiverFactory {

    public SystemReceiver create(SystemListener systemListener, Context context){
        return new SystemReceiver(systemListener, context);
    }
}
