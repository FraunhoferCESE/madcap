package edu.umd.fcmd.sensorlisteners.listener.system;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;

/**
 * Created by MMueller on 12/30/2016.
 *
 * Factory class of System Receviers
 */
public class SystemReceiverFactory {

    @Inject
    public SystemReceiverFactory() {}

    public SystemReceiver create(SystemListener systemListener, Context context, SharedPreferences prefs){
        return new SystemReceiver(systemListener, context, prefs);
    }
}
