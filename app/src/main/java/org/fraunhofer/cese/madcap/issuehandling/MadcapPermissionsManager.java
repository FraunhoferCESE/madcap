package org.fraunhofer.cese.madcap.issuehandling;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

import org.fraunhofer.cese.madcap.PermissionsActivity;
import org.fraunhofer.cese.madcap.R;

import javax.inject.Inject;

import edu.umd.fcmd.sensorlisteners.issuehandling.PermissionsManager;

/**
 * Created by MMueller on 11/15/2016.
 * <p>
 * Edited by PGuruprasad on 4/27/2017.
 * <p>
 * All permission handling is now centralised in MadcapPermissionManager
 */

public class MadcapPermissionsManager implements PermissionsManager {
    private Context context;

    private static final int NOTIFICATION_ID = 918273;

    private NotificationManager mNotificationManager;

    @Inject
    public MadcapPermissionsManager(Context context, NotificationManager notificationManager) {
        this.context = context;
        mNotificationManager = notificationManager;
    }

    public void requestPermissionFromNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_stat_madcaplogo)
                .setOnlyAlertOnce(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentTitle("MADCAP requests permissions");


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(PermissionsActivity.class);
        stackBuilder.addNextIntent(new Intent(context, PermissionsActivity.class));
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    @Override
    public boolean isContactPermitted() {
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED); //? true : false;
    }

    @Override
    public boolean isLocationPermitted() {
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);// ? true : false;
    }

    @Override
    public boolean isSmsPermitted() {
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED);// ? true : false;
    }

    @Override
    public boolean isTelephonePermitted() {
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED);// ? true : false;
    }

    @Override
    public boolean isUsageStatsPermitted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
                AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, appInfo.uid, appInfo.packageName);
                return (mode == AppOpsManager.MODE_ALLOWED);

            } catch (PackageManager.NameNotFoundException e) {
                //do not resolve this catch.
                return false;
            }
        }
        else {
            return (ContextCompat.checkSelfPermission(context,Manifest.permission.GET_TASKS) == PackageManager.PERMISSION_GRANTED);
        }
    }

    public boolean hasAllPermissions() {
        return (isUsageStatsPermitted()
                && isLocationPermitted()
                && isContactPermitted()
                && isSmsPermitted()
                && isTelephonePermitted()
        );
    }

    public enum PermissionGrantedEvent {
        CONTACTS, TELEPHONE, SMS, LOCATION, USAGE
    }
}

