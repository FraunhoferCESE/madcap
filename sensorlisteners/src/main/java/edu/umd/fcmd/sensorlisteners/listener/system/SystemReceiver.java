package edu.umd.fcmd.sensorlisteners.listener.system;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.TimeZone;

import edu.umd.fcmd.sensorlisteners.model.system.DockStateProbe;
import edu.umd.fcmd.sensorlisteners.model.system.InputMethodProbe;
import edu.umd.fcmd.sensorlisteners.model.system.SystemUptimeProbe;
import edu.umd.fcmd.sensorlisteners.model.system.TimeChangedProbe;
import edu.umd.fcmd.sensorlisteners.model.system.TimezoneProbe;
import edu.umd.fcmd.sensorlisteners.model.system.UserPresenceProbe;
import edu.umd.fcmd.sensorlisteners.model.system.AirplaneModeProbe;
import edu.umd.fcmd.sensorlisteners.model.system.DreamingModeProbe;
import edu.umd.fcmd.sensorlisteners.model.system.ScreenProbe;


/**
 * Created by MMueller on 12/30/2016.
 *
 * BroadcastReceiver listening to certain general system events like:
 * - dreaming of the device
 */
public class SystemReceiver extends BroadcastReceiver {
    private final String TAG = getClass().getSimpleName();

    private final SystemListener systemListener;
    private final Context context;
    private String oldTimezone;

    public SystemReceiver(SystemListener systemListener, Context context){
        this.systemListener = systemListener;
        this.context = context;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        oldTimezone = prefs.getString("PREF_TIMEZONE", TimeZone.getDefault().getID());
    }

    /**
     * This method is called when the BroadcastReceiver is receiving an Intent
     * broadcast.  During this time you can use the other methods on
     * BroadcastReceiver to view/modify the current result values.  This method
     * is always called within the main thread of its process, unless you
     * explicitly asked for it to be scheduled on a different thread using
     * {@link Context registerReceiver(BroadcastReceiver,
     * IntentFilter, String, Handler)}. When it runs on the main
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
     * {@link Context bindService(Intent, ServiceConnection, int)}.  If you wish
     * to interact with a service that is already running, you can use
     * {@link #peekService}.
     * <p>
     * <p>The Intent filters used in {@link Context#registerReceiver}
     * and in application manifests are <em>not</em> guaranteed to be exclusive. They
     * are hints to the operating system about how to find suitable recipients. It is
     * possible for senders to force delivery to specific recipients, bypassing filter
     * resolution.  For this reason, {@link #onReceive(Context, Intent) onReceive()}
     * implementations should respond only to known actions, ignoring any unexpected
     * Intents that they may receive.
     *
     * @param context The Context in which the receiver is running.
     * @param intent  The Intent being received.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_DREAMING_STARTED:
                DreamingModeProbe dreamingModeProbeOn = new DreamingModeProbe();
                dreamingModeProbeOn.setDate(System.currentTimeMillis());
                dreamingModeProbeOn.setState(DreamingModeProbe.ON);
                systemListener.onUpdate(dreamingModeProbeOn);
                break;
            case Intent.ACTION_DREAMING_STOPPED:
                DreamingModeProbe dreamingModeProbeOff = new DreamingModeProbe();
                dreamingModeProbeOff.setDate(System.currentTimeMillis());
                dreamingModeProbeOff.setState(DreamingModeProbe.OFF);
                systemListener.onUpdate(dreamingModeProbeOff);
                break;
            case Intent.ACTION_SCREEN_OFF:
                ScreenProbe screenProbeOff = new ScreenProbe();
                screenProbeOff.setDate(System.currentTimeMillis());
                screenProbeOff.setState(ScreenProbe.OFF);
                systemListener.onUpdate(screenProbeOff);
                break;
            case Intent.ACTION_SCREEN_ON:
                ScreenProbe screenProbeOn = new ScreenProbe();
                screenProbeOn.setDate(System.currentTimeMillis());
                screenProbeOn.setState(ScreenProbe.ON);
                systemListener.onUpdate(screenProbeOn);
                break;
            case Intent.ACTION_AIRPLANE_MODE_CHANGED:
                AirplaneModeProbe airplaneModeProbe = new AirplaneModeProbe();
                airplaneModeProbe.setDate(System.currentTimeMillis());
                airplaneModeProbe.setState(systemListener.getCurrentAirplaneModeState());
                systemListener.onUpdate(airplaneModeProbe);
                break;
            case Intent.ACTION_BOOT_COMPLETED:
                SystemUptimeProbe systemUptimeProbeBoot = new SystemUptimeProbe();
                systemUptimeProbeBoot.setDate(System.currentTimeMillis());
                systemUptimeProbeBoot.setState(SystemUptimeProbe.BOOT);
                systemListener.onUpdate(systemUptimeProbeBoot);
                break;
            case Intent.ACTION_SHUTDOWN:
                SystemUptimeProbe systemUptimeProbeShutdown = new SystemUptimeProbe();
                systemUptimeProbeShutdown.setDate(System.currentTimeMillis());
                systemUptimeProbeShutdown.setState(SystemUptimeProbe.SHUTDOWN);
                systemListener.onUpdate(systemUptimeProbeShutdown);
                break;
            case Intent.ACTION_USER_PRESENT:
                UserPresenceProbe userPresenceProbe = new UserPresenceProbe();
                userPresenceProbe.setDate(System.currentTimeMillis());
                userPresenceProbe.setPresence("PRESENT");
                systemListener.onUpdate(userPresenceProbe);
                break;
            case Intent.ACTION_INPUT_METHOD_CHANGED:
                InputMethodProbe inputMethodProbe = new InputMethodProbe();
                inputMethodProbe.setDate(System.currentTimeMillis());
                inputMethodProbe.setMethod(systemListener.getCurrentInputMethod());
                systemListener.onUpdate(inputMethodProbe);
                break;
            case Intent.ACTION_TIMEZONE_CHANGED:
                TimezoneProbe timezoneProbe = new TimezoneProbe();
                timezoneProbe.setDate(System.currentTimeMillis());

                long now = System.currentTimeMillis();
                String newTimezone = TimeZone.getDefault().getID();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                if (TimeZone.getTimeZone(oldTimezone).getOffset(now) != TimeZone.getTimeZone(newTimezone).getOffset(now)) {
                    prefs.edit().putString("PREF_TIMEZONE", newTimezone).commit();
                    timezoneProbe.setTimeZone(newTimezone);
                    systemListener.onUpdate(timezoneProbe);
                    oldTimezone = newTimezone;
                }
                break;
            case Intent.ACTION_DOCK_EVENT:
                DockStateProbe dockStateProbe = new DockStateProbe();
                dockStateProbe.setDate(System.currentTimeMillis());
                dockStateProbe.setState(systemListener.getCurrentDockState());
                dockStateProbe.setKind(systemListener.getCurrentDockDevice());
                systemListener.onUpdate(dockStateProbe);
                break;
            case Intent.ACTION_TIME_CHANGED:
                TimeChangedProbe timeChangedProbe = new TimeChangedProbe();
                timeChangedProbe.setDate(System.currentTimeMillis());
                timeChangedProbe.setChange(TimeChangedProbe.TIMEADJUST);
                systemListener.onUpdate(timeChangedProbe);
                break;
            default:
                Log.d(TAG, "Unkown system event received");
                break;
        }
    }
}
