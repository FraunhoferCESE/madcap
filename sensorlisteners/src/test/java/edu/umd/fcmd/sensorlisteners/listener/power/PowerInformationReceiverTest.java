package edu.umd.fcmd.sensorlisteners.listener.power;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by MMueller on 12/12/2016.
 */
public class PowerInformationReceiverTest {
    PowerListener mockPowerListener;
    int mockInitialLevel;

    @Before
    public void setUp() throws Exception {
        mockPowerListener = mock(PowerListener.class);

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void onReceive() throws Exception {
        PowerInformationReceiver cut = new PowerInformationReceiver(mockPowerListener, mockInitialLevel);

        Intent mockIntent = mock(Intent.class);
        Context mockContext = mock(Context.class);

        when(mockIntent.getAction()).thenReturn(Intent.ACTION_POWER_CONNECTED);

        when(mockContext.registerReceiver(any(BroadcastReceiver.class), any(IntentFilter.class))).thenReturn(null);
        cut.onReceive(mockContext, mockIntent);

        Intent mockChargingIntent = mock(Intent.class);

        when(mockContext.registerReceiver(any(BroadcastReceiver.class), any(IntentFilter.class))).thenReturn(mockChargingIntent);
        when(mockChargingIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)).thenReturn(BatteryManager.BATTERY_PLUGGED_AC);
        cut.onReceive(mockContext, mockIntent);

        when(mockChargingIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)).thenReturn(BatteryManager.BATTERY_PLUGGED_USB);
        cut.onReceive(mockContext, mockIntent);

        when(mockChargingIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)).thenReturn(BatteryManager.BATTERY_PLUGGED_WIRELESS);
        cut.onReceive(mockContext, mockIntent);

        when(mockChargingIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)).thenReturn(26568);
        cut.onReceive(mockContext, mockIntent);

        when(mockIntent.getAction()).thenReturn(Intent.ACTION_POWER_DISCONNECTED);
        cut.onReceive(mockContext, mockIntent);

        when(mockIntent.getAction()).thenReturn(Intent.ACTION_BATTERY_CHANGED);
        when(mockIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)).thenReturn(100);
        cut.onReceive(mockContext, mockIntent);

        when(mockIntent.getAction()).thenReturn("Karpfen");
        cut.onReceive(mockContext, mockIntent);
    }

}