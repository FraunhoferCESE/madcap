package org.fraunhofer.cese.madcap.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.service.notification.NotificationListenerService;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.support.v4.content.LocalBroadcastManager;



import org.fraunhofer.cese.madcap.R;
import org.fraunhofer.cese.madcap.WelcomeActivity;
import org.fraunhofer.cese.madcap.services.NotificationService;

/**
 * Created by Thorfinnur Petursson on 2/8/2018.
 * This is a service that reads notifications from the user
 */

public class NotificationService extends NotificationListenerService
{
    Context context;
    android.support.v7.app.NotificationCompat.Builder nottification;
    private static final int uniqueID = 45612;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        nottification = new android.support.v7.app.NotificationCompat.Builder(this);
        nottification.setAutoCancel(true);
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

        getRunNotification();
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

        getRunNotification();
    }
    public void getRunNotification() {


        nottification.setSmallIcon(R.drawable.ic_account_circle_white_24dp);
        nottification.setTicker("This is the Ticker");
        nottification.setWhen(System.currentTimeMillis());
        nottification.setContentTitle("DANGER DANGER!");
        nottification.setContentText("This is a Title!");

        Intent intent = new Intent(this, NotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent, PendingIntent.FLAG_UPDATE_CURRENT);
        nottification.setContentIntent(pendingIntent);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(uniqueID, nottification.build());




        /*
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.ic_stat_madcaplogo);
        // TODO: Refactor
        mBuilder.setContentTitle("Notification Danger wifi");
        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        mBuilder.setPriority(Notification.PRIORITY_LOW);
        mBuilder.setContentText("Hello World!");

    */

        ;
    }
}
