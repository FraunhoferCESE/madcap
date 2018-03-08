package org.fraunhofer.cese.madcap.util;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import org.fraunhofer.cese.madcap.R;
import org.fraunhofer.cese.madcap.services.NotificationService;

/**
 * Created by Thorfinnur Petursson on 2/8/2018.
 * This is a service that reads notifications from the user
 */

public class NotificationCreator extends IntentService
{
    Context context;
    android.support.v7.app.NotificationCompat.Builder notification;
    private static final int uniqueID = 45619;
    public NotificationCreator(String name) {
        super(name);
    }

   public static int getNotificationId() {
       return uniqueID;
   }
    @Override
    protected void onHandleIntent(Intent intent) {

    }
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
      //  Intent intent = new Intent(context, NotificationService.class);
      //  context.startService(intent);

        notification = new android.support.v7.app.NotificationCompat.Builder(this);
        notification.setAutoCancel(true);
    }


    public void getRunNotification(Context context) {
        notification.setTicker("This is the Ticker");
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle("DANGER DANGER!");
        notification.setContentText("This is a Title!");
        notification.setSmallIcon(R.drawable.ic_account_circle_white_24dp);

        Intent intent = new Intent(this, NotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(uniqueID, notification.build());

    }
}
