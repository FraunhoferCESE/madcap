package com.example.mlang.funf_sensor.Probe.NotificationAndBroadcastHandlers;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.example.mlang.funf_sensor.Probe.NotificationAndBroadcastHandlers.NotificationProbe;
import com.example.mlang.funf_sensor.Probe.NotificationAndBroadcastHandlers.NotificationReceiver;

/**
 * Created by MLang on 22.01.2015.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationService extends NotificationListenerService {

    private String TAG = this.getClass().getSimpleName();
    private NotificationReceiver receiver;
    private NotificationProbe probe = new NotificationProbe();

    @Override
    public void onCreate() {
        super.onCreate();
        receiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.SendBroadcast");
        registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        /*
        When a notification is posted on the device, this method is called.
        This is working already. We can get its data that we want to use for the storage.
        This is done as an intent here. Now we have to make FUNF storing this data in the database.
         */
        //super.onNotificationPosted(sbn);
        Log.i(TAG, "**********  onNotificationPosted");
        Log.i(TAG, "ID :" + sbn.getId() + " " + sbn.getNotification().tickerText + " " + sbn.getPackageName());
        Intent i = new Intent("com.example.SendBroadcast");
        i.putExtra("notification_event", "onNotificationPosted :" + sbn.getPackageName() + "n");
        i.putExtra("notification_trigger", sbn.getPackageName());
        i.putExtra("notification_message", sbn.getNotification().tickerText);
        i.putExtra("timestamp", System.currentTimeMillis());
        //probe.sendData(i);
        sendBroadcast(i);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        /*
        When a notification is removed on the device, this method is called.
        This is working already. We can get its data that we want to use for the storage.
        This is done as an intent here. Now we have to make FUNF storing this data in the database.
         */
        Log.i(TAG, "********** onNotificationRemoved");
        Log.i(TAG, "ID :" + sbn.getId() + " " + sbn.getNotification().tickerText + " " + sbn.getPackageName());
        Intent i = new Intent("com.example.SendBroadcast");
        i.putExtra("notification_event", "onNotificationRemoved :" + sbn.getPackageName() + "n");
        i.putExtra("notification_trigger", sbn.getPackageName());
        i.putExtra("notification_message", sbn.getNotification().tickerText);
        i.putExtra("timestamp", System.currentTimeMillis());
        //probe.sendData(i);
        sendBroadcast(i);
    }
}
