package org.fraunhofer.cese.madcap.issuehandling;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

import org.fraunhofer.cese.madcap.MyApplication;
import org.fraunhofer.cese.madcap.PermissionsManager;
import org.fraunhofer.cese.madcap.R;
import org.fraunhofer.cese.madcap.WelcomeActivity;

import javax.inject.Inject;

import edu.umd.fcmd.sensorlisteners.issuehandling.PermissionDeniedHandler;

/**
 * Created by MMueller on 11/15/2016.
 */

public class MadcapPermissionDeniedHandler implements PermissionDeniedHandler {
    private final String TAG = getClass().getSimpleName();

    private Context context;

    private boolean actionUsagePrompted;
    private boolean activityUsagePrompted;
    private boolean bluetoothPermissionPrompted;

    private static final int RUN_CODE = 998;
    private static final int NOTIFICATION_ID = 918273;

    private NotificationManager mNotificationManager;

    @Inject
    public MadcapPermissionDeniedHandler(Context context){
        this.context = context;
    }

    /**
     * To be invoked when some permission is being denied.
     *
     * @param permissionString the permission code according
     *                         to the manifest.

    @Override
    public void onPermissionDenied(String permissionString) {
        switch(permissionString){
            case Manifest.permission.ACCESS_FINE_LOCATION:
                MyApplication.madcapLogger.e(TAG, "Location permission denied");
                //Do some more things like kicking off a timer.
                break;
            case Settings.ACTION_USAGE_ACCESS_SETTINGS:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //This only works on api level 21+
                    if(!actionUsagePrompted){
                        MyApplication.madcapLogger.e(TAG, "Action usage access unavailable");
                        requestPermissionFromNotification("MADCAP requires a permission to access app information", "access_setting");
                        actionUsagePrompted = true;
                    }
                }
                break;
            case Manifest.permission.READ_CONTACTS:

                break;
            case Manifest.permission.ACCESS_WIFI_STATE:
                MyApplication.madcapLogger.e(TAG, "WiFi access permission denied");
                break;
            case Manifest.permission.BLUETOOTH:
                if(!bluetoothPermissionPrompted){
                    MyApplication.madcapLogger.e(TAG, "Bluetooth permission denied");
                    bluetoothPermissionPrompted = true;
                }
                break;
            default:
                MyApplication.madcapLogger.e(TAG, "Unknown permission denied");
                break;
        }
    }

    public void requestPermissionFromNotification(String message, String permissionFor) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.ic_stat_madcaplogo);

        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        mBuilder.setPriority(Notification.PRIORITY_HIGH);

        if (permissionFor.equalsIgnoreCase("access_setting")) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent permissionsIntent = PendingIntent.getActivity(context, NOTIFICATION_ID + 1, intent, 0);

            Intent resultIntent = new Intent(context, WelcomeActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(WelcomeActivity.class);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);

            mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));

            mBuilder.setContentTitle("MADCAP requests a permission");
            mBuilder.addAction(R.drawable.ic_stat_madcaplogo, "Settings", permissionsIntent);
            mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            mBuilder.setAutoCancel(true);
            Notification note = mBuilder.build();
            mNotificationManager.notify(RUN_CODE, note);
        } else {
            Intent intent = new Intent(context, PermissionsManager.class);
            PendingIntent permissionsIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, intent, 0);

            mBuilder.setContentTitle("MADCAP requests permissions");
            mBuilder.addAction(R.drawable.ic_stat_madcaplogo, "Settings", permissionsIntent);
            mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            mBuilder.setAutoCancel(true);
            Notification note = mBuilder.build();
            mNotificationManager.notify(RUN_CODE, note);
        }
    }*/

    public void requestPermissionFromNotification(){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.ic_stat_madcaplogo);

        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        mBuilder.setPriority(Notification.PRIORITY_HIGH);

        Intent intent = new Intent(context, PermissionsManager.class);
        PendingIntent permissionsIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, intent, 0);

        mBuilder.setContentTitle("MADCAP requests permissions");
        mBuilder.addAction(R.drawable.ic_stat_madcaplogo,"Settings", permissionsIntent);
        mBuilder.setAutoCancel(true);

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(RUN_CODE, mBuilder.build());

    }
}

