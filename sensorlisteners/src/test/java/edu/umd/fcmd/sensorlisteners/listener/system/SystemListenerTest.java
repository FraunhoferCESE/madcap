package edu.umd.fcmd.sensorlisteners.listener.system;

import android.content.Context;
import android.telephony.ServiceState;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by MMueller on 1/3/2017.
 */
public class SystemListenerTest {
    Context mockContext;
    ProbeManager<Probe> mockProbeManager;
    SystemReceiverFactory mockSystemReceiverFactory;
    String mockMadcapVersion;

    @Before
    public void setUp() throws Exception {
        mockContext = mock(Context.class);
        mockProbeManager = mock(ProbeManager.class);
        mockSystemReceiverFactory = mock(SystemReceiverFactory.class);
        mockMadcapVersion = "version";
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void onUpdate() throws Exception {

    }

    @Test
    public void startListening() throws Exception {

    }

    @Test
    public void stopListening() throws Exception {

    }

    @Test
    public void isRunning() throws Exception {

    }

    @Test
    public void getCurrentAirplaneModeState() throws Exception {

    }

}