package edu.umd.fcmd.sensorlisteners.listener.network;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by MMueller on 1/23/2017.
 */
public class MSMSReceiverFactoryTest {
    NetworkListener mockNetworkListener = mock(NetworkListener.class);

    @Before
    public void setUp() throws Exception {
        mockNetworkListener = mock(NetworkListener.class);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void create() throws Exception {
        MSMSReceiverFactory cut = new MSMSReceiverFactory();

        cut.create(mockNetworkListener);
    }

}