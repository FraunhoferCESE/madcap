package edu.umd.fcmd.sensorlisteners.listener.location;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationRequest;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;
import javax.inject.Named;

import edu.umd.fcmd.sensorlisteners.NoSensorFoundException;
import edu.umd.fcmd.sensorlisteners.issuehandling.PermissionDeniedHandler;
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

    private static final long MAX_TIME = 600000;

    private static final int STRATEGY_FUSED = 1;
    private static final int STRAGEY_LEGACY = 2;

    private final int locationStrategy;
    private static final int GPS_PERMIT = 1;

    private final Context context;
    private final ProbeManager<Probe> mProbeManager;

    private final FusedLocationProviderApi fusedLocationProviderApi;
    private final GoogleApiClient apiClient;
    private final PendingIntent mPendingIntent;

    private final LocationServiceStatusReceiver locationServiceStatusReceiver;
    private final PermissionDeniedHandler permissionDeniedHandler;

    private final LocationManager locationManager;
    private volatile boolean runningStatus;

    /**
     * Default constructor which should be used.
     *
     * @param context       the app context.
     * @param mProbeManager the ProbeManager to connect to.
     * @param apiClient     a GoogleApi client.
     */
    @Inject
    public LocationListener(Context context,
                            ProbeManager<Probe> mProbeManager,
                            FusedLocationProviderApi fusedLocationProviderApi,
                            @Named("FusedLocationProviderApi") GoogleApiClient apiClient,
                            GoogleApiClient.ConnectionCallbacks connectionCallbackClass,
                            GoogleApiClient.OnConnectionFailedListener connectionFailedCallbackClass,
                            PermissionDeniedHandler permissionDeniedHandler,
                            LocationServiceStatusReceiverFactory locationServiceStatusReceiverFactory) {

        // TODO: This should be configurable somehow.
        locationStrategy = STRATEGY_FUSED;

        this.context = context;
        this.mProbeManager = mProbeManager;

        this.fusedLocationProviderApi = fusedLocationProviderApi;
        this.apiClient = apiClient;
        apiClient.registerConnectionCallbacks(connectionCallbackClass);
        apiClient.registerConnectionFailedListener(connectionFailedCallbackClass);

        this.permissionDeniedHandler = permissionDeniedHandler;

        locationServiceStatusReceiver = locationServiceStatusReceiverFactory.create(this);
        locationServiceStatusReceiver.sendInitialProbe(context);
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.location.PROVIDERS_CHANGED");
        this.context.registerReceiver(locationServiceStatusReceiver, intentFilter);

        mPendingIntent = PendingIntent.getService(context, 1, new Intent(context, FusedLocationService.class), 0);
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
        Log.d(TAG, "startListening. Strategy: " + locationStrategy);
        if (!runningStatus) {
            if (locationStrategy == STRATEGY_FUSED) {
                EventBus.getDefault().register(this);
            }

            if (isPermittedByUser()) {
                if (locationStrategy == STRATEGY_FUSED) {
                    apiClient.connect();
                } else {
                    onLocationChanged(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
                }

            } else {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, GPS_PERMIT);
                permissionDeniedHandler.onPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }

        runningStatus = true;
    }

    @Subscribe
    public void handleFusedLocationConnectedEvent(FusedLocationConnectedEvent event) {
        onLocationChanged(fusedLocationProviderApi.getLastLocation(apiClient));

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(MAX_TIME);
        locationRequest.setFastestInterval(MIN_TIME);
        locationRequest.setSmallestDisplacement(MIN_DISTANCE);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        fusedLocationProviderApi.requestLocationUpdates(apiClient, locationRequest, mPendingIntent);
    }

    @Override
    public synchronized void stopListening() {
        if (runningStatus) {
            if (locationServiceStatusReceiver != null) {
                context.unregisterReceiver(locationServiceStatusReceiver);
            }


            if (locationStrategy == STRATEGY_FUSED) {
                EventBus.getDefault().unregister(this);
            }

            if (isPermittedByUser()) {
                if (locationStrategy == STRATEGY_FUSED) {
                    if (apiClient.isConnected()) {
                        onLocationChanged(fusedLocationProviderApi.getLastLocation(apiClient));
                    }
                    fusedLocationProviderApi.removeLocationUpdates(apiClient, mPendingIntent);
                    apiClient.disconnect();
                } else {
                    locationManager.removeUpdates(this);
                }
            } else {
                permissionDeniedHandler.onPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION);
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

    @Subscribe
    public void handleFusedLocation(Location location) {
        Log.i(TAG, "Fused location received: " + location);
        onUpdate(createLocationProbe(location));
    }

    @Override
    public boolean isPermittedByUser() {
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_DENIED) {
            Log.e(TAG, "Location access NOT permitted");
            return false;
        } else {
            Log.v(TAG, "Location access permitted");
            return true;
        }
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
                } else {
                    permissionDeniedHandler.onPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION);
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
