package edu.umd.fcmd.sensorlisteners.listener.network;

import android.os.Handler;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import edu.umd.fcmd.sensorlisteners.model.network.MSMSProbe;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by MMueller on 1/23/2017.
 */
public class MMSOutObserverTest {
    WifiListener mockWifiListener;
    Handler mockHandler;

    @Before
    public void setUp() throws Exception {
        mockWifiListener = mock(WifiListener.class);
        mockHandler = mock(Handler.class);

    }

    @Test
    public void constructorTest() throws Exception {
        MMSOutObserver cut = new MMSOutObserver(mockHandler, mockWifiListener);

    }

    @Test
    public void onChange() throws Exception {
        MMSOutObserver cut = new MMSOutObserver(mockHandler, mockWifiListener);

        cut.onChange(true);

        verify(mockWifiListener).onUpdate(any(MSMSProbe.class));

    }

    @Test
    public void deliverSelfNotifications() throws Exception {
        MMSOutObserver cut = new MMSOutObserver(mockHandler, mockWifiListener);

        Assert.assertFalse(cut.deliverSelfNotifications());

    }

}