package org.fraunhofer.cese.madcap.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.fraunhofer.cese.madcap.R;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by TPetursson on 3/9/2018.
 */

public class NotificationPostingService extends Service {
    private NotificationManager mNM;

    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private int NOTIFICATION;

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        NotificationPostingService getService() {
            return NotificationPostingService.this;
        }
    }

    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        //The NotificationID class is used to make sure that each notification gets a unique ID
        NotificationPostingService.NotificationID notificationID = new NotificationPostingService.NotificationID();
        NOTIFICATION = notificationID.getID();
        showNotification();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        mNM.cancel(NOTIFICATION);
    }

    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new NotificationPostingService.LocalBinder();

    /**
     * Show a notification while this service is running.
     */
    public class NotificationID {
        private final AtomicInteger c = new AtomicInteger(0);
        public int getID() {
            return c.incrementAndGet();
        }
    }

    private void showNotification() {
        NotificationService notificationService = new NotificationService();

            // In this sample, we'll use the same text for the ticker and the expanded notification
            // The PendingIntent to launch our activity if the user selects this notification
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, WifiService.class), 0);
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            // Set the info for the views that show in the notification panel.
            Notification notification = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.madcaplogo2)  // the status icon
                    .setTicker("Open Wifi")  // the status text
                    .setWhen(System.currentTimeMillis())  // the time stamp
                    .setContentTitle("Open Wifi")  // the label of the entry
                    .setContentText("You are connected to an open wifi")  // the contents of the entry
                    .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                    .setSound(alarmSound)
                    .build();

            // Send the notification.
            mNM.notify(NOTIFICATION, notification);
    }

}
