package org.fraunhofer.cese.madcap.issuehandling;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import org.fraunhofer.cese.madcap.MyApplication;

import java.util.List;

import edu.umd.fcmd.sensorlisteners.issuehandling.PermissionDeniedHandler;

/**
 * Created by MMueller on 11/15/2016.
 */

public class MadcapPermissionDeniedHandler implements PermissionDeniedHandler {
    private final String TAG = getClass().getSimpleName();

    private Context context;

    private boolean actionUsagePrompted;
    private boolean bluetoothPermissionPrompted;

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
                MyApplication.madcapLogger.e(TAG, "Location permission denied");
                //Do some more things like kicking off a timer.
                break;
            case Settings.ACTION_USAGE_ACCESS_SETTINGS:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //This only works on api level 21+
                    if(!actionUsagePrompted){
                        MyApplication.madcapLogger.e(TAG, "Action usage access denied");
                        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        actionUsagePrompted = true;
                    }
                }
                break;
            case Manifest.permission.BLUETOOTH:
                if(!bluetoothPermissionPrompted){
                    MyApplication.madcapLogger.e(TAG, "Bluetooth permission deniedd");
                    bluetoothPermissionPrompted = true;
                }
            default:
                MyApplication.madcapLogger.e(TAG, "Unknown permission denied");
                break;
        }
    }
}
