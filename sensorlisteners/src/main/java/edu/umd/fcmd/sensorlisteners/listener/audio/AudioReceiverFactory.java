package edu.umd.fcmd.sensorlisteners.listener.audio;

import javax.inject.Inject;

/**
 * Created by MMueller on 1/24/2017.
 *
 * Factory class for AudioReceivers following the factory pattern.
 */
public class AudioReceiverFactory {

    @Inject
    public AudioReceiverFactory() {}

    public AudioReceiver create(AudioListener audioListener){
        return new AudioReceiver(audioListener);
    }
}
