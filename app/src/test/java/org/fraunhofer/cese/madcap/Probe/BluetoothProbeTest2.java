package org.fraunhofer.cese.madcap.Probe;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.google.gson.JsonObject;

import org.fraunhofer.cese.madcap.AbstractTest;
import org.fraunhofer.cese.madcap.JsonObjectFactory;
import org.junit.After;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by llayman on 9/15/2016.
 */
public class BluetoothProbeTest2 extends AbstractTest {

    @Override
    public final void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
    }

    @Override
    @After
    public void tearDown() {

    }

    /**
     * Test the case where there is no bluetooth adapter during onEnable
     */
    @Test
    public void testOnEnableNoAdapter() {
        Context mockContext = mock(Context.class);
        JsonObjectFactory mockJsonObjectFactory = mock(JsonObjectFactory.class);
        when(mockJsonObjectFactory.createJsonObject(any(Intent.class))).thenReturn(new JsonObject());

        // Run the CUT code without passing in a BluetoothAdapter
        BluetoothProbe2 bluetoothProbe = new BluetoothProbe2(null, mockContext, mockJsonObjectFactory);
        bluetoothProbe.onEnable();

        // bluetoothAdapter == null. No data should be sent to the jsonObjectFactory.
        verify(mockJsonObjectFactory, never()).createJsonObject(any(Intent.class));

    }

    /**
     * Test the case where there are no bonded devices in onEnable
     */
    @Test
    public void testOnEnableAdapterNoDevices() {

        Context mockContext = mock(Context.class);
        JsonObjectFactory mockJsonObjectFactory = mock(JsonObjectFactory.class);
        when(mockJsonObjectFactory.createJsonObject(any(Intent.class))).thenReturn(new JsonObject());


        // Set up how our BluetoothAdapter will respnd to methods called during onEnable()...
        BluetoothAdapter mockBluetoothAdapter = mock(BluetoothAdapter.class);
        when(mockBluetoothAdapter.getState()).thenReturn(BluetoothAdapter.STATE_OFF);
        when(mockBluetoothAdapter.getAddress()).thenReturn("127.0.0.1");
        when(mockBluetoothAdapter.getName()).thenReturn("this is my mock adapter!");
        when(mockBluetoothAdapter.getBondedDevices()).thenReturn(new HashSet<BluetoothDevice>());

        // Run the CUT code.
        BluetoothProbe2 probe = new BluetoothProbe2(mockBluetoothAdapter, mockContext, mockJsonObjectFactory);
        probe.onEnable();

        // bluetoothAdapter != null. Many things should be called. Check everything that is visible to us.
        verify(mockContext, times(1)).registerReceiver(any(BluetoothInformationReceiver.class), any(IntentFilter.class));
        verify(mockBluetoothAdapter, times(1)).getBondedDevices();

        // Verify that createJsonObject was called and inspect the Intent that was passed. The contents should match what we told the mockBluetoothAdapter to return.
        ArgumentCaptor<Intent> argument = ArgumentCaptor.forClass(Intent.class);
        verify(mockJsonObjectFactory).createJsonObject(argument.capture());
        assertEquals("Initial Probe!", argument.getValue().getStringExtra("BluetoothProbe: "));
        assertEquals("off.", argument.getValue().getStringExtra("State: "));
        assertEquals("127.0.0.1", argument.getValue().getStringExtra("Address: "));
        assertEquals("this is my mock adapter!", argument.getValue().getStringExtra("Name: "));
        assertEquals("[]", argument.getValue().getStringExtra("Bonded devices: ")); // this is what an empty list.toString looks like
    }

    /**
     * Test the case where there are some devices in onEnable
     */
    @Test
    public void testOnEnableAdapterSomeDevices() {

        Context mockContext = mock(Context.class);
        JsonObjectFactory mockJsonObjectFactory = mock(JsonObjectFactory.class);
        when(mockJsonObjectFactory.createJsonObject(any(Intent.class))).thenReturn(new JsonObject());

        // Make some mock BluetoothDevices to return
        BluetoothDevice mockBluetoothDevice1 = mock(BluetoothDevice.class);
        when(mockBluetoothDevice1.getName()).thenReturn("Ear piece");
        BluetoothDevice mockBluetoothDevice2 = mock(BluetoothDevice.class);
        when(mockBluetoothDevice2.getName()).thenReturn("speaker");
        Set<BluetoothDevice> bluetoothDevices = new HashSet<>();
        bluetoothDevices.add(mockBluetoothDevice1);
        bluetoothDevices.add(mockBluetoothDevice2);

        // Set up how our BluetoothAdapter will respnd to methods called during onEnable()...
        BluetoothAdapter mockBluetoothAdapter = mock(BluetoothAdapter.class);
        when(mockBluetoothAdapter.getState()).thenReturn(BluetoothAdapter.STATE_OFF);
        when(mockBluetoothAdapter.getAddress()).thenReturn("127.0.0.1");
        when(mockBluetoothAdapter.getName()).thenReturn("this is my mock adapter!");
        when(mockBluetoothAdapter.getBondedDevices()).thenReturn(bluetoothDevices);

        // Run the CUT code.
        BluetoothProbe2 probe = new BluetoothProbe2(mockBluetoothAdapter, mockContext, mockJsonObjectFactory);
        probe.onEnable();

        // bluetoothAdapter != null. Many things should be called. Check everything that is visible to us.
        verify(mockContext, times(1)).registerReceiver(any(BluetoothInformationReceiver.class), any(IntentFilter.class));
        verify(mockBluetoothAdapter, times(1)).getBondedDevices();

        // Verify that createJsonObject was called and inspect the Intent that was passed. The contents should match what we told the mockBluetoothAdapter to return.
        ArgumentCaptor<Intent> argument = ArgumentCaptor.forClass(Intent.class);
        verify(mockJsonObjectFactory).createJsonObject(argument.capture());
        assertEquals("Initial Probe!", argument.getValue().getStringExtra("BluetoothProbe: "));
        assertEquals("off.", argument.getValue().getStringExtra("State: "));
        assertEquals("127.0.0.1", argument.getValue().getStringExtra("Address: "));
        assertEquals("this is my mock adapter!", argument.getValue().getStringExtra("Name: "));
        assertEquals("[Ear piece, speaker]", argument.getValue().getStringExtra("Bonded devices: ")); // this is what an empty list.toString looks like
    }
}
