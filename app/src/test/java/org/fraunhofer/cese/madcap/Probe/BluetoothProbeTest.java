package org.fraunhofer.cese.madcap.Probe;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;

import com.google.gson.JsonObject;

import org.fraunhofer.cese.madcap.AbstractTest;
import org.fraunhofer.cese.madcap.JsonObjectFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by MMueller on 9/13/2016.
 */
public class BluetoothProbeTest extends AbstractTest{
    final BluetoothAdapter mockBluetoothAdapter = mock(BluetoothAdapter.class);

    @Override
    public final void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
    }

    @Override
    @After
    public void tearDown() {

    }

    @Test
    public final void testConstructor() throws Exception {
        Context context = RuntimeEnvironment.application.getApplicationContext();
        JsonObjectFactory jsonObjectFactory = spy(JsonObjectFactory.class);

        BluetoothProbe bluetoothProbe_no_param = new BluetoothProbe();
        Assert.assertSame("Unmatched", bluetoothProbe_no_param.getBluetoothAdapter(), BluetoothAdapter.getDefaultAdapter());

        BluetoothProbe bluetoothProbe_two_param = new BluetoothProbe(mockBluetoothAdapter, context);
        assertEquals("Unmatched", context, bluetoothProbe_two_param.getContext());
        Assert.assertSame("Unmatched", mockBluetoothAdapter, bluetoothProbe_two_param.getBluetoothAdapter());
        BroadcastReceiver receiver = new BluetoothInformationReceiver(bluetoothProbe_no_param, bluetoothProbe_no_param);

        BluetoothProbe bluetoothProbe_four_param = new BluetoothProbe(mockBluetoothAdapter, context, jsonObjectFactory, receiver);
        assertEquals("Unmatched", context, bluetoothProbe_four_param.getContext());
        assertEquals("Unmatched", mockBluetoothAdapter, bluetoothProbe_four_param.getBluetoothAdapter());
        assertEquals("Unmatched", receiver, BluetoothProbe.getReceiver());

    }

    @Test
    public final void testOnEnable() throws Exception {
        Context context = RuntimeEnvironment.application.getApplicationContext();
        JsonObjectFactory jsonObjectFactory = mock(JsonObjectFactory.class);
        when(jsonObjectFactory.createJsonObject(any(Intent.class))).thenReturn(new JsonObject());

        when(mockBluetoothAdapter.getState()).thenReturn(BluetoothAdapter.STATE_OFF);
        when(mockBluetoothAdapter.getState()).thenReturn(BluetoothAdapter.STATE_OFF);
        when(mockBluetoothAdapter.getState()).thenReturn(BluetoothAdapter.STATE_OFF);

        Set<BluetoothDevice> mockBondDevices = new HashSet<>();
        BluetoothDevice mockDevice = spy(BluetoothDevice.class);
        mockBondDevices.add(mockDevice);

        when(mockBluetoothAdapter.getBondedDevices()).thenReturn(mockBondDevices);

        BluetoothProbe bluetoothProbe = new BluetoothProbe(mockBluetoothAdapter, context, jsonObjectFactory, null);
        bluetoothProbe.onEnable();
        assertEquals("Unmatched", BluetoothProbe.OFF, bluetoothProbe.getLastSentIntent().getStringExtra("State: "));
    }

    @Test
    public final void testOnDisable() throws Exception {
        Context context = spy(RuntimeEnvironment.application.getApplicationContext());

        JsonObjectFactory jsonObjectFactory = mock(JsonObjectFactory.class);
        when(jsonObjectFactory.createJsonObject(any(Intent.class))).thenReturn(new JsonObject());

        BluetoothProbe bluetoothProbe = new BluetoothProbe(mockBluetoothAdapter, context, jsonObjectFactory, null);
        BluetoothInformationReceiver receiver = new BluetoothInformationReceiver(bluetoothProbe, bluetoothProbe);
        bluetoothProbe.setReceiver(receiver);
        context.registerReceiver(receiver, null);

        bluetoothProbe.onDisable();
        verify(context, atLeastOnce()).unregisterReceiver(receiver);
    }

    @Test
    public final void testGetConnectionStateChangedInformation() throws Exception {
        Intent intent = spy(Intent.class);
        Context context = spy(RuntimeEnvironment.application.getApplicationContext());
        JsonObjectFactory jsonObjectFactory = mock(JsonObjectFactory.class);
        when(jsonObjectFactory.createJsonObject(any(Intent.class))).thenReturn(new JsonObject());

        BluetoothProbe bluetoothProbe = new BluetoothProbe(mockBluetoothAdapter, context, jsonObjectFactory, null);

        //BluetoothAdapter mockBluetoothAdapter = mock(BluetoothAdapter.class);

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, 0)).thenReturn(BluetoothAdapter.STATE_CONNECTED);
        bluetoothProbe.getConnectionStateCInformation(intent);
        verify(intent, atLeastOnce()).putExtra("new ConnectionState: ", "connected");

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, 0)).thenReturn(BluetoothAdapter.STATE_CONNECTING);
        bluetoothProbe.getConnectionStateCInformation(intent);
        verify(intent, atLeastOnce()).putExtra("new ConnectionState: ", "connecting");

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, 0)).thenReturn(BluetoothAdapter.STATE_DISCONNECTED);
        bluetoothProbe.getConnectionStateCInformation(intent);
        verify(intent, atLeastOnce()).putExtra("new ConnectionState: ", "disconnected");

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, 0)).thenReturn(BluetoothAdapter.STATE_DISCONNECTING);
        bluetoothProbe.getConnectionStateCInformation(intent);
        verify(intent, atLeastOnce()).putExtra("new ConnectionState: ", "cacheClosing");

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, 0)).thenReturn(BluetoothAdapter.ERROR);
        bluetoothProbe.getConnectionStateCInformation(intent);
        verify(intent, atLeastOnce()).putExtra("new ConnectionState: ", BluetoothAdapter.ERROR);

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_CONNECTION_STATE, 0)).thenReturn(BluetoothAdapter.STATE_CONNECTED);
        bluetoothProbe.getConnectionStateCInformation(intent);
        verify(intent, atLeastOnce()).putExtra("previous ConnectionState: ", "connected");

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_CONNECTION_STATE, 0)).thenReturn(BluetoothAdapter.STATE_CONNECTING);
        bluetoothProbe.getConnectionStateCInformation(intent);
        verify(intent, atLeastOnce()).putExtra("previous ConnectionState: ", "connecting");

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_CONNECTION_STATE, 0)).thenReturn(BluetoothAdapter.STATE_DISCONNECTED);
        bluetoothProbe.getConnectionStateCInformation(intent);
        verify(intent, atLeastOnce()).putExtra("previous ConnectionState: ", "disconnected");

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_CONNECTION_STATE, 0)).thenReturn(BluetoothAdapter.STATE_DISCONNECTING);
        bluetoothProbe.getConnectionStateCInformation(intent);
        verify(intent, atLeastOnce()).putExtra("previous ConnectionState: ", "cacheClosing");

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_CONNECTION_STATE, 0)).thenReturn(BluetoothAdapter.ERROR);
        bluetoothProbe.getConnectionStateCInformation(intent);
        verify(intent, atLeastOnce()).putExtra("previous ConnectionState: ", BluetoothAdapter.ERROR);

    }

    @Test
    public final void testGetScanModeChangeInformation() throws Exception {
        Intent intent = spy(Intent.class);
        Context context = spy(RuntimeEnvironment.application.getApplicationContext());
        JsonObjectFactory jsonObjectFactory = mock(JsonObjectFactory.class);
        when(jsonObjectFactory.createJsonObject(any(Intent.class))).thenReturn(new JsonObject());

        BluetoothProbe bluetoothProbe = new BluetoothProbe(mockBluetoothAdapter, context, jsonObjectFactory, null);

        assertEquals("Unmatched", intent, bluetoothProbe.getScanModeChangeInformation(intent));

        verify(intent, atLeastOnce()).putExtra(BluetoothProbe.getTAG(), "ScanMode changed");

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, 0)).thenReturn(BluetoothAdapter.SCAN_MODE_NONE);
        bluetoothProbe.getScanModeChangeInformation(intent);
        verify(intent, atLeastOnce()).putExtra("new ScanMode: ", "invisible");

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, 0)).thenReturn(BluetoothAdapter.SCAN_MODE_CONNECTABLE);
        bluetoothProbe.getScanModeChangeInformation(intent);
        verify(intent, atLeastOnce()).putExtra("new ScanMode: ", "invisible, but connectable");

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, 0)).thenReturn(BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE);
        bluetoothProbe.getScanModeChangeInformation(intent);
        verify(intent, atLeastOnce()).putExtra("new ScanMode: ", "visible and connectable");

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, 0)).thenReturn(BluetoothAdapter.ERROR);
        bluetoothProbe.getScanModeChangeInformation(intent);
        verify(intent, atLeastOnce()).putExtra("new ScanMode: ", intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, 0));


        when(intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_SCAN_MODE, 0)).thenReturn(BluetoothAdapter.SCAN_MODE_NONE);
        bluetoothProbe.getScanModeChangeInformation(intent);
        verify(intent, atLeastOnce()).putExtra("previous ScanMode: ", "invisible");

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_SCAN_MODE, 0)).thenReturn(BluetoothAdapter.SCAN_MODE_CONNECTABLE);
        bluetoothProbe.getScanModeChangeInformation(intent);
        verify(intent, atLeastOnce()).putExtra("previous ScanMode: ", "invisible, but connectable");

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_SCAN_MODE, 0)).thenReturn(BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE);
        bluetoothProbe.getScanModeChangeInformation(intent);
        verify(intent, atLeastOnce()).putExtra("previous ScanMode: ", "visible and connectable");

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_SCAN_MODE, 0)).thenReturn(BluetoothAdapter.ERROR);
        bluetoothProbe.getScanModeChangeInformation(intent);
        verify(intent, atLeastOnce()).putExtra("previous ScandMode: ", intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_SCAN_MODE, 0));
    }

    @Test
    public final void testGetStateChangeInformation() throws Exception {
        Intent intent = spy(Intent.class);
        Context context = spy(RuntimeEnvironment.application.getApplicationContext());
        JsonObjectFactory jsonObjectFactory = mock(JsonObjectFactory.class);
        when(jsonObjectFactory.createJsonObject(any(Intent.class))).thenReturn(new JsonObject());

        BluetoothProbe bluetoothProbe = new BluetoothProbe(mockBluetoothAdapter, context, jsonObjectFactory, null);

        assertEquals("Unmatched", intent, bluetoothProbe.getStateChangeInformation(intent));

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)).thenReturn(BluetoothAdapter.STATE_OFF);
        bluetoothProbe.getStateChangeInformation(intent);
        verify(intent, atLeastOnce()).putExtra("new State: ", BluetoothProbe.OFF);

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)).thenReturn(BluetoothAdapter.STATE_ON);
        bluetoothProbe.getStateChangeInformation(intent);
        verify(intent, atLeastOnce()).putExtra("new State: ", BluetoothProbe.ON);

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)).thenReturn(BluetoothAdapter.STATE_TURNING_ON);
        bluetoothProbe.getStateChangeInformation(intent);
        verify(intent, atLeastOnce()).putExtra("new State: ", BluetoothProbe.TURNING_ON);

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)).thenReturn(BluetoothAdapter.STATE_TURNING_OFF);
        bluetoothProbe.getStateChangeInformation(intent);
        verify(intent, atLeastOnce()).putExtra("new State: ", BluetoothProbe.TURNING_OFF);

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)).thenReturn(BluetoothAdapter.ERROR);
        bluetoothProbe.getStateChangeInformation(intent);
        verify(intent, atLeastOnce()).putExtra("new State: ", intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0));

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, 0)).thenReturn(BluetoothAdapter.STATE_OFF);
        bluetoothProbe.getStateChangeInformation(intent);
        verify(intent, atLeastOnce()).putExtra("previous State: ", BluetoothProbe.OFF);

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, 0)).thenReturn(BluetoothAdapter.STATE_ON);
        bluetoothProbe.getStateChangeInformation(intent);
        verify(intent, atLeastOnce()).putExtra("previous State: ", BluetoothProbe.ON);

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, 0)).thenReturn(BluetoothAdapter.STATE_TURNING_OFF);
        bluetoothProbe.getStateChangeInformation(intent);
        verify(intent, atLeastOnce()).putExtra("previous State: ", BluetoothProbe.TURNING_OFF);

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, 0)).thenReturn(BluetoothAdapter.STATE_TURNING_ON);
        bluetoothProbe.getStateChangeInformation(intent);
        verify(intent, atLeastOnce()).putExtra("previous State: ", BluetoothProbe.TURNING_ON);

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, 0)).thenReturn(BluetoothAdapter.ERROR);
        bluetoothProbe.getStateChangeInformation(intent);
        verify(intent, atLeastOnce()).putExtra("previous State: ", intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, 0));
    }

    @Test
    public final void testGetBluetoothState() throws Exception {
        Intent intent = spy(Intent.class);
        Context context = spy(RuntimeEnvironment.application.getApplicationContext());
        JsonObjectFactory jsonObjectFactory = mock(JsonObjectFactory.class);
        when(jsonObjectFactory.createJsonObject(any(Intent.class))).thenReturn(new JsonObject());

        BluetoothProbe bluetoothProbe = new BluetoothProbe(mockBluetoothAdapter, context, jsonObjectFactory, null);

        when(mockBluetoothAdapter.getState()).thenReturn(BluetoothAdapter.STATE_OFF);
        Assert.assertEquals("Unmachted return values", bluetoothProbe.OFF, bluetoothProbe.getBluetoothState());

        when(mockBluetoothAdapter.getState()).thenReturn(BluetoothAdapter.STATE_ON);
        Assert.assertEquals("Unmachted return values", bluetoothProbe.ON, bluetoothProbe.getBluetoothState());

        when(mockBluetoothAdapter.getState()).thenReturn(BluetoothAdapter.STATE_TURNING_ON);
        Assert.assertEquals("Unmachted return values", bluetoothProbe.TURNING_ON, bluetoothProbe.getBluetoothState());

        when(mockBluetoothAdapter.getState()).thenReturn(BluetoothAdapter.STATE_TURNING_OFF);
        Assert.assertEquals("Unmachted return values", bluetoothProbe.TURNING_OFF, bluetoothProbe.getBluetoothState());

        when(mockBluetoothAdapter.getState()).thenReturn(BluetoothAdapter.ERROR);
        Assert.assertEquals("Unmachted return values", Integer.toString(mockBluetoothAdapter.getState()), bluetoothProbe.getBluetoothState());
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
        BluetoothProbe bluetoothProbe = new BluetoothProbe(null, mockContext, mockJsonObjectFactory, null);
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
        BluetoothProbe probe = new BluetoothProbe(mockBluetoothAdapter, mockContext, mockJsonObjectFactory, null);
        probe.onEnable();

        // bluetoothAdapter != null. Many things should be called. Check everything that is visible to us.
        verify(mockContext, times(1)).registerReceiver(any(BluetoothInformationReceiver.class), any(IntentFilter.class));
        verify(mockBluetoothAdapter, times(1)).getBondedDevices();

        // Verify that createJsonObject was called and inspect the Intent that was passed. The contents should match what we told the mockBluetoothAdapter to return.
        ArgumentCaptor<Intent> argument = ArgumentCaptor.forClass(Intent.class);
        verify(mockJsonObjectFactory).createJsonObject(argument.capture());
        assertEquals("Initial Probe!", argument.getValue().getStringExtra("BluetoothProbe: "));
        assertEquals("OFF", argument.getValue().getStringExtra("State: "));
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
        BluetoothProbe probe = new BluetoothProbe(mockBluetoothAdapter, mockContext, mockJsonObjectFactory, null);
        probe.onEnable();

        // bluetoothAdapter != null. Many things should be called. Check everything that is visible to us.
        verify(mockContext, times(1)).registerReceiver(any(BluetoothInformationReceiver.class), any(IntentFilter.class));
        verify(mockBluetoothAdapter, times(1)).getBondedDevices();

        // Verify that createJsonObject was called and inspect the Intent that was passed. The contents should match what we told the mockBluetoothAdapter to return.
        ArgumentCaptor<Intent> argument = ArgumentCaptor.forClass(Intent.class);
        verify(mockJsonObjectFactory).createJsonObject(argument.capture());
        assertEquals("Initial Probe!", argument.getValue().getStringExtra("BluetoothProbe: "));
        assertEquals("OFF", argument.getValue().getStringExtra("State: "));
        assertEquals("127.0.0.1", argument.getValue().getStringExtra("Address: "));
        assertEquals("this is my mock adapter!", argument.getValue().getStringExtra("Name: "));
        assertEquals("[speaker, Ear piece]", argument.getValue().getStringExtra("Bonded devices: ")); // this is what an empty list.toString looks like
    }

}