package edu.umd.fcmd.sensorlisteners.listener.network;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by MMueller on 1/23/2017.
 */
public class MMSOutObserverFactoryTest {
    NetworkListener mockNetworkListener;

    @Before
    public void setUp() throws Exception {
        mockNetworkListener = mock(NetworkListener.class);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void create() throws Exception {
        MMSOutObserverFactory cut = new MMSOutObserverFactory();
        cut.create(mockNetworkListener);

    }

}