package edu.umd.fcmd.sensorlisteners.listener.audio;

import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.util.Log;

import javax.inject.Inject;

import edu.umd.fcmd.sensorlisteners.NoSensorFoundException;
import edu.umd.fcmd.sensorlisteners.issuehandling.PermissionsManager;
import edu.umd.fcmd.sensorlisteners.listener.Listener;
import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.model.audio.HeadphoneProbe;
import edu.umd.fcmd.sensorlisteners.model.audio.RingerProbe;
import edu.umd.fcmd.sensorlisteners.model.audio.VolumeProbe;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;

/**
 * Created by MMueller on 1/23/2017.
 *
 * Listener class for audio related events.
 *
 * Configurable significance delta for volume
 * updates.
 */
public class AudioListener implements Listener{
    private final String TAG = getClass().getSimpleName();
    private final Context context;
    private final ProbeManager<Probe> probeProbeManager;
    private final PermissionsManager permissionsManager;
    private final AudioReceiverFactory audioReceiverFactory;
    private static final int SIGIFICANCE_DELTA = 0;

    private AudioReceiver audioReceiver;
    private AudioManager audioManager;

    private int alarmVolume;
    private int dtmfVolume;
    private int musicVolume;
    private int notificationVolume;
    private int ringVolume;
    private int systemVolume;
    private int voiceCallVolume;

    private boolean runningState;

    /**
     * Main constructor.
     * @param context the context.
     * @param probeProbeManager the update probe manager.
     * @param audioReceiverFactory the audio receiver factory.
     * @param permissionsManager the permission handler.
     */
    @Inject
    public AudioListener(Context context,
                         ProbeManager<Probe> probeProbeManager,
                         AudioReceiverFactory audioReceiverFactory,
                         PermissionsManager permissionsManager){
        this.context = context;
        this.probeProbeManager = probeProbeManager;
        this.audioReceiverFactory = audioReceiverFactory;
        this.permissionsManager = permissionsManager;

        audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void onUpdate(Probe state) {
        probeProbeManager.save(state);
    }

    @Override
    public void startListening() throws NoSensorFoundException {
        if(!runningState) {
            if (isPermittedByUser()) {
                sendInitialProbes();
                audioReceiver = audioReceiverFactory.create(this);

                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(AudioManager.ACTION_HEADSET_PLUG);
                intentFilter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);
                intentFilter.addAction("android.media.VOLUME_CHANGED_ACTION");

                context.registerReceiver(audioReceiver, intentFilter);
            }

            runningState = true;
        }else {
            permissionsManager.requestPermissionFromNotification();
            Log.i(TAG,"Audio listener NOT listening");
            runningState = false;
        }
    }

    @Override
    public void stopListening() {
        if(runningState){
            if(context != null && audioReceiver != null){
                context.unregisterReceiver(audioReceiver);
            }
        }
        runningState = false;
    }

    @Override
    public boolean isRunning() {
        return runningState;
    }

    @Override
    public boolean isPermittedByUser() {
        //TODO: system check for permission
        //non dangerous permission
        return true;
    }

    /**
     * Sends initial audio related probes.
     */
    private void sendInitialProbes(){
       //Initially evaluating changes.
        evaluateVolumeChanges(1000, 1000, 1000, 1000, 1000, 1000, 1000);
    }

    /**
     * Evaluates volume changes with explicit old volumes.
     * Creates new probes if necessary.
     *
     * @param oldAlarmVolume the old alarm volume.
     * @param oldDtmfVolume the old dtmf volume.
     * @param oldMusicVolume the old music volume.
     * @param oldNotificationVolume the old notification volume.
     * @param oldRingVolume the old ringer volume.
     * @param oldSystemVolume the old system volume.
     * @param oldVoiceCallVolume the old voice call volume.
     */
    private void evaluateVolumeChanges(int oldAlarmVolume,
                               int oldDtmfVolume,
                               int oldMusicVolume,
                               int oldNotificationVolume,
                               int oldRingVolume,
                               int oldSystemVolume,
                               int oldVoiceCallVolume){

        if((getCurrentAlarmVolume() >= oldAlarmVolume + SIGIFICANCE_DELTA) ||
                (getCurrentAlarmVolume() <= oldAlarmVolume - SIGIFICANCE_DELTA)){
            VolumeProbe alarmVolumeProbe = new VolumeProbe();
            alarmVolumeProbe.setDate(System.currentTimeMillis());
            alarmVolumeProbe.setKind(VolumeProbe.ALARM);
            alarmVolumeProbe.setVolume(getCurrentAlarmVolume());
            onUpdate(alarmVolumeProbe);
            alarmVolume = getCurrentAlarmVolume();
        }

        if((getCurrentDTMFVolume() >= oldDtmfVolume + SIGIFICANCE_DELTA) ||
                (getCurrentDTMFVolume() <= oldDtmfVolume - SIGIFICANCE_DELTA)){
            VolumeProbe dtmfVolumeProbe = new VolumeProbe();
            dtmfVolumeProbe.setDate(System.currentTimeMillis());
            dtmfVolumeProbe.setKind(VolumeProbe.DTMF);
            dtmfVolumeProbe.setVolume(getCurrentDTMFVolume());
            onUpdate(dtmfVolumeProbe);
            dtmfVolume = getCurrentDTMFVolume();
        }

        if((getCurrentMusicVolume() >= oldMusicVolume + SIGIFICANCE_DELTA) ||
                (getCurrentMusicVolume() <= oldMusicVolume - SIGIFICANCE_DELTA)){
            VolumeProbe musicVolumeProbe = new VolumeProbe();
            musicVolumeProbe.setDate(System.currentTimeMillis());
            musicVolumeProbe.setKind(VolumeProbe.MUSIC);
            musicVolumeProbe.setVolume(getCurrentMusicVolume());
            onUpdate(musicVolumeProbe);
            musicVolume = getCurrentMusicVolume();
        }


        if((getCurrentNotificationVolume() >= oldNotificationVolume + SIGIFICANCE_DELTA) ||
                (getCurrentNotificationVolume() <= oldNotificationVolume - SIGIFICANCE_DELTA)){
            VolumeProbe notificationVolumeProbe = new VolumeProbe();
            notificationVolumeProbe.setDate(System.currentTimeMillis());
            notificationVolumeProbe.setKind(VolumeProbe.NOTIFICATION);
            notificationVolumeProbe.setVolume(getCurrentNotificationVolume());
            onUpdate(notificationVolumeProbe);
            notificationVolume = getCurrentNotificationVolume();
        }


        if((getCurrentRingVolume() >= oldRingVolume + SIGIFICANCE_DELTA) ||
                (getCurrentRingVolume() <= oldRingVolume - SIGIFICANCE_DELTA) ){
            VolumeProbe ringVolumeProbe = new VolumeProbe();
            ringVolumeProbe.setDate(System.currentTimeMillis());
            ringVolumeProbe.setKind(VolumeProbe.RING);
            ringVolumeProbe.setVolume(getCurrentRingVolume());
            onUpdate(ringVolumeProbe);
            ringVolume = getCurrentRingVolume();
        }


        if((getCurrentSystemVolume() >= oldSystemVolume + SIGIFICANCE_DELTA) ||
                (getCurrentSystemVolume() <= oldSystemVolume - SIGIFICANCE_DELTA)){
            VolumeProbe systemSoundProbe = new VolumeProbe();
            systemSoundProbe.setDate(System.currentTimeMillis());
            systemSoundProbe.setKind(VolumeProbe.SYSTEM);
            systemSoundProbe.setVolume(getCurrentSystemVolume());
            onUpdate(systemSoundProbe);
            systemVolume = getCurrentSystemVolume();
        }


        if((getCurrentVoiceCallVolume() >= oldVoiceCallVolume + SIGIFICANCE_DELTA) ||
                (getCurrentVoiceCallVolume() <= oldVoiceCallVolume - SIGIFICANCE_DELTA)){
            VolumeProbe callVolumeProbe = new VolumeProbe();
            callVolumeProbe.setDate(System.currentTimeMillis());
            callVolumeProbe.setKind(VolumeProbe.VOICE_CALL);
            callVolumeProbe.setVolume(getCurrentVoiceCallVolume());
            onUpdate(callVolumeProbe);
            voiceCallVolume = getCurrentVoiceCallVolume();
        }
    }

    /**
     * Evaluates the volume changes with cached volume.
     * Creates new probes if necessary.
     */
    void evaluateVolumeChanges(){
        evaluateVolumeChanges(alarmVolume,
                dtmfVolume,
                musicVolume,
                notificationVolume,
                ringVolume,
                systemVolume,
                voiceCallVolume);
    }

    /**
     * Gets if the headphone is plugged in or not.
     * @return PLUGGED or UNPLUGGED.
     */
    String getCurrentHeadphonePlugState(){
        if(audioManager.isWiredHeadsetOn()){
            return HeadphoneProbe.PLUGGED;
        }else{
            return HeadphoneProbe.UNPLUGGED;
        }
    }

    /**
     * Gets the currently active ringer mode of
     * the phone.
     *
     * @return NORMAL or
     * SILENT or
     * VIBRATE
     */
    String getCurrentRingerMode(){
        int mode = audioManager.getRingerMode();

        switch(mode){
            case AudioManager.RINGER_MODE_NORMAL:
                return RingerProbe.NORMAL;
            case AudioManager.RINGER_MODE_SILENT:
                return RingerProbe.SILENT;
            case AudioManager.RINGER_MODE_VIBRATE:
                return RingerProbe.VIBRATE;
            default:
                Log.e(TAG, "unspecified current ringer mode detected");
                return"";
        }
    }

    /**
     * Gets the current alarm volume.
     * @return index [0..100]
     */
    private int getCurrentAlarmVolume(){
        return audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
    }

    /**
     * Gets the current dtmf volume.
     * @return index [0..100]
     */
    private int getCurrentDTMFVolume(){
        return audioManager.getStreamVolume(AudioManager.STREAM_DTMF);
    }

    /**
     * Gets the current music volume.
     * @return index [0..100]
     */
    private int getCurrentMusicVolume(){
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * Gets the current notification sounds volume.
     * @return index [0..100]
     */
    private int getCurrentNotificationVolume(){
        return audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
    }

    /**
     * Gets the current system sounds volume.
     * @return index [0..100]
     */
    private int getCurrentSystemVolume(){
        return audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
    }

    /**
     * Gets the current voice call volume.
     * @return index [0..100]
     */
    private int getCurrentVoiceCallVolume(){
        return audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
    }

    /**
     * Gets the current ring sounds volume.
     * @return index [0..100]
     */
    private int getCurrentRingVolume(){
        return audioManager.getStreamVolume(AudioManager.STREAM_RING);
    }

}
