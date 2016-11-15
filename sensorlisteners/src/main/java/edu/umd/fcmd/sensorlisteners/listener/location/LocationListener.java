package edu.umd.fcmd.sensorlisteners.listener.location;

import android.content.Context;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.awareness.SnapshotApi;
import com.google.android.gms.common.api.GoogleApiClient;

import edu.umd.fcmd.sensorlisteners.NoSensorFoundException;
import edu.umd.fcmd.sensorlisteners.issuehandling.PermissionDeniedHandler;
import edu.umd.fcmd.sensorlisteners.listener.Listener;
import edu.umd.fcmd.sensorlisteners.model.LocationServiceStatusProbe;
import edu.umd.fcmd.sensorlisteners.model.LocationProbe;
import edu.umd.fcmd.sensorlisteners.service.StateManager;

/**
 * Created by MMueller on 11/4/2016.
 *
 * A listener for Locations. Retrieving updates in a certain defined period.
 */

@SuppressWarnings("ClassNamePrefixedWithPackageName")
public class LocationListener implements Listener<LocationProbe> {
    private static final String TAG = LocationListener.class.getSimpleName();

    private final Context context;
    private final StateManager mStateManager;
    private final SnapshotApi snapshotApi;

    private final TimedLocationTaskFactory timedLocationTaskFactory;
    private TimedLocationTask timedLocationTask;
    private final GoogleApiClient mGoogleApiClient;
    private LocationServiceStatusReceiver locationServiceStatusReceiver;
    private IntentFilter intentFilter;
    private PermissionDeniedHandler permissionDeniedHandler;

    /**
     * Default constructor which should be used.
     *
     * @param context          the app context.
     * @param mStateManager    the StateManager to connect to.
     * @param mGoogleApiClient  a GoogleApi client.
     * @param snapshotApi      usally the Awarness.SnapshotsApi
     */
    public LocationListener(Context context,
                            StateManager mStateManager,
                            GoogleApiClient mGoogleApiClient,
                            SnapshotApi snapshotApi,
                            TimedLocationTaskFactory timedTaskFactory,
                            LocationServiceStatusReceiverFactory locationServiceStatusReceiverFactory,
                            GoogleApiClient.ConnectionCallbacks connectionCallbackClass,
                            GoogleApiClient.OnConnectionFailedListener connectionFailedCallbackClass,
                            PermissionDeniedHandler permissionDeniedHandler) {
        this.context = context;
        this.mStateManager = mStateManager;
        this.mGoogleApiClient = mGoogleApiClient;
        this.snapshotApi = snapshotApi;
        this.permissionDeniedHandler = permissionDeniedHandler;
        locationServiceStatusReceiver =  locationServiceStatusReceiverFactory.create(this);
        locationServiceStatusReceiver.sendInitialProbe(context);
        timedLocationTaskFactory = timedTaskFactory;
        //noinspection ThisEscapedInObjectConstruction
        mGoogleApiClient.registerConnectionCallbacks(connectionCallbackClass);
        //noinspection ThisEscapedInObjectConstruction
        mGoogleApiClient.registerConnectionFailedListener(connectionFailedCallbackClass);
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.location.PROVIDERS_CHANGED");
        context.registerReceiver(locationServiceStatusReceiver, intentFilter);
    }

    /**
     * Being called when a new location update is available.
     * @param state location update state.
     */
    @Override
    public void onUpdate(LocationProbe state) {
        mStateManager.save(state);
    }

    /**
     * Being called when a new location service status is available.
     * @param state location service update state.
     */
    public void onUpdate(LocationServiceStatusProbe state) {
        mStateManager.save(state);
    }

    /**
     * Starts listening to frequent location updates.
     * @throws NoSensorFoundException when the connection to the GoogleApi client fails.
     */
    @Override
    public void startListening() throws NoSensorFoundException {
        mGoogleApiClient.connect();
        timedLocationTask = timedLocationTaskFactory.create(this, snapshotApi, permissionDeniedHandler);
        timedLocationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void stopListening() {
        if (timedLocationTask != null) {
            Log.d(TAG, "Timed location task is not null");
            timedLocationTask.cancel(true);
            mGoogleApiClient.disconnect();
            context.unregisterReceiver(locationServiceStatusReceiver);
        }
    }

    protected Context getContext() {
        return context;
    }

    protected GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }
}
