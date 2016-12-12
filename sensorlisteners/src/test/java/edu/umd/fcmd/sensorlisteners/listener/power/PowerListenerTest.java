package edu.umd.fcmd.sensorlisteners.listener.power;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by MMueller on 12/12/2016.
 */
public class PowerListenerTest {
    Context mockContext;
    ProbeManager<Probe> mockProbeManager;

    @Before
    public void setUp() throws Exception {
        mockContext = mock(Context.class);
        mockProbeManager = mock(ProbeManager.class);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void onUpdate() throws Exception {
        PowerListener cut = new PowerListener(mockContext, mockProbeManager);

        Probe mockProbe = mock(Probe.class);
        cut.onUpdate(mockProbe);

        verify(mockProbeManager).save(mockProbe);
    }

    @Test
    public void startListening() throws Exception {
        PowerListener cut = new PowerListener(mockContext, mockProbeManager);

        cut.startListening();

        Assert.assertTrue(cut.isRunning());

        Intent mockIntent = mock(Intent.class);
        when(mockContext.registerReceiver(any(BroadcastReceiver.class), any(IntentFilter.class))).thenReturn(mockIntent);
        cut.startListening();
    }

    @Test
    public void stopListening() throws Exception {
        PowerListener cut = new PowerListener(mockContext, mockProbeManager);

        cut.startListening();
        cut.stopListening();
        Assert.assertFalse(cut.isRunning());
    }

    @Test
    public void isRunning() throws Exception {
        PowerListener cut = new PowerListener(mockContext, mockProbeManager);

        cut.startListening();
        Assert.assertTrue(cut.isRunning());

        cut.stopListening();
        Assert.assertFalse(cut.isRunning());

    }

}