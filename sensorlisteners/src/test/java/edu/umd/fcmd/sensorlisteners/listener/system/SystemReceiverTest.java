package edu.umd.fcmd.sensorlisteners.listener.system;

import android.content.Context;
import android.content.Intent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.umd.fcmd.sensorlisteners.model.system.AirplaneModeProbe;
import edu.umd.fcmd.sensorlisteners.model.system.DreamingModeProbe;
import edu.umd.fcmd.sensorlisteners.model.system.ScreenProbe;
import edu.umd.fcmd.sensorlisteners.model.system.SystemUptimeProbe;
import edu.umd.fcmd.sensorlisteners.model.system.UserPresenceProbe;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by MMueller on 1/3/2017.
 */
public class SystemReceiverTest {
    SystemListener mockSystemListener;
    Context mockContext;

    @Before
    public void setUp() throws Exception {
        mockSystemListener = mock(SystemListener.class);
        mockContext = mock(Context.class);
    }

    @Test
    public void constructorTest() throws Exception {
        SystemReceiver cut = new SystemReceiver(mockSystemListener, mockContext);
    }

    @Test
    public void onReceive() throws Exception {
        SystemReceiver cut = new SystemReceiver(mockSystemListener, mockContext);

        Intent mockIntent = mock(Intent.class);

        when(mockIntent.getAction()).thenReturn(Intent.ACTION_DREAMING_STARTED);
        cut.onReceive(mockContext, mockIntent);
        verify(mockSystemListener).onUpdate(any(DreamingModeProbe.class));

        when(mockIntent.getAction()).thenReturn(Intent.ACTION_DREAMING_STOPPED);
        cut.onReceive(mockContext, mockIntent);
        verify(mockSystemListener, times(2)).onUpdate(any(DreamingModeProbe.class));

        when(mockIntent.getAction()).thenReturn(Intent.ACTION_SCREEN_OFF);
        cut.onReceive(mockContext, mockIntent);
        verify(mockSystemListener, times(3)).onUpdate(any(ScreenProbe.class));

        when(mockIntent.getAction()).thenReturn(Intent.ACTION_SCREEN_ON);
        cut.onReceive(mockContext, mockIntent);
        verify(mockSystemListener, times(4)).onUpdate(any(ScreenProbe.class));

        when(mockIntent.getAction()).thenReturn(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        cut.onReceive(mockContext, mockIntent);
        verify(mockSystemListener, times(5)).onUpdate(any(AirplaneModeProbe.class));

        when(mockIntent.getAction()).thenReturn(Intent.ACTION_BOOT_COMPLETED);
        cut.onReceive(mockContext, mockIntent);
        verify(mockSystemListener, times(6)).onUpdate(any(SystemUptimeProbe.class));

        when(mockIntent.getAction()).thenReturn(Intent.ACTION_SHUTDOWN);
        cut.onReceive(mockContext, mockIntent);
        verify(mockSystemListener, times(7)).onUpdate(any(SystemUptimeProbe.class));

        when(mockIntent.getAction()).thenReturn(Intent.ACTION_USER_PRESENT);
        cut.onReceive(mockContext, mockIntent);
        verify(mockSystemListener, times(8)).onUpdate(any(UserPresenceProbe.class));

        when(mockIntent.getAction()).thenReturn("Currywurst");
        cut.onReceive(mockContext, mockIntent);

    }

}