package org.fraunhofer.cese.madcap.Probe;

import android.content.Intent;
import android.media.AudioManager;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;


import edu.mit.media.funf.probe.Probe;

/**
 *
 */
public class AudioProbe extends Probe.Base implements Probe.ContinuousProbe {

    private static final long SLEEP_DURATION = 15000;
    private static final String TAG = "AudioProbe: ";
    private static Thread audioProbeDeliverer;
    private static int oldMode = -1;
    private static int oldRingtoneMode = -1;
    private static boolean oldMic;
    private static boolean oldMusic;
    private static boolean oldHeadset;

    @Override
    protected void onEnable() {

        onStart();
        Gson gson = getGson();
        AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);

        audioProbeDeliverer = createAudioProbeDeliverer(gson, audioManager);

        audioProbeDeliverer.start();

        Log.i(TAG, "AudioProbe enabled.");
    }


    private Thread createAudioProbeDeliverer(Gson gson, final AudioManager audioManager) {


        audioProbeDeliverer = new Thread(new Runnable() {
            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                boolean run = true;
                while (run && !audioProbeDeliverer.isInterrupted()) {

                    StringBuilder audioInfo = new StringBuilder("");


                    //Audio Mode
                    audioInfo.append("AudioMode: ");
                    switch (audioManager.getMode()) {
                        case AudioManager.MODE_NORMAL:
                            audioInfo.append("MODE_NORMAL; ");
                            break;
                        case AudioManager.MODE_RINGTONE:
                            audioInfo.append("MODE_RINGTONE; ");
                            break;
                        case AudioManager.MODE_IN_CALL:
                            audioInfo.append("MODE_IN_CALL; ");
                            break;
                        case AudioManager.MODE_IN_COMMUNICATION:
                            audioInfo.append("MODE_IN_COMMUNICATION; ");
                            break;
                        default:
                            Log.i(TAG, "Something went wrong; ");
                            break;
                    }

                    //Ringtone Mode
                    audioInfo.append("RingtoneMode: ");
                    switch (audioManager.getRingerMode()) {
                        case AudioManager.RINGER_MODE_NORMAL:
                            audioInfo.append("RINGER_MODE_NORMAL; ");
                            break;
                        case AudioManager.RINGER_MODE_VIBRATE:
                            audioInfo.append("RINGER_MODE_VIBRATE; ");
                            break;
                        case AudioManager.RINGER_MODE_SILENT:
                            audioInfo.append("RINGER_MODE_SILENT; ");
                            break;
                        default:
                            Log.i(TAG, "Something went wrong; ");
                            break;
                    }

                    //Microphone mute?
                    audioInfo.append("Microphone: ");
                    if (audioManager.isMicrophoneMute()) {
                        audioInfo.append("muted; ");
                    } else {
                        audioInfo.append("not muted; ");
                    }

                    //Music on?
                    audioInfo.append("Music: ");
                    if (audioManager.isMusicActive()) {
                        audioInfo.append("active; ");
                    } else {
                        audioInfo.append("inactive; ");
                    }

                    //Headset connected?
                    audioInfo.append("Headset: ");
                    if (audioManager.isWiredHeadsetOn()) {
                        audioInfo.append(" plugged; ");
                    } else {
                        audioInfo.append("no headset; ");
                    }

                    Intent intent = new Intent();
                    intent.putExtra(TAG, audioInfo.toString());

                    
                    if ((audioManager.getMode() != oldMode)
                            || (audioManager.getRingerMode() != oldRingtoneMode)
                            || (audioManager.isMicrophoneMute() != oldMic)
                            || (audioManager.isMusicActive() != oldMusic)
                            || (audioManager.isWiredHeadsetOn() != oldHeadset)) {

                        oldMode = audioManager.getMode();
                        oldRingtoneMode = audioManager.getRingerMode();
                        oldMic = audioManager.isMicrophoneMute();
                        oldMusic = audioManager.isMusicActive();
                        oldHeadset = audioManager.isWiredHeadsetOn();

                        sendData(intent);
                    }

                    try {
                        Thread.sleep(SLEEP_DURATION);
                    } catch (InterruptedException e) {
                        run = false;
                        Log.i(TAG, "AudioProbe interrupted.");
                    }
                }
            }
        });

        return audioProbeDeliverer;
    }

    public void sendData(Intent intent) {
        sendData(getGson().toJsonTree(intent).getAsJsonObject());
    }


    @Override
    protected void onDisable() {
        super.onDisable();
        audioProbeDeliverer.interrupt();
    }
}
