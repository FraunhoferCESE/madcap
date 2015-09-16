package org.fraunhofer.cese.funf_sensor.Probe.NotificationAndBroadcastHandlers;

import android.os.Bundle;
import android.util.Log;

import edu.mit.media.funf.Schedule;
import edu.mit.media.funf.probe.Probe;

/**
 * Created by MLang on 26.01.2015.
 */
@Schedule.DefaultSchedule(interval=300)
@Probe.RequiredPermissions(android.Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE)
public class NotificationProbe extends Probe.Base implements Probe.PassiveProbe {

    private static final String TAG = "NotificationProbe";

//    private final NotificationReceiver receiver = new NotificationReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//                sendData(getGson().toJsonTree(intent.getExtras()).getAsJsonObject());
//                Log.wtf("NotificationListener", "sendData() called. data = " + getGson().toJsonTree(intent.getExtras()).getAsJsonObject().toString());
//                stop();
//            }
//    };
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        Log.wtf("NotificationProbe", "onStart called.");
//        if(getContext() != null)
//        getContext().registerReceiver(receiver,new IntentFilter("com.example.SendBroadcast"));
//        //getContext().registerReceiver(receiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if(getContext() != null)
//        getContext().unregisterReceiver(receiver);
//    }
//
    public void sendData(Bundle bundle) {
        Log.i(TAG, "sendData() called. data = " + getGson().toJsonTree(bundle).getAsJsonObject().toString());
//        Set<DataListener> set = getDataListeners();
//        Log.d(TAG,"num datalisteners: "+set.size());
        sendData(getGson().toJsonTree(bundle).getAsJsonObject());
    }


}
