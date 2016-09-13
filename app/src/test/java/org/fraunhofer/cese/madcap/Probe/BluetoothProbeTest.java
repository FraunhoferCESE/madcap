package org.fraunhofer.cese.madcap.Probe;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.fraunhofer.cese.madcap.AbstractTest;
import org.fraunhofer.cese.madcap.JsonObjectFactory;
import org.junit.After;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;

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
    public void testOnEnable() throws Exception {
//        Context context = mock(Context.class);
        Context context = RuntimeEnvironment.application.getApplicationContext();


        JsonObjectFactory jsonObjectFactory = mock(JsonObjectFactory.class);
        when(jsonObjectFactory.createJsonObject(any(Intent.class))).thenReturn(new JsonObject());

        when(mockBluetoothAdapter.getState()).thenReturn(BluetoothAdapter.STATE_OFF);
        when(mockBluetoothAdapter.getState()).thenReturn(BluetoothAdapter.STATE_OFF);
        when(mockBluetoothAdapter.getState()).thenReturn(BluetoothAdapter.STATE_OFF);

        BluetoothProbe bluetoothProbe = new BluetoothProbe(mockBluetoothAdapter, context, jsonObjectFactory);
        bluetoothProbe.onEnable();
//        assertEquals(bluetoothProbe.getLastSentIntent().getStringExtra("State: "),BluetoothProbe.OFF );
    }

    @Test
    public void testOnDisable() throws Exception {

    }
}