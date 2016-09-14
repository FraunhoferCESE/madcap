package org.fraunhofer.cese.madcap.Probe;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.fraunhofer.cese.madcap.AbstractTest;
import org.fraunhofer.cese.madcap.JsonObjectFactory;
import org.junit.After;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.Stubber;
import org.robolectric.RuntimeEnvironment;

import mockit.Mock;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by MMueller on 9/13/2016.
 */
public class BluetoothProbeTest extends AbstractTest{
    final BluetoothAdapter mockBluetoothAdapter = mock(BluetoothAdapter.class);

    @Override
    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testConstructor() throws Exception {
        Context context = RuntimeEnvironment.application.getApplicationContext();
        JsonObjectFactory jsonObjectFactory = spy(JsonObjectFactory.class);

        BluetoothProbe bluetoothProbe_no_param = new BluetoothProbe();
        assertEquals(bluetoothProbe_no_param.getBluetoothAdapter(), BluetoothAdapter.getDefaultAdapter());

        BluetoothProbe bluetoothProbe_two_param = new BluetoothProbe(mockBluetoothAdapter, context);
        assertEquals(context, bluetoothProbe_two_param.getContext());
        assertEquals(mockBluetoothAdapter, bluetoothProbe_two_param.getBluetoothAdapter());
        BroadcastReceiver receiver = new BluetoothInformationReceiver(bluetoothProbe_no_param, bluetoothProbe_no_param);

        BluetoothProbe bluetoothProbe_four_param = new BluetoothProbe(mockBluetoothAdapter, context, jsonObjectFactory, receiver);
        assertEquals(context, bluetoothProbe_two_param.getContext());
        assertEquals(mockBluetoothAdapter, bluetoothProbe_two_param.getBluetoothAdapter());
        //assertEquals(jsonObjectFactory, bluetoothProbe_two_param.getJsonObjectFactory());
        //assertEquals(receiver, bluetoothProbe_four_param.getReceiver());

    }

    @Test
    public void testOnEnable() throws Exception {
//        Context context = mock(Context.class);
        Context context = RuntimeEnvironment.application.getApplicationContext();
        JsonObjectFactory jsonObjectFactory = mock(JsonObjectFactory.class);
        when(jsonObjectFactory.createJsonObject(any(Intent.class))).thenReturn(new JsonObject());

        when(mockBluetoothAdapter.getState()).thenReturn(BluetoothAdapter.STATE_OFF);
        when(mockBluetoothAdapter.getState()).thenReturn(BluetoothAdapter.STATE_OFF);
        when(mockBluetoothAdapter.getState()).thenReturn(BluetoothAdapter.STATE_OFF);

        BluetoothProbe bluetoothProbe = new BluetoothProbe(mockBluetoothAdapter, context, jsonObjectFactory, null);
        bluetoothProbe.onEnable();
        assertEquals(bluetoothProbe.getLastSentIntent().getStringExtra("State: "), BluetoothProbe.OFF );
    }

    @Test
    public void testOnDisable() throws Exception {
        Context context = spy(RuntimeEnvironment.application.getApplicationContext());

        JsonObjectFactory jsonObjectFactory = mock(JsonObjectFactory.class);
        when(jsonObjectFactory.createJsonObject(any(Intent.class))).thenReturn(new JsonObject());

        BluetoothInformationReceiver receiver;
        BluetoothProbe bluetoothProbe = new BluetoothProbe(mockBluetoothAdapter, context, jsonObjectFactory, null);
        receiver = new BluetoothInformationReceiver(bluetoothProbe, bluetoothProbe);
        bluetoothProbe.setReceiver(receiver);
        context.registerReceiver(receiver, null);

        bluetoothProbe.onDisable();
        verify(context, atLeastOnce()).unregisterReceiver(receiver);
    }

    @Test
    public void testGetConnectionStateChangedInformation() throws Exception {
        Intent intent = spy(Intent.class);
        Context context = spy(RuntimeEnvironment.application.getApplicationContext());
        JsonObjectFactory jsonObjectFactory = mock(JsonObjectFactory.class);
        when(jsonObjectFactory.createJsonObject(any(Intent.class))).thenReturn(new JsonObject());

        BluetoothProbe bluetoothProbe = new BluetoothProbe(mockBluetoothAdapter, context, jsonObjectFactory, null);

        BluetoothAdapter mockBluetoothAdapter = mock(BluetoothAdapter.class);

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, 0)).thenReturn(BluetoothAdapter.STATE_CONNECTED);
        bluetoothProbe.getConnectionStateChangedInformation(intent);
        verify(intent, atLeastOnce()).putExtra("new ConnectionState: ", "connected");

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, 0)).thenReturn(BluetoothAdapter.STATE_CONNECTING);
        bluetoothProbe.getConnectionStateChangedInformation(intent);
        verify(intent, atLeastOnce()).putExtra("new ConnectionState: ", "connecting");

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, 0)).thenReturn(BluetoothAdapter.STATE_DISCONNECTED);
        bluetoothProbe.getConnectionStateChangedInformation(intent);
        verify(intent, atLeastOnce()).putExtra("new ConnectionState: ", "disconnected");

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, 0)).thenReturn(BluetoothAdapter.STATE_DISCONNECTING);
        bluetoothProbe.getConnectionStateChangedInformation(intent);
        verify(intent, atLeastOnce()).putExtra("new ConnectionState: ", "cacheClosing");

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_CONNECTION_STATE, 0)).thenReturn(BluetoothAdapter.ERROR);
        bluetoothProbe.getConnectionStateChangedInformation(intent);
        verify(intent, atLeastOnce()).putExtra("new ConnectionState: ", BluetoothAdapter.ERROR);

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_CONNECTION_STATE, 0)).thenReturn(BluetoothAdapter.STATE_CONNECTED);
        bluetoothProbe.getConnectionStateChangedInformation(intent);
        verify(intent, atLeastOnce()).putExtra("previous ConnectionState: ", "connected");

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_CONNECTION_STATE, 0)).thenReturn(BluetoothAdapter.STATE_CONNECTING);
        bluetoothProbe.getConnectionStateChangedInformation(intent);
        verify(intent, atLeastOnce()).putExtra("previous ConnectionState: ", "connecting");

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_CONNECTION_STATE, 0)).thenReturn(BluetoothAdapter.STATE_DISCONNECTED);
        bluetoothProbe.getConnectionStateChangedInformation(intent);
        verify(intent, atLeastOnce()).putExtra("previous ConnectionState: ", "disconnected");

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_CONNECTION_STATE, 0)).thenReturn(BluetoothAdapter.STATE_DISCONNECTING);
        bluetoothProbe.getConnectionStateChangedInformation(intent);
        verify(intent, atLeastOnce()).putExtra("previous ConnectionState: ", "cacheClosing");

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_CONNECTION_STATE, 0)).thenReturn(BluetoothAdapter.ERROR);
        bluetoothProbe.getConnectionStateChangedInformation(intent);
        verify(intent, atLeastOnce()).putExtra("previous ConnectionState: ", BluetoothAdapter.ERROR);

    }

    @Test
    public void testGetScanModeChangeInformation() throws Exception {
        Intent intent = spy(Intent.class);
        Context context = spy(RuntimeEnvironment.application.getApplicationContext());
        JsonObjectFactory jsonObjectFactory = mock(JsonObjectFactory.class);
        when(jsonObjectFactory.createJsonObject(any(Intent.class))).thenReturn(new JsonObject());

        BluetoothProbe bluetoothProbe = new BluetoothProbe(mockBluetoothAdapter, context, jsonObjectFactory, null);

        BluetoothAdapter mockBluetoothAdapter = mock(BluetoothAdapter.class);

        assertEquals(intent, bluetoothProbe.getScanModeChangeInformation(intent));

        verify(intent, atLeastOnce()).putExtra(bluetoothProbe.getTAG(), "ScanMode changed");

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
    public void getStateChangeInformation() throws Exception {
        Intent intent = spy(Intent.class);
        Context context = spy(RuntimeEnvironment.application.getApplicationContext());
        JsonObjectFactory jsonObjectFactory = mock(JsonObjectFactory.class);
        when(jsonObjectFactory.createJsonObject(any(Intent.class))).thenReturn(new JsonObject());

        BluetoothProbe bluetoothProbe = new BluetoothProbe(mockBluetoothAdapter, context, jsonObjectFactory, null);

        BluetoothAdapter mockBluetoothAdapter = mock(BluetoothAdapter.class);

        assertEquals(intent, bluetoothProbe.getStateChangeInformation(intent));

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)).thenReturn(BluetoothAdapter.STATE_OFF);
        bluetoothProbe.getStateChangeInformation(intent);
        verify(intent, atLeastOnce()).putExtra("new State: ", bluetoothProbe.OFF);

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)).thenReturn(BluetoothAdapter.STATE_ON);
        bluetoothProbe.getStateChangeInformation(intent);
        verify(intent, atLeastOnce()).putExtra("new State: ", bluetoothProbe.ON);

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)).thenReturn(BluetoothAdapter.STATE_TURNING_ON);
        bluetoothProbe.getStateChangeInformation(intent);
        verify(intent, atLeastOnce()).putExtra("new State: ", bluetoothProbe.TURNING_ON);

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)).thenReturn(BluetoothAdapter.STATE_TURNING_OFF);
        bluetoothProbe.getStateChangeInformation(intent);
        verify(intent, atLeastOnce()).putExtra("new State: ", bluetoothProbe.TURNING_OFF);

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)).thenReturn(BluetoothAdapter.ERROR);
        bluetoothProbe.getStateChangeInformation(intent);
        verify(intent, atLeastOnce()).putExtra("new State: ", intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0));

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, 0)).thenReturn(BluetoothAdapter.STATE_OFF);
        bluetoothProbe.getStateChangeInformation(intent);
        verify(intent, atLeastOnce()).putExtra("previous State: ", bluetoothProbe.OFF);

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, 0)).thenReturn(BluetoothAdapter.STATE_ON);
        bluetoothProbe.getStateChangeInformation(intent);
        verify(intent, atLeastOnce()).putExtra("previous State: ", bluetoothProbe.ON);

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, 0)).thenReturn(BluetoothAdapter.STATE_TURNING_OFF);
        bluetoothProbe.getStateChangeInformation(intent);
        verify(intent, atLeastOnce()).putExtra("previous State: ", bluetoothProbe.TURNING_OFF);

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, 0)).thenReturn(BluetoothAdapter.STATE_TURNING_ON);
        bluetoothProbe.getStateChangeInformation(intent);
        verify(intent, atLeastOnce()).putExtra("previous State: ", bluetoothProbe.TURNING_ON);

        when(intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, 0)).thenReturn(BluetoothAdapter.ERROR);
        bluetoothProbe.getStateChangeInformation(intent);
        verify(intent, atLeastOnce()).putExtra("previous State: ", intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, 0));


    }
}