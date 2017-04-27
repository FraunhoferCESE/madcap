package edu.umd.fcmd.sensorlisteners.listener.location;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;

import javax.inject.Inject;
import javax.inject.Named;

import edu.umd.fcmd.sensorlisteners.NoSensorFoundException;
import edu.umd.fcmd.sensorlisteners.issuehandling.PermissionsManager;
import edu.umd.fcmd.sensorlisteners.listener.Listener;
import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.model.location.LocationProbe;
import edu.umd.fcmd.sensorlisteners.model.location.LocationServiceStatusProbe;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;

/**
 * A listener for Locations. Retrieving updates in a certain defined period.
 */
public class LocationListener implements Listener<LocationProbe>, android.location.LocationListener {
    private static final String TAG = LocationListener.class.getSimpleName();

    private static final double NETWORK_LOCATION_ACCURACY_THRESHOLD = 30.0;
    private static final long MIN_TIME = 30000;
    private static final float MIN_DISTANCE = 20.0f;
    private static final int GPS_PERMIT = 1;

    private final Context context;
    private final ProbeManager<Probe> mProbeManager;

    private final GoogleApiClient mGoogleApiClient;
    private final LocationServiceStatusReceiver locationServiceStatusReceiver;
    private final PermissionsManager permissionsManager;

    private final LocationManager locationManager;
    private volatile boolean runningStatus;

    /**
     * Default constructor which should be used.
     *
     * @param context          the app context.
     * @param mProbeManager    the ProbeManager to connect to.
     * @param mGoogleApiClient a GoogleApi client.
     */
    @Inject
    public LocationListener(Context context,
                            ProbeManager<Probe> mProbeManager,
                            @Named("AwarenessApi") GoogleApiClient mGoogleApiClient,
                            LocationServiceStatusReceiverFactory locationServiceStatusReceiverFactory,
                            GoogleApiClient.ConnectionCallbacks connectionCallbackClass,
                            GoogleApiClient.OnConnectionFailedListener connectionFailedCallbackClass,
                            PermissionsManager permissionsManager) {
        this.context = context;
        this.mProbeManager = mProbeManager;
        this.mGoogleApiClient = mGoogleApiClient;
        this.permissionsManager = permissionsManager;
        locationServiceStatusReceiver = locationServiceStatusReceiverFactory.create(this);
        locationServiceStatusReceiver.sendInitialProbe(context);
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mGoogleApiClient.registerConnectionCallbacks(connectionCallbackClass);
        mGoogleApiClient.registerConnectionFailedListener(connectionFailedCallbackClass);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.location.PROVIDERS_CHANGED");
        this.context.registerReceiver(locationServiceStatusReceiver, intentFilter);
    }

    /**
     * Being called when a new location update is available.
     *
     * @param state location update state.
     */
    @Override
    public void onUpdate(LocationProbe state) {
        mProbeManager.save(state);
    }

    /**
     * Being called when a new location service status is available.
     *
     * @param state location service update state.
     */
    void onUpdate(LocationServiceStatusProbe state) {
        mProbeManager.save(state);
    }

    /**
     * Starts listening to frequent location updates.
     *
     * @throws NoSensorFoundException when the connection to the GoogleApi client fails.
     */
    @Override
    public synchronized void startListening() throws NoSensorFoundException {
        if (!runningStatus) {
            if (isPermittedByUser()) {
                Log.d(TAG, "Sending initial location probes");
                onLocationChanged(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
            } else {
//                permissionsManager.requestPermissionFromNotification("MADCAP requires permission to access your location information.", "location");
                permissionsManager.requestPermissionFromNotification();
            }
        }

        runningStatus = true;
    }

    @Override
    public synchronized void stopListening() {
        if (runningStatus) {
            if (locationServiceStatusReceiver != null) {
                context.unregisterReceiver(locationServiceStatusReceiver);
            }

            if (isPermittedByUser()) {
                locationManager.removeUpdates(this);
            } else {
//                permissionsManager.onPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION);
                permissionsManager.requestPermissionFromNotification();
            }
        }
        runningStatus = false;
    }

//    private static boolean hasPermission(Context context, String permission) {
//        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
//    }

    @Override
    public boolean isRunning() {
        return runningStatus;
    }

    @Override
    public boolean isPermittedByUser() {
        if (permissionsManager.isLocationPermitted()) {
            Log.e(TAG,"Location access permitted by user");
            return true;
        }else {
            Log.v(TAG,"Location access NOT permitted by user");
            return false;
        }
    }

    // TODO: Need to refactor this
    protected Context getContext() {
        return context;
    }

    // TODO: Need to refactor this
    GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    /**
     * Called when the location has changed.
     * <p>
     * <p> There are no restrictions on the use of the supplied Location object.
     *
     * @param location The new location, as a Location object.
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "location changed");
        if (location != null) {
            if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER) && (location.getAccuracy() > NETWORK_LOCATION_ACCURACY_THRESHOLD)) {
                Log.d(TAG, "Network accuracy (" + location.getAccuracy() + ") is more than threshold (" + NETWORK_LOCATION_ACCURACY_THRESHOLD + "). Requesting location from GPS.");
                if (isPermittedByUser()) {
                    locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
//                } else permissionsManager.onPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION);
                }
            }
            onUpdate(createLocationProbe(location));
        } else {
            Log.d(TAG, "location is null");
        }
    }


    /**
     * Creates a Location Probe object from a
     *
     * @param location
     * @return
     */
    private static LocationProbe createLocationProbe(Location location) {
        LocationProbe probe = new LocationProbe();
        probe.setDate(System.currentTimeMillis());
        probe.setAccuracy((double) location.getAccuracy());
        probe.setAltitude(location.getAltitude());
        probe.setBearing((double) location.getBearing());
        probe.setLatitude(location.getLatitude());
        probe.setLongitude(location.getLongitude());
        probe.setOrigin(location.getProvider());
        probe.setSpeed((double) location.getSpeed());
        probe.setExtras(location.getExtras());

        return probe;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    /**
     * Called when the provider is enabled by the user.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     */
    @Override
    public void onProviderEnabled(String provider) {

    }

    /**
     * Called when the provider is disabled by the user. If requestLocationUpdates
     * is called on an already disabled provider, this method is called
     * immediately.
     *
     * @param provider the name of the location provider associated with this
     *                 update.
     */
    @Override
    public void onProviderDisabled(String provider) {

    }
}
