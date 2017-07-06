package edu.umd.fcmd.sensorlisteners.issuehandling;

/**
 * Created by MMueller on 11/15/2016.
 * Modified by PGuruprasad on 04/27/2017
 * <p>
 * Interface to handle permissions
 */
public interface PermissionsManager {

    void requestPermissionFromNotification();

    boolean isContactPermitted();

    boolean isLocationPermitted();

    boolean isSmsPermitted();

    boolean isTelephonePermitted();

    boolean isUsageStatsPermitted();

}
