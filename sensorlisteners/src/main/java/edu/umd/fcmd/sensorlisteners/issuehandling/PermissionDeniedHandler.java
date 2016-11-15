package edu.umd.fcmd.sensorlisteners.issuehandling;

/**
 * Created by MMueller on 11/15/2016.
 *
 * This interface will be called by some classes when
 * they suspect some denied permissions.
 * Need to be implemented to handle in a concrete instance.
 */
public interface PermissionDeniedHandler {

    /**
     * To be invoked when some permission is being denied.
     * @param permissionString the permission code according
     * to the manifest.
     */
    void onPermissionDenied(String permissionString);
}
