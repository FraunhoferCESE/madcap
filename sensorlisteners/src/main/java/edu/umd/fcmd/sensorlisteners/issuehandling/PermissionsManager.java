package edu.umd.fcmd.sensorlisteners.issuehandling;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * Created by MMueller on 11/15/2016.
 * Modified by PGuruprasad on 04/27/2017
 *
 * Interface to handle permissions
 */
public interface PermissionsManager {

    void requestPermissionFromNotification();

    boolean isContactPermitted();

    boolean isLocationPermitted();

    boolean isSmsPermitted();

    boolean isStoragePermitted();

    boolean isTelephonePermitted();

    boolean isUsageStatsPermitted();
}
