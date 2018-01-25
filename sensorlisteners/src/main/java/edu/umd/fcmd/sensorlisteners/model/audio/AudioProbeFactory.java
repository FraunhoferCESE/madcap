package edu.umd.fcmd.sensorlisteners.model.audio;

import android.media.AudioManager;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Factory class for creating audio-related probes.
 */
@SuppressWarnings("MethodMayBeStatic")
public class AudioProbeFactory {


    @Inject
    public AudioProbeFactory() { }

    /**
     * Creates a ringer probe to indicate the current ringer state.
     * Possible probes are normal, silent, vibrate.
     * If no ringer probe is detected, then it is unspecified by default.
     *
     * @param mode  see {@link AudioManager#EXTRA_RINGER_MODE}
     * @return a new ringer probe
     */
    public RingerProbe createRingerProbe(int mode) {
        RingerProbe ringerProbe = new RingerProbe();
        ringerProbe.setDate(System.currentTimeMillis());
        switch (mode) {
            case AudioManager.RINGER_MODE_NORMAL:
                ringerProbe.setMode(RingerProbe.NORMAL);
                break;
            case AudioManager.RINGER_MODE_SILENT:
                ringerProbe.setMode(RingerProbe.SILENT);
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                ringerProbe.setMode(RingerProbe.VIBRATE);
                break;
            default:
                Timber.w("unspecified current ringer mode detected: " + mode);
                ringerProbe.setMode(RingerProbe.UNKNOWN);
        }
        return ringerProbe;
    }

    /**
     * creates a headphone probe in response to a changing headphone state
     * Possible probes are plugged and unplugged.
     * If no headphone probe is detected, then it is unknown by default.
     *
     * @param state see {@link AudioManager#ACTION_HEADSET_PLUG}
     * @return a new headphone probe
     */
    public HeadphoneProbe createHeadphoneProbe(int state) {
        HeadphoneProbe headphoneProbe = new HeadphoneProbe();
        headphoneProbe.setDate(System.currentTimeMillis());

        switch(state) {
            case 0:
                headphoneProbe.setPlugState(HeadphoneProbe.UNPLUGGED);
                break;
            case 1:
                headphoneProbe.setPlugState(HeadphoneProbe.PLUGGED);
                break;
            default:
                headphoneProbe.setPlugState(HeadphoneProbe.UNKNOWN);
        }

        return headphoneProbe;
    }
}
