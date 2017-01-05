package edu.umd.fcmd.sensorlisteners.listener.system;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Looper;
import android.os.PowerManager;
import android.view.Display;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;

import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.model.system.DockStateProbe;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;

import static android.content.Context.POWER_SERVICE;
import static android.content.Intent.EXTRA_DOCK_STATE;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by MMueller on 1/4/2017.
 */
public class SystemListenerTest {
    Context mockContext;
    ProbeManager<Probe> mockProbeManager;
    SystemReceiverFactory mockSystemReceiverFactory;
    String mockVersion = "TEST";

    @Before
    public void setUp() throws Exception {
        mockContext = mock(Context.class);
        mockProbeManager = mock(ProbeManager.class);
        mockSystemReceiverFactory = mock(SystemReceiverFactory.class);
    }

    @Test
    public void constructorTest() throws Exception {
        SystemListener cut = new SystemListener(mockContext, mockProbeManager, mockSystemReceiverFactory, mockVersion);

    }

    @Test
    public void onUpdate() throws Exception {
        SystemListener cut = new SystemListener(mockContext, mockProbeManager, mockSystemReceiverFactory, mockVersion);

        Probe mockProbe = mock(Probe.class);
        cut.onUpdate(mockProbe);

        verify(mockProbeManager).save(mockProbe);
    }

    @Test
    public void startListening() throws Exception {
        setFinalStatic(Build.VERSION.class.getField("SDK_INT"), 21);
        SystemListener cut = new SystemListener(mockContext, mockProbeManager, mockSystemReceiverFactory, mockVersion);

        DisplayManager mockDisplayManager = mock(DisplayManager.class);
        when(mockContext.getSystemService(Context.DISPLAY_SERVICE)).thenReturn(mockDisplayManager);

        Display mockDisplay = mock(Display.class);
        when(mockDisplay.getState()).thenReturn(1111);

        Display[] mockDisplayArray = new Display[0];
        when(mockDisplayManager.getDisplays()).thenReturn(mockDisplayArray);
        when(mockContext.getMainLooper()).thenReturn(mock(Looper.class));
        when(mockContext.getApplicationContext()).thenReturn(mockContext);

        InputMethodManager mockInputMethodManager = mock(InputMethodManager.class);
        when(mockContext.getSystemService(Context.INPUT_METHOD_SERVICE)).thenReturn(mockInputMethodManager);

        List<InputMethodInfo> mockInputMethodList = mock(List.class);
        when(mockInputMethodManager.getEnabledInputMethodList()).thenReturn(mockInputMethodList);

        cut.startListening();

        when(mockInputMethodList.size()).thenReturn(1);

        cut.startListening();

        PowerManager mockPowerManager = mock(PowerManager.class);

        when(mockContext.getSystemService(POWER_SERVICE)).thenReturn(mockPowerManager);

        cut.startListening();

        Display[] mockDisplayArray2 = new Display[1];
        mockDisplayArray2[0] = mockDisplay;
        when(mockDisplayManager.getDisplays()).thenReturn(mockDisplayArray2);

        Iterator<Display> mockIterator = mock(Iterator.class);

        when(mockIterator.hasNext()).thenReturn(true, false);
        when(mockIterator.next()).thenReturn(mockDisplay);

        cut.startListening();

        Assert.assertTrue(cut.isRunning());

    }

    @Test
    public void stopListening() throws Exception {
        SystemListener cut = new SystemListener(mockContext, mockProbeManager, mockSystemReceiverFactory, mockVersion);

        DisplayManager mockDisplayManager = mock(DisplayManager.class);
        when(mockContext.getSystemService(Context.DISPLAY_SERVICE)).thenReturn(mockDisplayManager);

        Display mockDisplay = mock(Display.class);
        when(mockDisplay.getState()).thenReturn(1111);

        Display[] mockDisplayArray = new Display[0];
        when(mockDisplayManager.getDisplays()).thenReturn(mockDisplayArray);
        when(mockContext.getMainLooper()).thenReturn(mock(Looper.class));
        when(mockContext.getApplicationContext()).thenReturn(mockContext);

        InputMethodManager mockInputMethodManager = mock(InputMethodManager.class);
        when(mockContext.getSystemService(Context.INPUT_METHOD_SERVICE)).thenReturn(mockInputMethodManager);

        List<InputMethodInfo> mockInputMethodList = mock(List.class);
        when(mockInputMethodManager.getEnabledInputMethodList()).thenReturn(mockInputMethodList);

        PowerManager mockPowerManager = mock(PowerManager.class);

        when(mockContext.getSystemService(POWER_SERVICE)).thenReturn(mockPowerManager);
        when(mockPowerManager.isScreenOn()).thenReturn(true);

        cut.startListening();
        cut.stopListening();

        Assert.assertFalse(cut.isRunning());

    }

    @Test
    public void getCurrentAirplaneModeState() throws Exception {
        SystemListener cut = new SystemListener(mockContext, mockProbeManager, mockSystemReceiverFactory, mockVersion);

        setFinalStatic(Build.VERSION.class.getField("SDK_INT"), 21);

        cut.getCurrentAirplaneModeState();

    }

    @Test
    public void getCurrentAirplaneModeState2() throws Exception {
        SystemListener cut = new SystemListener(mockContext, mockProbeManager, mockSystemReceiverFactory, mockVersion);

        setFinalStatic(Build.VERSION.class.getField("SDK_INT"), 16);

        cut.getCurrentAirplaneModeState();
    }

    @Test
    public void getCurrentDockDevice() throws Exception {
        SystemListener cut = new SystemListener(mockContext, mockProbeManager, mockSystemReceiverFactory, mockVersion);

        Intent mockIntent = mock(Intent.class);

        when(mockContext.registerReceiver(any(BroadcastReceiver.class), any(IntentFilter.class))).thenReturn(null);

        Assert.assertEquals("-", cut.getCurrentDockDevice());

        when(mockContext.registerReceiver(any(BroadcastReceiver.class), any(IntentFilter.class))).thenReturn(mockIntent);

        when(mockIntent.getIntExtra(EXTRA_DOCK_STATE, -1)).thenReturn(Intent.EXTRA_DOCK_STATE_CAR);
        Assert.assertEquals(DockStateProbe.CAR, cut.getCurrentDockDevice());

        when(mockIntent.getIntExtra(EXTRA_DOCK_STATE, -1)).thenReturn(Intent.EXTRA_DOCK_STATE_DESK);
        Assert.assertEquals(DockStateProbe.DESK, cut.getCurrentDockDevice());

        when(mockIntent.getIntExtra(EXTRA_DOCK_STATE, -1)).thenReturn(Intent.EXTRA_DOCK_STATE_HE_DESK);
        Assert.assertEquals(DockStateProbe.DESK, cut.getCurrentDockDevice());

        when(mockIntent.getIntExtra(EXTRA_DOCK_STATE, -1)).thenReturn(Intent.EXTRA_DOCK_STATE_LE_DESK);
        Assert.assertEquals(DockStateProbe.DESK, cut.getCurrentDockDevice());

    }

    static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);
    }

}