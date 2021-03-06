package edu.umd.fcmd.sensorlisteners.listener.network;

import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * Created by MMueller on 12/27/2016.
 */
public class TelephonyListenerFactoryTest {
    Context mockContext;
    WifiListener mockWifiListener;

    @Before
    public void setUp() throws Exception {
        mockContext = mock(Context.class);
        mockWifiListener = mock(WifiListener.class);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void create() throws Exception {
        TelephonyListenerFactory cut = new TelephonyListenerFactory();

        cut.create(mockContext, mockWifiListener);
    }

}