package edu.umd.fcmd.sensorlisteners.listener.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.umd.fcmd.sensorlisteners.model.BluetoothConnectionProbe;
import edu.umd.fcmd.sensorlisteners.model.Probe;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by MMueller on 12/8/2016.
 */
public class BluetoothInformationReceiverTest {
    BluetoothListener mockBluetoothListener;

    @Before
    public void setUp() throws Exception {
        mockBluetoothListener = mock(BluetoothListener.class);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void constructorTest() throws Exception {
        BluetoothInformationReceiver cut = new BluetoothInformationReceiver(mockBluetoothListener);
    }

    @Test
    public void onReceive() throws Exception {
        BluetoothInformationReceiver cut = new BluetoothInformationReceiver(mockBluetoothListener);

        Context mockContext = mock(Context.class);
        Intent mockIntent = mock(Intent.class);

        BluetoothAdapter mockBluetoothAdapter = mock(BluetoothAdapter.class);
        when(mockBluetoothListener.getBluetoothAdapter()).thenReturn(mockBluetoothAdapter);

        when(mockIntent.getAction()).thenReturn(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        Bundle mockBundle = mock(Bundle.class);
        when(mockIntent.getExtras()).thenReturn(mockBundle);
        BluetoothDevice mockBluetoothDevice = mock(BluetoothDevice.class);
        when(mockBundle.getParcelable("android.bluetooth.BluetoothDevice")).thenReturn(mockBluetoothDevice);
        cut.onReceive(mockContext, mockIntent);

        when(mockIntent.getAction()).thenReturn(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        cut.onReceive(mockContext, mockIntent);

        when(mockIntent.getAction()).thenReturn(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        cut.onReceive(mockContext, mockIntent);

        when(mockIntent.getAction()).thenReturn(BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED);
        cut.onReceive(mockContext, mockIntent);

        when(mockIntent.getAction()).thenReturn(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        cut.onReceive(mockContext, mockIntent);

        when(mockIntent.getAction()).thenReturn(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        cut.onReceive(mockContext, mockIntent);

        when(mockIntent.getAction()).thenReturn(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        when(mockIntent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, 0)).thenReturn(BluetoothAdapter.SCAN_MODE_NONE);
        cut.onReceive(mockContext, mockIntent);
        when(mockIntent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, 0)).thenReturn(BluetoothAdapter.SCAN_MODE_CONNECTABLE);
        cut.onReceive(mockContext, mockIntent);
        when(mockIntent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, 0)).thenReturn(BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE);
        cut.onReceive(mockContext, mockIntent);
        when(mockIntent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, 0)).thenReturn(2222);
        cut.onReceive(mockContext, mockIntent);

        when(mockIntent.getAction()).thenReturn(BluetoothAdapter.ACTION_STATE_CHANGED);
        cut.onReceive(mockContext, mockIntent);

        when(mockIntent.getAction()).thenReturn("aasodhskjfhs");
        cut.onReceive(mockContext, mockIntent);
    }

}