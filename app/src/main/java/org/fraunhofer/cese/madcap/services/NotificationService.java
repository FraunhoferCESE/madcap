package org.fraunhofer.cese.madcap.services;

import android.service.notification.NotificationListenerService;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.support.v4.content.LocalBroadcastManager;
import org.fraunhofer.cese.madcap.services.NotificationService;

/**
 * Created by Thorfinnur Petursson on 2/8/2018.
 * This is a service that reads notifications from the user
 */

public class NotificationService extends NotificationListenerService
{
    Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    /**
     * onNotificationPosted captures and reads notifications posted by other apps
      * @param sbn
     */


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        String ticker ="-";
        String pack = "-";
        String title = "-";
        String text = "-";
        Bundle extras = sbn.getNotification().extras;

        if(sbn.getPackageName() != null)
        {
            pack = sbn.getPackageName();
        }
        if(sbn.getNotification().tickerText !=null) {
            ticker = sbn.getNotification().tickerText.toString();
        }

        if(extras.getString("android.title") != null)
        {
            title = sbn.getPackageName();
        }
        if(extras.getCharSequence("android.text") != null)
        {
            text = extras.getCharSequence("android.text").toString();
        }

        Log.i("Package",pack);
        Log.i("Ticker",ticker);
        Log.i("Title",title);
        Log.i("Text",text);
        Log.i("Thorfinnur","NotificationService!!");

    }

    /**
     * onNotificationRemoved
     * this function captures the notifications that are removed
     * from the notification status bar.
     * @param sbn
     */

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("NotificationRemoved","Notification Removed");

        String ticker ="-";
        String pack = "-";
        String title = "-";
        String text = "-";
        Bundle extras = sbn.getNotification().extras;
        if(sbn.getNotification().tickerText !=null) {
            ticker = sbn.getNotification().tickerText.toString();
        }

        if(sbn.getPackageName() != null) {
            pack = sbn.getPackageName();
        }
        if(sbn.getNotification().tickerText !=null) {
            ticker = sbn.getNotification().tickerText.toString();
        }
        if(extras.getString("android.title") != null) {
            title = extras.getString("android.title");
        }
        if(extras.getCharSequence("android.text") != null) {
            text = extras.getCharSequence("android.text").toString();
        }

        Log.i("Package Removed",pack);
        Log.i("Ticker Removed",ticker);
        Log.i("Title Removed",title);
        Log.i("Text Removed",text);

    }
}
