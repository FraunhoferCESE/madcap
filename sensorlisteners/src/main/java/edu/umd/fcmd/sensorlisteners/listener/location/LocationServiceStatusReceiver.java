package edu.umd.fcmd.sensorlisteners.listener.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

import edu.umd.fcmd.sensorlisteners.model.LocationServiceStatusProbe;

/**
 * Created by MMueller on 11/14/2016.
 * <p>
 * A location Staus receiver listening to a special intent which is
 * indicating if the user turned the location services on or off.
 */
@SuppressWarnings({"ALL", "NonBooleanMethodNameMayNotStartWithQuestion"})
public class LocationServiceStatusReceiver extends BroadcastReceiver {
    private final String TAG = getClass().getSimpleName();
    private final LocationListener locationListener;
    private String oldStatus = null;

    LocationServiceStatusReceiver(LocationListener locationListener) {
        this.locationListener = locationListener;
    }

    /**
     * This method is called when the BroadcastReceiver is receiving an Intent
     * broadcast.  During this time you can use the other methods on
     * BroadcastReceiver to view/modify the current result values.  This method
     * is always called within the main thread of its process, unless you
     * explicitly asked for it to be scheduled on a different thread using
     * . When it runs on the main
     * thread you should
     * never perform long-running operations in it (there is a timeout of
     * 10 seconds that the system allows before considering the receiver to
     * be blocked and a candidate to be killed). You cannot launch a popup dialog
     * in your implementation of onReceive().
     * <p>
     * <p><b>If this BroadcastReceiver was launched through a &lt;receiver&gt; tag,
     * then the object is no longer alive after returning from this
     * function.</b>  This means you should not perform any operations that
     * return a result to you asynchronously -- in particular, for interacting
     * with services, you should use
     * {@link Context#startService(Intent)} instead of
     * .  If you wish
     * to interact with a service that is already running, you can use
     * {@link #peekService}.
     * <p>
     * <p>The Intent filters used in {@link Context#registerReceiver}
     * and in application manifests are <em>not</em> guaranteed to be exclusive. They
     * are hints to the operating system about how to find suitable recipients. It is
     * possible for senders to force delivery to specific recipients, bypassing filter
     * resolution.  For this reason,
     * implementations should respond only to known actions, ignoring any unexpected
     * Intents that they may receive.
     *
     * @param context The Context in which the receiver is running.
     * @param intent  The Intent being received.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            Log.d(TAG, "GPS Status changed");
            updateStatus(context);
        }
    }

    void sendInitialProbe(Context context) {
        updateStatus(context);
    }

    private void updateStatus(Context context) {
        Log.d(TAG, "Check for Location preference status");
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        try {
            String status = lm.isProviderEnabled(LocationManager.GPS_PROVIDER) == true ? LocationServiceStatusProbe.ON : LocationServiceStatusProbe.OFF;

            if (oldStatus == null || !oldStatus.equals(status)) {
                Log.d(TAG, "Location status updated:" + status);

                LocationServiceStatusProbe probe = new LocationServiceStatusProbe();
                probe.setDate(System.currentTimeMillis());
                probe.setLocationServiceStatus(status);
                locationListener.onUpdate(probe);
                oldStatus = status;
            }
        } catch (RuntimeException ignored) {
            Log.e(TAG, "Could not access provider");
        }
    }
}
