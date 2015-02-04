package com.example.mlang.funf_sensor.Probe.NotificationAndBroadcastHandlers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

/**
 * Created by MLang on 22.01.2015.
 */
public class NotificationReceiver extends android.content.BroadcastReceiver {
    /*
    When a broadcast with an intent defined in the AndroidManifest is received here,
    we make it being displayed on the device.
     */

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = "Broadcast intent detected "
                + intent.getAction();

        Toast.makeText(context, message,
                Toast.LENGTH_LONG).show();

        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

        //notificationProbe.sendData(intent);

        /*Log.wtf(TAG, "onReceived called");
        if (intent.getStringExtra("command").equals("clearall")) {
            NotificationListenerServiceProbe.this.cancelAllNotifications();
        } else if (intent.getStringExtra("command").equals("list")) {
            Intent i1 = new Intent("android.intent.action.MAIN");
            i1.putExtra("notification_event", "=====================");
            sendBroadcast(i1);
            int i = 1;
            for (StatusBarNotification sbn : NotificationListenerServiceProbe.this.getActiveNotifications()) {
                Intent i2 = new Intent("android.intent.action.MAIN");
                i2.putExtra("notification_event", i + " " + sbn.getPackageName() + "\n");
                sendBroadcast(i2);
                i++;
            }
            Intent i3 = new Intent("android.intent.action.MAIN");
            i3.putExtra("notification_event", "===== Notification List ====");
            sendBroadcast(i3);

        }*/

    }
}
