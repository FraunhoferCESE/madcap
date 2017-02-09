package edu.umd.fcmd.sensorlisteners.listener.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import edu.umd.fcmd.sensorlisteners.issuehandling.PermissionDeniedHandler;

public class SingleShotLocationProvider {

    private PermissionDeniedHandler permissionDeniedHandler;

    public interface LocationCallback {
        public void onNewSingleGPSLocationAvailable(Location location);
    }

    public SingleShotLocationProvider(PermissionDeniedHandler permissionDeniedHandler) {
        this.permissionDeniedHandler = permissionDeniedHandler;
    }


    // calls back to calling thread, note this is for low grain: if you want higher precision, swap the
    // contents of the else and if. Also be sure to check gps permission/settings are allowed.
    // call usually takes <10ms
    public void requestSingleUpdate(final Context context, final LocationCallback callback) {
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.);
        if (isGPSEnabled) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionDeniedHandler.onPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION);
                return;
            }
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new android.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    callback.onNewSingleGPSLocationAvailable(location);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            }, null);
        }

    }
}
