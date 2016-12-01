package org.fraunhofer.cese.madcap.issuehandling;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import org.fraunhofer.cese.madcap.MyApplication;

import edu.umd.fcmd.sensorlisteners.issuehandling.PermissionDeniedHandler;

/**
 * Created by MMueller on 11/15/2016.
 */

public class MadcapPermissionDeniedHandler implements PermissionDeniedHandler {
    private final String TAG = getClass().getSimpleName();

    private Context context;

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
                MyApplication.madcapLogger.e(TAG, "Action usage access denied");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //This only works on api level 21+
                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
                break;
            default:
                MyApplication.madcapLogger.e(TAG, "Unknown permission denied");
                break;
        }
    }
}
