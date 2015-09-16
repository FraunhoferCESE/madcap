package org.fraunhofer.cese.funf_sensor.Probe.NotificationAndBroadcastHandlers;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by MLang on 22.01.2015.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationService extends NotificationListenerService {

    private Context context;

    public static final String ACTION = "org.fraunhofer.funf.NotificationReceivedBroadcast";

    private static final String TAG = "NotificationService";


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Log.d(TAG, "Starting notification service");
//        receiver = new NotificationReceiver();
//        IntentFilter filter = new IntentFilter();
//        filter.addAction("com.example.SendBroadcast");
//        registerReceiver(receiver, filter);
    }

    //    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        unregisterReceiver(receiver);
//    }



    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        /*
        When a notification is posted on the device, this method is called.
        This is working already. We can get its data that we want to use for the storage.
        This is done as an intent here. Now we have to make FUNF storing this data in the database.
         */
//        Log.i(TAG, "ID :" + sbn.getId() + " " + sbn.getNotification().tickerText + " " + sbn.getPackageName());

        Intent i = new Intent(ACTION);
        i.putExtra("notification_event", "onNotificationPosted :" + sbn.getPackageName() + "n");
        i.putExtra("notification_trigger", sbn.getPackageName());
        i.putExtra("notification_message", sbn.getNotification().tickerText);
        i.putExtra("timestamp", System.currentTimeMillis());
        LocalBroadcastManager.getInstance(context).sendBroadcast(i);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        /*
        When a notification is removed on the device, this method is called.
        This is working already. We can get its data that we want to use for the storage.
        This is done as an intent here. Now we have to make FUNF storing this data in the database.
         */
//        Log.i(TAG, "ID :" + sbn.getId() + " " + sbn.getNotification().tickerText + " " + sbn.getPackageName());

        Intent i = new Intent(ACTION);
        i.putExtra("notification_event", "onNotificationRemoved :" + sbn.getPackageName() + "n");
        i.putExtra("notification_trigger", sbn.getPackageName());
        i.putExtra("notification_message", sbn.getNotification().tickerText);
        i.putExtra("timestamp", System.currentTimeMillis());
        LocalBroadcastManager.getInstance(context).sendBroadcast(i);
    }
}
