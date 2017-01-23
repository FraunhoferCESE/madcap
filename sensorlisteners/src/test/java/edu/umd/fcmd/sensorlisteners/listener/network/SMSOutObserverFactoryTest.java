package edu.umd.fcmd.sensorlisteners.listener.network;

import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by MMueller on 1/23/2017.
 */
public class SMSOutObserverFactoryTest {
    NetworkListener mockNetworkListener;
    Context mockContext;

    @Before
    public void setUp() throws Exception {
        mockNetworkListener = mock(NetworkListener.class);
        mockContext = mock(Context.class);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void create() throws Exception {
        SMSOutObserverFactory cut = new SMSOutObserverFactory();

        cut.create(mockNetworkListener, mockContext);
    }

}