package edu.umd.fcmd.sensorlisteners.listener.system;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;

import javax.inject.Inject;

import edu.umd.fcmd.sensorlisteners.listener.Listener;
import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.model.system.BuildVersionProvider;
import edu.umd.fcmd.sensorlisteners.model.system.SystemProbeFactory;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;
import timber.log.Timber;

/**
 * Created by MMueller on 12/30/2016.
 * <p>
 * Listener to certain system events as:
 * - Dreaming mode
 * - Screen state
 */
public class SystemListener extends BroadcastReceiver implements Listener {
    private boolean runningState;

    private final Context mContext;
    private final ProbeManager<Probe> probeManager;
    private final String madcapVerison;
    private final SystemProbeFactory factory;


    private ContentObserver settingsContentObserver;


    @Inject
    public SystemListener(Context context,
                          ProbeManager<Probe> probeManager,
                          SystemProbeFactory factory,
                          BuildVersionProvider buildVersionProvider) {
        mContext = context;
        this.probeManager = probeManager;
        this.factory = factory;
        madcapVerison = buildVersionProvider.getBuildVersion();
    }

    @Override
    public void onUpdate(Probe state) {
        probeManager.save(state);
    }

    @Override
    public void startListening() {
        if (!runningState) {

            IntentFilter systemFilter = new IntentFilter();
            systemFilter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            systemFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
            systemFilter.addAction(Intent.ACTION_SHUTDOWN);
            systemFilter.addAction(Intent.ACTION_USER_PRESENT);
            systemFilter.addAction(Intent.ACTION_DREAMING_STARTED);
            systemFilter.addAction(Intent.ACTION_DREAMING_STOPPED);
            systemFilter.addAction(Intent.ACTION_SCREEN_ON);
            systemFilter.addAction(Intent.ACTION_SCREEN_OFF);
            systemFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            systemFilter.addAction(Intent.ACTION_TIME_CHANGED);
            systemFilter.addAction(Intent.ACTION_INPUT_METHOD_CHANGED);
            systemFilter.addAction(Intent.ACTION_DOCK_EVENT);
            mContext.registerReceiver(this, systemFilter);

            settingsContentObserver = new ContentObserver(new Handler()) {
                @Override
                public void onChange(boolean selfChange) {
                    onUpdate(factory.createScreenOffTimeoutProbe(mContext));
                }
            };

            mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(Settings.System.SCREEN_OFF_TIMEOUT), true, settingsContentObserver);

            //sendInitalProbes();
            onUpdate(factory.createScreenProbe(mContext));
            onUpdate(factory.createAirplaneModeProbe(mContext));
            onUpdate(factory.createTimezoneProbe());
            onUpdate(factory.createSystemInfoProbe(mContext, madcapVerison));
            onUpdate(factory.createInputMethodProbe(mContext));
            onUpdate(factory.createDockStateProbe(mContext));
            onUpdate(factory.createScreenOffTimeoutProbe(mContext));
            runningState = true;
        }
    }


    @Override
    public void stopListening() {
        if (runningState) {
            mContext.unregisterReceiver(this);
            mContext.getContentResolver().unregisterContentObserver(settingsContentObserver);
            runningState = false;
        }
    }

    @Override
    public boolean isPermittedByUser() {
        //non dangerous permission
        return true;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_DREAMING_STARTED:
            case Intent.ACTION_DREAMING_STOPPED:
                onUpdate(factory.createDreamingModeProbe(intent));
                break;
            case Intent.ACTION_SCREEN_OFF:
            case Intent.ACTION_SCREEN_ON:
                onUpdate(factory.createScreenProbe(intent));
                break;
            case Intent.ACTION_AIRPLANE_MODE_CHANGED:
                onUpdate(factory.createAirplaneModeProbe(context));
                break;
            case Intent.ACTION_BOOT_COMPLETED:
            case Intent.ACTION_SHUTDOWN:
                onUpdate(factory.createSystemUptimeProbe(intent));
                break;
            case Intent.ACTION_USER_PRESENT:
                onUpdate(factory.createUserPresenceProbe());
                break;
            case Intent.ACTION_INPUT_METHOD_CHANGED:
                onUpdate(factory.createInputMethodProbe(context));
                break;
            case Intent.ACTION_TIMEZONE_CHANGED:
                onUpdate(factory.createTimezoneProbe());
                break;
            case Intent.ACTION_DOCK_EVENT:
                onUpdate(factory.createDockStateProbe(context));
                break;
            case Intent.ACTION_TIME_CHANGED:
                onUpdate(factory.createTimeChangedProbe());
                break;
            default:
                Timber.d("Unkown system event received");
                break;
        }
    }
}