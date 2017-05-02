package org.fraunhofer.cese.madcap.issuehandling;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import org.fraunhofer.cese.madcap.PermissionsActivity;
import org.fraunhofer.cese.madcap.R;

import javax.inject.Inject;

import edu.umd.fcmd.sensorlisteners.issuehandling.PermissionsManager;

/**
 * Created by MMueller on 11/15/2016.
 *
 * Edited by PGuruprasad on 4/27/2017.
 *
 * All permission handling is now centralised in MadcapPermissionManager
 */

public class MadcapPermissionsManager implements PermissionsManager {
    private final String TAG = getClass().getSimpleName();

    private Context context;

    private static final int RUN_CODE = 998;
    private static final int NOTIFICATION_ID = 918273;

    private NotificationManager mNotificationManager;

    @Inject
    public MadcapPermissionsManager(Context context) {
        this.context = context;
    }

    public void requestPermissionFromNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.ic_stat_madcaplogo);

        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        mBuilder.setPriority(Notification.PRIORITY_HIGH);

        Intent intent = new Intent(context, PermissionsActivity.class);
        PendingIntent permissionsIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, intent, 0);

        mBuilder.setContentTitle("MADCAP requests permissions");
        mBuilder.addAction(R.drawable.ic_stat_madcaplogo, "Settings", permissionsIntent);
        mBuilder.setAutoCancel(true);
        mBuilder.setOnlyAlertOnce(true);
        Notification notification = mBuilder.build();

        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(RUN_CODE, notification);

    }

    @Override
    public boolean isContactPermitted(){
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED); //? true : false;
    }

    @Override
    public boolean isLocationPermitted(){
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);// ? true : false;
    }

    @Override
    public boolean isSmsPermitted(){
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED);// ? true : false;
    }

    @Override
    public boolean isStoragePermitted(){
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);// ? true : false;
    }

    @Override
    public boolean isTelephonePermitted(){
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED);// ? true : false;
    }

    @Override
    public boolean isBluetoothPermitted(){
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED);// ? true : false;
    }

    @Override
    public boolean isUsageStatsPermitted(){
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, appInfo.uid, appInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            //do not resolve this catch.
            return false;
        }
    }

    public boolean hasAllPermissions(){
        return ( isStoragePermitted()
                && isUsageStatsPermitted()
                && isLocationPermitted()
                && isContactPermitted()
                && isSmsPermitted()
                && isTelephonePermitted()
        );
    }

    public static class PermissionGrantedEvent {
        private final String message;

        public PermissionGrantedEvent(String message) {
            this.message = message;
        }

        public String onPermissionGrantedEvent() {
            return message;
        }
    }
}

