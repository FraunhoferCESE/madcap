package edu.umd.fcmd.sensorlisteners.listener.location;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.awareness.SnapshotApi;
import com.google.android.gms.common.api.GoogleApiClient;

import edu.umd.fcmd.sensorlisteners.NoSensorFoundException;
import edu.umd.fcmd.sensorlisteners.issuehandling.PermissionDeniedHandler;
import edu.umd.fcmd.sensorlisteners.issuehandling.SensorNoAnswerReceivedHandler;
import edu.umd.fcmd.sensorlisteners.listener.Listener;
import edu.umd.fcmd.sensorlisteners.model.LocationServiceStatusProbe;
import edu.umd.fcmd.sensorlisteners.model.LocationProbe;
import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;

/**
 * Created by MMueller on 11/4/2016.
 *
 * A listener for Locations. Retrieving updates in a certain defined period.
 */
@SuppressWarnings({"ClassNamePrefixedWithPackageName", "rawtypes", "ThisEscapedInObjectConstruction"})
public class LocationListener implements Listener<LocationProbe>, android.location.LocationListener {
    private static final String TAG = LocationListener.class.getSimpleName();

    private final Context context;
    private final ProbeManager<Probe> mProbeManager;
    private final SnapshotApi snapshotApi;

    private final TimedLocationTaskFactory timedLocationTaskFactory;
    private TimedLocationTask timedLocationTask;
    private final GoogleApiClient mGoogleApiClient;
    private final LocationServiceStatusReceiver locationServiceStatusReceiver;
    private final PermissionDeniedHandler permissionDeniedHandler;
    private final SensorNoAnswerReceivedHandler sensorNoAnswerReceivedHandler;

    private LocationManager locationManager;
    private boolean runningStatus;

    private boolean useGps = true;
    private boolean useNetwork = false;
    private boolean useCache = true;

    /**
     * Default constructor which should be used.
     *
     * @param context          the app context.
     * @param mProbeManager    the ProbeManager to connect to.
     * @param mGoogleApiClient  a GoogleApi client.
     * @param snapshotApi      usally the Awarness.SnapshotsApi
     */
    public LocationListener(Context context,
                            ProbeManager<Probe> mProbeManager,
                            GoogleApiClient mGoogleApiClient,
                            SnapshotApi snapshotApi,
                            TimedLocationTaskFactory timedTaskFactory,
                            LocationServiceStatusReceiverFactory locationServiceStatusReceiverFactory,
                            GoogleApiClient.ConnectionCallbacks connectionCallbackClass,
                            GoogleApiClient.OnConnectionFailedListener connectionFailedCallbackClass,
                            PermissionDeniedHandler permissionDeniedHandler,
                            SensorNoAnswerReceivedHandler sensorNoAnswerReceivedHandler) {
        this.context = context;
        this.mProbeManager = mProbeManager;
        this.mGoogleApiClient = mGoogleApiClient;
        this.snapshotApi = snapshotApi;
        this.permissionDeniedHandler = permissionDeniedHandler;
        this.sensorNoAnswerReceivedHandler = sensorNoAnswerReceivedHandler;
        locationServiceStatusReceiver = locationServiceStatusReceiverFactory.create(this);
        locationServiceStatusReceiver.sendInitialProbe(context);
        timedLocationTaskFactory = timedTaskFactory;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mGoogleApiClient.registerConnectionCallbacks(connectionCallbackClass);
        mGoogleApiClient.registerConnectionFailedListener(connectionFailedCallbackClass);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.location.PROVIDERS_CHANGED");
        this.context.registerReceiver(locationServiceStatusReceiver, intentFilter);
    }

    /**
     * Being called when a new location update is available.
     * @param state location update state.
     */
    @Override
    public void onUpdate(LocationProbe state) {
        mProbeManager.save(state);
    }

    /**
     * Being called when a new location service status is available.
     * @param state location service update state.
     */
    void onUpdate(LocationServiceStatusProbe state) {
        mProbeManager.save(state);
    }

    /**
     * Starts listening to frequent location updates.
     * @throws NoSensorFoundException when the connection to the GoogleApi client fails.
     */
    @Override
    public void startListening() throws NoSensorFoundException {
        if (!runningStatus) {
            // Method Google Awareness API
//            mGoogleApiClient.connect();
//            timedLocationTask = timedLocationTaskFactory.create(this, snapshotApi, permissionDeniedHandler, sensorNoAnswerReceivedHandler);
//            timedLocationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            //Method for LocationManager
            if (useGps) {
                while (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    permissionDeniedHandler.onPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION);
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 5, this);

            }

//            if (useNetwork) {
//                while (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    permissionDeniedHandler.onPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION);
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//
//                }
//                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 15000, 5, this);
//            }

            sendInitialProbes();
        }

        runningStatus = true;
    }

    private void sendInitialProbes() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionDeniedHandler.onPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Log.d(TAG, "Sending initial Location Probes");
        onLocationChanged(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
        onLocationChanged(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
    }

    @Override
    public void stopListening() {
        if (runningStatus) {
            if(timedLocationTask != null){
                // Method Google Awareness API
            Log.d(TAG, "Timed location task is not null");
//            timedLocationTask.cancel(true);
//            mGoogleApiClient.disconnect();
            }
            context.unregisterReceiver(locationServiceStatusReceiver);

            //Method Location Manager
            while (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionDeniedHandler.onPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION);
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
            }locationManager.removeUpdates(this);


        }
        runningStatus = false;
    }

    @Override
    public boolean isRunning() {
        return runningStatus;
    }

    protected Context getContext() {
        return context;
    }

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
        if(location != null){
            String provider = location.getProvider();
            if (provider == null
                    || (useGps && LocationManager.GPS_PROVIDER.equals(provider))
                    || (useNetwork && LocationManager.NETWORK_PROVIDER.equals(provider))) {
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

                onUpdate(probe);
            }
        }else{
            Log.d(TAG, "location is null");
        }
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
