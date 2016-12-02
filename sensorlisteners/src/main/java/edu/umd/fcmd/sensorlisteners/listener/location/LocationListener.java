package edu.umd.fcmd.sensorlisteners.listener.location;

import android.content.Context;
import android.content.IntentFilter;
import android.os.AsyncTask;
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
public class LocationListener implements Listener<LocationProbe> {
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
    private boolean runningStatus;

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
        locationServiceStatusReceiver =  locationServiceStatusReceiverFactory.create(this);
        locationServiceStatusReceiver.sendInitialProbe(context);
        timedLocationTaskFactory = timedTaskFactory;
        mGoogleApiClient.registerConnectionCallbacks(connectionCallbackClass);
        mGoogleApiClient.registerConnectionFailedListener(connectionFailedCallbackClass);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.location.PROVIDERS_CHANGED");
        context.registerReceiver(locationServiceStatusReceiver, intentFilter);
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
        if(!runningStatus){
            mGoogleApiClient.connect();
            timedLocationTask = timedLocationTaskFactory.create(this, snapshotApi, permissionDeniedHandler, sensorNoAnswerReceivedHandler);
            timedLocationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        runningStatus = true;
    }

    @Override
    public void stopListening() {
        if (timedLocationTask != null && runningStatus) {
            Log.d(TAG, "Timed location task is not null");
            timedLocationTask.cancel(true);
            mGoogleApiClient.disconnect();
            context.unregisterReceiver(locationServiceStatusReceiver);
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
}
