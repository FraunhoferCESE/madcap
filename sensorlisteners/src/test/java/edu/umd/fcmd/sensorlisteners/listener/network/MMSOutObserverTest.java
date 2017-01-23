package edu.umd.fcmd.sensorlisteners.listener.network;

import android.os.Handler;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.umd.fcmd.sensorlisteners.model.network.MSMSProbe;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by MMueller on 1/23/2017.
 */
public class MMSOutObserverTest {
    NetworkListener mockNetworkListener;
    Handler mockHandler;

    @Before
    public void setUp() throws Exception {
        mockNetworkListener = mock(NetworkListener.class);
        mockHandler = mock(Handler.class);

    }

    @Test
    public void constructorTest() throws Exception {
        MMSOutObserver cut = new MMSOutObserver(mockHandler, mockNetworkListener);

    }

    @Test
    public void onChange() throws Exception {
        MMSOutObserver cut = new MMSOutObserver(mockHandler, mockNetworkListener);

        cut.onChange(true);

        verify(mockNetworkListener).onUpdate(any(MSMSProbe.class));

    }

    @Test
    public void deliverSelfNotifications() throws Exception {
        MMSOutObserver cut = new MMSOutObserver(mockHandler, mockNetworkListener);

        Assert.assertFalse(cut.deliverSelfNotifications());

    }

}