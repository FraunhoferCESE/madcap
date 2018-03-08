package org.fraunhofer.cese.madcap.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.fraunhofer.cese.madcap.R;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Thorfinnur Petursson on 2/8/2018.
 * This is a service that reads notifications from the user
 */


public class NotificationService extends Service {
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
        NotificationService getService() {
            return NotificationService.this;
        }
    }

    @Override
    public void onCreate() {
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        NotificationID notificationID = new NotificationID();
        NOTIFICATION = notificationID.getID();
        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        NotificationID notificationID = new NotificationID();
        NOTIFICATION = notificationID.getID();
        showNotification();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        mNM.cancel(NOTIFICATION);

        // Tell the user we stopped.

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

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
        // In this sample, we'll use the same text for the ticker and the expanded notification


        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, WifiService.class), 0);

        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.madcaplogo2)  // the status icon
                .setTicker("Open Wifi")  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle("Open Wifi")  // the label of the entry
                .setContentText("You are connected to an open wifi")  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .build();

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }

}


    /*
    Context context;
    android.support.v7.app.NotificationCompat.Builder notification;
    private static final int uniqueID = 45619;
    //  openWifiNotification openWifiNotification = new openWifiNotification();

 /*   public void createNotification(){
        Intent intent = new Intent(getApplicationContext(), NotificationService.class);
        intent.setAction(CommonConstants.ACTION_SNOOZE);
        startService(intent);
        onCreate();
        getRunNotification(intent);
        }

     public interface ServiceCallbacks {
         void doSomething();
     }

    @Override
    public void onCreate() {
        super.onCreate();
        Intent intent = new Intent(context, NotificationService.class);
        context.startService(intent);

      //  context = getApplicationContext();
        notification = new android.support.v7.app.NotificationCompat.Builder(this);
        notification.setAutoCancel(true);
    }


     * onNotificationPosted captures and reads notifications posted by other apps
      * @param sbn



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

    }

    /**
     * onNotificationRemoved
     * this function captures the notifications that are removed
     * from the notification status bar.
     * @param sbn


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

     //   getRunNotification();
    }
    public void getRunNotification() {
        //long uniqueID = System.currentTimeMillis();
       // int randomID = toIntExact(uniqueID);
        onCreate();


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
*/