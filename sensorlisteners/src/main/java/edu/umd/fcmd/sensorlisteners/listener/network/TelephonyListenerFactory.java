package edu.umd.fcmd.sensorlisteners.listener.network;

import android.content.Context;

/**
 * Created by MMueller on 12/22/2016.
 */

public class TelephonyListenerFactory {

    public TelephonyListener create(Context context, NetworkListener networkListener){
        return new TelephonyListener(context, networkListener);
    }
}
