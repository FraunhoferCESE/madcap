package edu.umd.fcmd.sensorlisteners.listener.audio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;

import javax.inject.Inject;

import edu.umd.fcmd.sensorlisteners.listener.Listener;
import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.model.audio.AudioProbeFactory;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;
import timber.log.Timber;

/**
 * Created by MMueller on 1/23/2017.
 * <p>
 * Listener class for audio related events.
 * <p>
 * Configurable significance delta for volume
 * updates.
 */
public class AudioListener extends BroadcastReceiver implements Listener {
    private final Context mContext;
    private final ProbeManager<Probe> probeProbeManager;
    private final AudioProbeFactory factory;
    private final AudioManager audioManager;

    private boolean runningState;

    /**
     * Main constructor.
     *
     * @param context           the mContext.
     * @param probeProbeManager the update probe manager.
     * @param factory           the factory for creating audio-related probes
     * @param audioManager      the system audio manager
     */
    @Inject
    public AudioListener(Context context,
                         ProbeManager<Probe> probeProbeManager,
                         AudioProbeFactory factory,
                         AudioManager audioManager) {
        mContext = context;
        this.probeProbeManager = probeProbeManager;
        this.factory = factory;
        this.audioManager = audioManager;
    }

    @Override
    public void onUpdate(Probe state) {
        probeProbeManager.save(state);
    }

    @Override
    public void startListening() {
        if (!runningState && isPermittedByUser()) {
            Timber.d("startListening");

            IntentFilter intentFilter = new IntentFilter();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                intentFilter.addAction(AudioManager.ACTION_HEADSET_PLUG);
            }
            intentFilter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);

            onUpdate(factory.createRingerProbe(audioManager.getRingerMode()));

            mContext.registerReceiver(this, intentFilter);
            runningState = true;
        }
    }

    @Override
    public void stopListening() {
        if (runningState) {
            mContext.unregisterReceiver(this);
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
        if (AudioManager.ACTION_HEADSET_PLUG.equals(intent.getAction())) {
            onUpdate(factory.createHeadphoneProbe(intent.getIntExtra("state", -1)));
        } else if (AudioManager.RINGER_MODE_CHANGED_ACTION.equals(intent.getAction())) {
            onUpdate(factory.createRingerProbe(intent.getIntExtra(AudioManager.EXTRA_RINGER_MODE, -1)));
        }
    }
}
