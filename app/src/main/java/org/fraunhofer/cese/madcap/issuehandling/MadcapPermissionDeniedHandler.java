package org.fraunhofer.cese.madcap.issuehandling;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

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
                        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        actionUsagePrompted = true;
                    }
                }
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
}
