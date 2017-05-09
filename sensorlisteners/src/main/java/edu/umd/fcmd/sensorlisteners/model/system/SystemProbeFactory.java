package edu.umd.fcmd.sensorlisteners.model.system;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.Display;

import com.jaredrummler.android.device.DeviceName;

import java.util.TimeZone;

import javax.inject.Inject;

import timber.log.Timber;

import static android.content.Context.POWER_SERVICE;
import static android.content.Intent.EXTRA_DOCK_STATE;

/**
 * Factory class for creating a variety of probes related to system level events, such as turning on Airplane mode, turning off the screen, etc.
 */
@SuppressWarnings("MethodMayBeStatic")
public class SystemProbeFactory {


    /**
     * Default constructor needed for dependency injection with Dagger2
     */
    @Inject
    SystemProbeFactory() {
    }

    /**
     * Creates a probe from an Intent indicating whether the screen is now on or off
     *
     * @param intent see {@link Intent#ACTION_SCREEN_OFF} and {@link Intent#ACTION_SCREEN_ON}
     * @return a new probe
     */
    public ScreenProbe createScreenProbe(Intent intent) {
        ScreenProbe probe = new ScreenProbe();
        probe.setDate(System.currentTimeMillis());
        probe.setState(intent.getAction().equals(Intent.ACTION_SCREEN_OFF) ? ScreenProbe.OFF : ScreenProbe.ON);
        return probe;
    }

    /**
     * Creates a Screen on/off probe based on the system state
     *
     * @param context the calling context
     * @return a probe containing the state of the primary display
     */
    public ScreenProbe createScreenProbe(Context context) {
        ScreenProbe screenProbe = new ScreenProbe();
        screenProbe.setDate(System.currentTimeMillis());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            //API 20+
            DisplayManager displayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
            Display display = displayManager.getDisplay(Display.DEFAULT_DISPLAY);
            screenProbe.setState((display.getState() == Display.STATE_OFF) ? ScreenProbe.OFF : ScreenProbe.ON);

        } else {
            // API 11-19
            PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
            screenProbe.setState(powerManager.isScreenOn() ? ScreenProbe.ON : ScreenProbe.OFF);
        }

        return screenProbe;
    }

    /**
     * Creates a probe capturing whether or not the device is in Airplane mode
     *
     * @param context the calling context
     * @return a new airplane mode probe
     */
    public AirplaneModeProbe createAirplaneModeProbe(Context context) {
        AirplaneModeProbe airplaneModeProbe = new AirplaneModeProbe();
        airplaneModeProbe.setDate(System.currentTimeMillis());
        airplaneModeProbe.setState(((Settings.Global.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0)) == 0) ?
                AirplaneModeProbe.OFF :
                AirplaneModeProbe.ON);

        return airplaneModeProbe;
    }

    /**
     * Creates a probe containing the current TimeZone set for the device
     *
     * @return a new timezone probe
     */
    public TimezoneProbe createTimezoneProbe() {
        TimezoneProbe timezoneProbe = new TimezoneProbe();
        timezoneProbe.setDate(System.currentTimeMillis());
        timezoneProbe.setTimeZone(TimeZone.getDefault().getID());
        return timezoneProbe;
    }

    /**
     * Creates a probe containing a variety of system information.
     *
     * @param context    the calling context
     * @param appVersion the calling app's version to include in the probe
     * @return a new system info probe
     */
    public SystemInfoProbe createSystemInfoProbe(Context context, String appVersion) {
        SystemInfoProbe systemInfoProbe = new SystemInfoProbe();
        systemInfoProbe.setDate(System.currentTimeMillis());
        DeviceName.DeviceInfo info = DeviceName.getDeviceInfo(context);
        systemInfoProbe.setManufacturer(info.manufacturer);  // "Samsung"
        systemInfoProbe.setModel(info.marketName);            // "Galaxy S7 Edge"
        systemInfoProbe.setMadcapVersion(appVersion);
        systemInfoProbe.setApiLevel((double) Build.VERSION.SDK_INT);

        return systemInfoProbe;
    }

    /**
     * Creates a probe that returns the current default input method
     *
     * @param context the calling context
     * @return the new input method probe
     */
    public InputMethodProbe createInputMethodProbe(Context context) {
        InputMethodProbe inputMethodProbe = new InputMethodProbe();
        inputMethodProbe.setDate(System.currentTimeMillis());
        inputMethodProbe.setMethod(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD));
        return inputMethodProbe;
    }

    /**
     * Creates a probe that gets the current docking state of the device
     *
     * @param context the calling context
     * @return the new dock state probe
     */
    public DockStateProbe createDockStateProbe(Context context) {
        DockStateProbe dockStateProbe = new DockStateProbe();
        dockStateProbe.setDate(System.currentTimeMillis());

        Intent dockStatus = context.registerReceiver(null, new IntentFilter(Intent.ACTION_DOCK_EVENT));

        int dockState = (dockStatus == null) ? -1 : dockStatus.getIntExtra(EXTRA_DOCK_STATE, -1);

        switch(dockState) {
            case -1:
                dockStateProbe.setState(DockStateProbe.UNKNOWN);
                break;
            case Intent.EXTRA_DOCK_STATE_UNDOCKED:
                dockStateProbe.setState(DockStateProbe.UNDOCKED);
                break;
            default:
                dockStateProbe.setState(DockStateProbe.DOCKED);
        }

        switch (dockState) {
            case Intent.EXTRA_DOCK_STATE_CAR:
                dockStateProbe.setKind(DockStateProbe.CAR);
                break;
            case Intent.EXTRA_DOCK_STATE_DESK:
            case Intent.EXTRA_DOCK_STATE_HE_DESK:
            case Intent.EXTRA_DOCK_STATE_LE_DESK:
                dockStateProbe.setKind(DockStateProbe.DESK);
                break;
            default:
                dockStateProbe.setKind("-");
        }

        return dockStateProbe;
    }


    /**
     * Creates a probe that contains the current settings for screen timeout
     *
     * @param context the calling context
     * @return a new screen timeout probe
     */
    public ScreenOffTimeoutProbe createScreenOffTimeoutProbe(Context context) {
        ScreenOffTimeoutProbe probe = new ScreenOffTimeoutProbe();
        probe.setDate(System.currentTimeMillis());
        probe.setTimeout(Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, -1));
        return probe;
    }

    /**
     * Creates a probe that determines whether the device in a DREAMING state depending on the intent
     *
     * @param intent the intent to inspect. See {@link Intent#ACTION_DREAMING_STARTED} and {@link Intent#ACTION_DREAMING_STOPPED}
     * @return
     */
    public DreamingModeProbe createDreamingModeProbe(Intent intent) {
        DreamingModeProbe probe = new DreamingModeProbe();
        probe.setDate(System.currentTimeMillis());
        probe.setState(intent.getAction().equals(Intent.ACTION_DREAMING_STARTED) ? DreamingModeProbe.ON : DreamingModeProbe.OFF);
        return probe;
    }


    /**
     * Creates a probe based capturing the time of a boot completed even or a shutdown even
     *
     * @param intent the captured intent. See {@link Intent#ACTION_SHUTDOWN}, {@link Intent#ACTION_BOOT_COMPLETED}
     * @return
     */
    public SystemUptimeProbe createSystemUptimeProbe(Intent intent) {
        SystemUptimeProbe probe = new SystemUptimeProbe();
        probe.setDate(System.currentTimeMillis());
        probe.setState(intent.getAction().equals(Intent.ACTION_SHUTDOWN) ? SystemUptimeProbe.SHUTDOWN : SystemUptimeProbe.BOOT);
        return probe;
    }

    /**
     * Creates a new probe when the user is present based on an intent
     *
     * @return a new probe
     */
    public UserPresenceProbe createUserPresenceProbe() {
        UserPresenceProbe probe = new UserPresenceProbe();
        probe.setDate(System.currentTimeMillis());
        probe.setPresence(UserPresenceProbe.PRESENT);
        return probe;
    }

    /**
     * Creates a new time changed probe for when the system broadcasts that the time set has changed
     *
     * @return a new time changed probe
     */
    public TimeChangedProbe createTimeChangedProbe() {

        TimeChangedProbe probe = new TimeChangedProbe();
        probe.setDate(System.currentTimeMillis());
        probe.setChange(TimeChangedProbe.TIMEADJUST);
        return probe;
    }


}
