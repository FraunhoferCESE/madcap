package org.fraunhofer.cese.madcap.issuehandling;

import android.Manifest;

import org.fraunhofer.cese.madcap.MyApplication;

import edu.umd.fcmd.sensorlisteners.issuehandling.PermissionDeniedHandler;

/**
 * Created by MMueller on 11/15/2016.
 */

public class MadcapPermissionDeniedHandler implements PermissionDeniedHandler {
    private final String TAG = getClass().getSimpleName();

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
            default:
                MyApplication.madcapLogger.e(TAG, "Unknown permission denied");
                break;
        }
    }
}
