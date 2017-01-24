package edu.umd.fcmd.sensorlisteners.listener.audio;

import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;

import edu.umd.fcmd.sensorlisteners.NoSensorFoundException;
import edu.umd.fcmd.sensorlisteners.issuehandling.PermissionDeniedHandler;
import edu.umd.fcmd.sensorlisteners.listener.Listener;
import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.model.audio.HeadphoneProbe;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;

/**
 * Created by MMueller on 1/23/2017.
 *
 * Listener class for audio related events.
 */
public class AudioListener implements Listener {
    private final String TAG = getClass().getSimpleName();
    private final Context context;
    private final ProbeManager<Probe> probeProbeManager;
    private final PermissionDeniedHandler permissionDeniedHandler;
    private final AudioReceiverFactory audioReceiverFactory;

    private AudioReceiver audioReceiver;
    private AudioManager audioManager;

    private boolean runningState;

    public AudioListener(Context context,
                         ProbeManager<Probe> probeProbeManager,
                         AudioReceiverFactory audioReceiverFactory,
                         PermissionDeniedHandler permissionDeniedHandler){
        this.context = context;
        this.probeProbeManager = probeProbeManager;
        this.audioReceiverFactory = audioReceiverFactory;
        this.permissionDeniedHandler = permissionDeniedHandler;

        audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void onUpdate(Probe state) {
        probeProbeManager.save(state);
    }

    @Override
    public void startListening() throws NoSensorFoundException {
        if(!runningState){
            sendInitialProbes();
            audioReceiver = audioReceiverFactory.create(this);

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(AudioManager.ACTION_HEADSET_PLUG);

            context.registerReceiver(audioReceiver, intentFilter);

        }
        runningState = true;
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

    /**
     * Sends initial audio related probes.
     */
    private void sendInitialProbes(){
        // Headphone plug state probe.
        HeadphoneProbe headphoneProbe = new HeadphoneProbe();
        headphoneProbe.setDate(System.currentTimeMillis());
        headphoneProbe.setPlugState(getCurrentHeadphonePlugState());
        onUpdate(headphoneProbe);

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
}
