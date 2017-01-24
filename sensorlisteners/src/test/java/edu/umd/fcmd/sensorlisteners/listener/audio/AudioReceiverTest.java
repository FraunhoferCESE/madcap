package edu.umd.fcmd.sensorlisteners.listener.audio;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.umd.fcmd.sensorlisteners.model.audio.HeadphoneProbe;
import edu.umd.fcmd.sensorlisteners.model.audio.RingerProbe;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by MMueller on 1/24/2017.
 */
public class AudioReceiverTest {
    AudioListener mockAudioListener;

    @Before
    public void setUp() throws Exception {
        mockAudioListener = mock(AudioListener.class);

    }

    @Test
    public void constructorTest() throws Exception {
        AudioReceiver cut = new AudioReceiver(mockAudioListener);

    }

    @Test
    public void onReceive() throws Exception {
        AudioReceiver cut = new AudioReceiver(mockAudioListener);

        Context mockContext = mock(Context.class);
        Intent mockIntent = mock(Intent.class);

        when(mockIntent.getAction()).thenReturn(AudioManager.ACTION_HEADSET_PLUG);
        cut.onReceive(mockContext, mockIntent);
        verify(mockAudioListener).onUpdate(any(HeadphoneProbe.class));

        when(mockIntent.getAction()).thenReturn(AudioManager.RINGER_MODE_CHANGED_ACTION);
        cut.onReceive(mockContext, mockIntent);
        verify(mockAudioListener, atLeastOnce()).onUpdate(any(RingerProbe.class));

        when(mockIntent.getAction()).thenReturn("android.media.VOLUME_CHANGED_ACTION");
        cut.onReceive(mockContext, mockIntent);
        verify(mockAudioListener).evaluateVolumeChanges();

        when(mockIntent.getAction()).thenReturn("Schnitzel");
        cut.onReceive(mockContext, mockIntent);
    }
}