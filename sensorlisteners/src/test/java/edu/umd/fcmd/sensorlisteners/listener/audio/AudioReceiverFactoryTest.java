package edu.umd.fcmd.sensorlisteners.listener.audio;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by MMueller on 1/24/2017.
 */
public class AudioReceiverFactoryTest {
    AudioListener mockAudioListener;

    @Before
    public void setUp() throws Exception {
        AudioListener mockAudioListener = mock(AudioListener.class);

    }

    @Test
    public void constructorTest() throws Exception {
        AudioReceiverFactory cut = new AudioReceiverFactory();

    }

    @Test
    public void create() throws Exception {
        AudioReceiverFactory cut = new AudioReceiverFactory();

        cut.create(mockAudioListener);

    }

}