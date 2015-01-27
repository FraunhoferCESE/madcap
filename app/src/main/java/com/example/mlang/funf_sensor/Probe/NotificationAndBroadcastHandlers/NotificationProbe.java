package com.example.mlang.funf_sensor.Probe.NotificationAndBroadcastHandlers;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import edu.mit.media.funf.Schedule;
import edu.mit.media.funf.probe.Probe;

/**
 * Created by MLang on 26.01.2015.
 */
@Schedule.DefaultSchedule(interval=300)
@Probe.RequiredPermissions(android.Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE)
public class NotificationProbe extends Probe.Base implements Probe.ContinuousProbe{

    private final NotificationReceiver receiver = new NotificationReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                sendData(getGson().toJsonTree(intent.getExtras()).getAsJsonObject());
                Log.wtf("NotificationListener", "sendData() called. data = " + getGson().toJsonTree(intent.getExtras()).getAsJsonObject().toString());
                stop();
            }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Log.wtf("NotificationProbe", "onStart called.");
        if(getContext() != null)
        getContext().registerReceiver(receiver,new IntentFilter("com.example.SendBroadcast"));
        //getContext().registerReceiver(receiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(getContext() != null)
        getContext().unregisterReceiver(receiver);
    }

    public void sendData(Intent intent) {
        Log.wtf("NotificationListener", "sendData() called. data = " + getGson().toJsonTree(intent.getExtras()).getAsJsonObject().toString());
        sendData(getGson().toJsonTree(intent.getExtras()).getAsJsonObject());
    }
}
