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
import android.support.v4.content.ContextCompat;

import org.fraunhofer.cese.madcap.MyApplication;
import org.fraunhofer.cese.madcap.PermissionsManager;
import org.fraunhofer.cese.madcap.R;

import javax.inject.Inject;

import edu.umd.fcmd.sensorlisteners.issuehandling.PermissionDeniedHandler;
import timber.log.Timber;

/**
 * Created by MMueller on 11/15/2016.
 */

public class MadcapPermissionDeniedHandler implements PermissionDeniedHandler {
    private final String TAG = getClass().getSimpleName();

    private Context context;

    private boolean actionUsagePrompted;
    private boolean activityUsagePrompted;
    private boolean bluetoothPermissionPrompted;

    private static final int RUN_CODE = 1;
    private static final int NOTIFICATION_ID = 918273;
    private static final int REQUEST_CODE = 996 ;

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
     */
    @Override
    public void onPermissionDenied(String permissionString) {
        switch(permissionString){
            case Manifest.permission.ACCESS_FINE_LOCATION:
                Timber.e("Location permission denied");
                //Do some more things like kicking off a timer.
                break;
            case Settings.ACTION_USAGE_ACCESS_SETTINGS:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //This only works on api level 21+
                    if(!actionUsagePrompted){
                        Timber.e("Action usage access denied");
                        requestPermissionFromNotification("MADCAP requires a permission to access app information", "access_setting");
                        actionUsagePrompted = true;
                    }
                }
                break;
            case Manifest.permission.READ_CONTACTS:

                break;
            case Manifest.permission.ACCESS_WIFI_STATE:
                Timber.e("WiFi access permission denied");
                break;
            case Manifest.permission.BLUETOOTH:
                if(!bluetoothPermissionPrompted){
                    Timber.e("Bluetooth permission deniedd");
                    bluetoothPermissionPrompted = true;
                }
                break;
            default:
                Timber.e("Unknown permission denied");
                break;
        }
    }

    public void requestPermissionFromNotification(String message, String permissionFor){

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.ic_stat_madcaplogo);
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        mBuilder.setPriority(Notification.PRIORITY_HIGH);

        if(permissionFor.equalsIgnoreCase("access_setting")){
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent permissionsIntent = PendingIntent.getActivity(context, NOTIFICATION_ID+1, intent, 0);

            mBuilder.setContentTitle("MADCAP requests a permission");
            mBuilder.addAction(R.drawable.ic_stat_madcaplogo,"Settings",permissionsIntent);
            mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            Notification note = mBuilder.build();
            note.flags |= Notification.FLAG_AUTO_CANCEL;
            mNotificationManager.notify(RUN_CODE, note);
        }else{
            Intent intent = new Intent(context, PermissionsManager.class);
            PendingIntent permissionsIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, intent, 0);

            mBuilder.setContentTitle("MADCAP requests permissions");
            mBuilder.addAction(R.drawable.ic_stat_madcaplogo,"Settings",permissionsIntent);
            mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            Notification note = mBuilder.build();
            note.flags |= Notification.FLAG_AUTO_CANCEL;
            mNotificationManager.notify(RUN_CODE+1, note);
        }


    }
}
