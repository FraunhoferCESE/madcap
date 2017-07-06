package edu.umd.fcmd.sensorlisteners.listener.audio;

import android.content.Context;
import android.media.AudioManager;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import edu.umd.fcmd.sensorlisteners.issuehandling.PermissionsManager;
import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.model.audio.HeadphoneProbe;
import edu.umd.fcmd.sensorlisteners.model.audio.RingerProbe;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by MMueller on 1/24/2017.
 */
public class AudioListenerTest {
    Context mockContext;
    ProbeManager<Probe> mockProbeManager;
    AudioReceiverFactory mockAudioReceiverFactory;
    int mockDelta = 0;
    PermissionsManager mockPermissionsManager;
    AudioManager mockAudioManager;

    @Before
    public void setUp() throws Exception {
        mockContext = mock(Context.class);
        mockProbeManager = mock(ProbeManager.class);
        mockAudioReceiverFactory = mock(AudioReceiverFactory.class);
        mockPermissionsManager = mock(PermissionsManager.class);
        mockAudioManager = mock(AudioManager.class);

        when(mockContext.getSystemService(Context.AUDIO_SERVICE)).thenReturn(mockAudioManager);

    }

    @Test
    public void constructorTest() throws Exception {
        AudioListener cut = new AudioListener(mockContext,
                mockProbeManager,
                mockAudioReceiverFactory,
                mockDelta,
                mockPermissionsManager);
    }

    @Test
    public void onUpdate() throws Exception {
        AudioListener cut = new AudioListener(mockContext,
                mockProbeManager,
                mockAudioReceiverFactory,
                mockDelta,
                mockPermissionsManager);

        Probe mockProbe = mock(Probe.class);

        cut.onUpdate(mockProbe);
        verify(mockProbeManager).save(mockProbe);
    }

    @Test
    public void startListening() throws Exception {
        AudioListener cut = new AudioListener(mockContext,
                mockProbeManager,
                mockAudioReceiverFactory,
                mockDelta,
                mockPermissionsManager);

        cut.startListening();

        Assert.assertTrue(cut.isRunning());

    }

    @Test
    public void stopListening() throws Exception {
        AudioListener cut = new AudioListener(mockContext,
                mockProbeManager,
                mockAudioReceiverFactory,
                mockDelta,
                mockPermissionsManager);

        cut.startListening();
        cut.stopListening();

        Assert.assertFalse(cut.isRunning());

    }

    @Test
    public void isRunning() throws Exception {
        AudioListener cut = new AudioListener(mockContext,
                mockProbeManager,
                mockAudioReceiverFactory,
                mockDelta,
                mockPermissionsManager);

        cut.startListening();

        Assert.assertTrue(cut.isRunning());

        cut.stopListening();

        Assert.assertFalse(cut.isRunning());

    }

    @Test
    public void evaluateVolumeChanges() throws Exception {
        AudioListener cut = new AudioListener(mockContext,
                mockProbeManager,
                mockAudioReceiverFactory,
                mockDelta,
                mockPermissionsManager);

        cut.evaluateVolumeChanges();

    }

    @Test
    public void getCurrentHeadphonePlugState() throws Exception {
        AudioListener cut = new AudioListener(mockContext,
                mockProbeManager,
                mockAudioReceiverFactory,
                mockDelta,
                mockPermissionsManager);

        when(mockAudioManager.isWiredHeadsetOn()).thenReturn(true);
        Assert.assertEquals(cut.getCurrentHeadphonePlugState(), HeadphoneProbe.PLUGGED);

        when(mockAudioManager.isWiredHeadsetOn()).thenReturn(false);
        Assert.assertEquals(cut.getCurrentHeadphonePlugState(), HeadphoneProbe.UNPLUGGED);

    }

    @Test
    public void getCurrentRingerMode() throws Exception {
        AudioListener cut = new AudioListener(mockContext,
                mockProbeManager,
                mockAudioReceiverFactory,
                mockDelta,
                mockPermissionsManager);

        when(mockAudioManager.getRingerMode()).thenReturn(AudioManager.RINGER_MODE_NORMAL);
        Assert.assertEquals(cut.getCurrentRingerMode(), RingerProbe.NORMAL);

        when(mockAudioManager.getRingerMode()).thenReturn(AudioManager.RINGER_MODE_SILENT);
        Assert.assertEquals(cut.getCurrentRingerMode(), RingerProbe.SILENT);

        when(mockAudioManager.getRingerMode()).thenReturn(AudioManager.RINGER_MODE_VIBRATE);
        Assert.assertEquals(cut.getCurrentRingerMode(), RingerProbe.VIBRATE);

        when(mockAudioManager.getRingerMode()).thenReturn(99999999);
        Assert.assertEquals(cut.getCurrentRingerMode(), "");
    }

}