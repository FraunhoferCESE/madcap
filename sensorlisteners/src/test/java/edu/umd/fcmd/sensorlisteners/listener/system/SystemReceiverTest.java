package edu.umd.fcmd.sensorlisteners.listener.system;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.TimeZone;

import edu.umd.fcmd.sensorlisteners.model.system.AirplaneModeProbe;
import edu.umd.fcmd.sensorlisteners.model.system.DockStateProbe;
import edu.umd.fcmd.sensorlisteners.model.system.DreamingModeProbe;
import edu.umd.fcmd.sensorlisteners.model.system.InputMethodProbe;
import edu.umd.fcmd.sensorlisteners.model.system.ScreenProbe;
import edu.umd.fcmd.sensorlisteners.model.system.SystemUptimeProbe;
import edu.umd.fcmd.sensorlisteners.model.system.TimeChangedProbe;
import edu.umd.fcmd.sensorlisteners.model.system.TimezoneProbe;
import edu.umd.fcmd.sensorlisteners.model.system.UserPresenceProbe;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
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
    SharedPreferences mockPrefs;

    @Before
    public void setUp() throws Exception {
        mockSystemListener = mock(SystemListener.class);
        mockContext = mock(Context.class);
        mockPrefs = mock(SharedPreferences.class);
    }

    @Test
    public void constructorTest() throws Exception {
        when(mockPrefs.getString(anyString(), anyString())).thenReturn(TimeZone.getDefault().getID());

        SystemReceiver cut = new SystemReceiver(mockSystemListener, mockContext, mockPrefs);
    }

    @Test
    public void onReceive() throws Exception {
        when(mockPrefs.getString(anyString(), anyString())).thenReturn(TimeZone.getDefault().getID());

        SystemReceiver cut = new SystemReceiver(mockSystemListener, mockContext, mockPrefs);

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

        when(mockIntent.getAction()).thenReturn(Intent.ACTION_INPUT_METHOD_CHANGED);
        cut.onReceive(mockContext, mockIntent);
        verify(mockSystemListener, times(9)).onUpdate(any(InputMethodProbe.class));

        when(mockIntent.getAction()).thenReturn(Intent.ACTION_TIMEZONE_CHANGED);

        SharedPreferences.Editor mockEdit = mock(SharedPreferences.Editor.class);
        SharedPreferences.Editor mockEdit2 = mock(SharedPreferences.Editor.class);
        when(mockPrefs.edit()).thenReturn(mockEdit);
        when(mockEdit.putString(anyString(),anyString())).thenReturn(mockEdit2);
        when(mockEdit2.commit()).thenReturn(true);

        cut.onReceive(mockContext, mockIntent);
        verify(mockSystemListener, times(9)).onUpdate(any(TimezoneProbe.class));

        when(mockIntent.getAction()).thenReturn(Intent.ACTION_DOCK_EVENT);
        cut.onReceive(mockContext, mockIntent);
        verify(mockSystemListener, times(10)).onUpdate(any(DockStateProbe.class));

        when(mockIntent.getAction()).thenReturn(Intent.ACTION_TIME_CHANGED);
        cut.onReceive(mockContext, mockIntent);
        verify(mockSystemListener, times(11)).onUpdate(any(TimeChangedProbe.class));

        when(mockIntent.getAction()).thenReturn("Currywurst");
        cut.onReceive(mockContext, mockIntent);

    }

}