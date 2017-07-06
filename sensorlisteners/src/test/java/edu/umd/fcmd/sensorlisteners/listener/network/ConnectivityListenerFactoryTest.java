package edu.umd.fcmd.sensorlisteners.listener.network;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * Created by MMueller on 12/27/2016.
 */
public class ConnectivityListenerFactoryTest {
    WifiListener mockWifiListener;

    @Before
    public void setUp() throws Exception {
        mockWifiListener = mock(WifiListener.class);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void create() throws Exception {
        ConnectionInfoReceiverFactory cut = new ConnectionInfoReceiverFactory();
        cut.create(mockWifiListener);
    }

}