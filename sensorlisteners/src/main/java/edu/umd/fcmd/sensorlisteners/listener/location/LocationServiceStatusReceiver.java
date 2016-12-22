package edu.umd.fcmd.sensorlisteners.listener.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

import java.util.Objects;
import java.util.regex.Pattern;

import edu.umd.fcmd.sensorlisteners.model.Location.LocationServiceStatusProbe;

/**
 * Created by MMueller on 11/14/2016.
 * <p>
 * A location Staus receiver listening to a special intent which is
 * indicating if the user turned the location services on or off.
 */
public class LocationServiceStatusReceiver extends BroadcastReceiver {
    private final String TAG = getClass().getSimpleName();
    private final LocationListener locationListener;
    private String oldStatus;
    private static final Pattern PROVIDER_CHANGED_PATTERN = Pattern.compile("android.location.PROVIDERS_CHANGED");

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
        if (PROVIDER_CHANGED_PATTERN.matcher(intent.getAction()).matches()) {
            Log.d(TAG, "GPS Status changed");
            long currentTime = System.currentTimeMillis();
            synchronized (new Object()) {
                oldStatus = updateStatus(context, oldStatus, currentTime);
            }
        }
    }

    void sendInitialProbe(Context context) {
        long currentTime = System.currentTimeMillis();
        synchronized (new Object()) {
            oldStatus = updateStatus(context, null, currentTime);
        }
    }

    private String updateStatus(Context context, String previousStatus, long currentTime) {
        Log.d(TAG, "Check for Location preference status");
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        String newStatus;
        try {
            newStatus = (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) ? LocationServiceStatusProbe.ON : LocationServiceStatusProbe.OFF;
        } catch (RuntimeException ignored) {
            newStatus = LocationServiceStatusProbe.NO_PROVIDER;
        }

        if (!Objects.equals(previousStatus, newStatus)) {
            Log.d(TAG, "Location status updated:" + newStatus);

            LocationServiceStatusProbe probe = new LocationServiceStatusProbe();
            probe.setDate(currentTime);
            probe.setLocationServiceStatus(newStatus);
            locationListener.onUpdate(probe);
        }

        return newStatus;
    }
}
