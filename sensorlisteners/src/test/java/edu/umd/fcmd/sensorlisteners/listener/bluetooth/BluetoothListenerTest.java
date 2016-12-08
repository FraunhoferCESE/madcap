package edu.umd.fcmd.sensorlisteners.listener.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Process;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;
import java.util.Set;

import edu.umd.fcmd.sensorlisteners.issuehandling.PermissionDeniedHandler;
import edu.umd.fcmd.sensorlisteners.listener.IntentFilterFactory;
import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;

import static android.R.attr.permission;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by MMueller on 12/7/2016.
 */
public class BluetoothListenerTest {
    Context mockContext;
    ProbeManager<Probe> mockProbeManager;
    BluetoothAdapter mockBluetoothAdapter;
    PermissionDeniedHandler mockPermissionDeniedHandler;
    BluetoothInformationReceiverFactory mockBluetoothInformationReceiverFactory;
    IntentFilterFactory mockIntenFilterFactory;

    @Before
    public void setUp() throws Exception {
        mockContext = mock(Context.class);
        mockProbeManager = (ProbeManager<Probe>) mock(ProbeManager.class);
        mockBluetoothAdapter = mock(BluetoothAdapter.class);
        mockPermissionDeniedHandler = mock(PermissionDeniedHandler.class);
        mockBluetoothInformationReceiverFactory = mock(BluetoothInformationReceiverFactory.class);
        mockIntenFilterFactory = mock(IntentFilterFactory.class);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void constructorTest() throws Exception {
        BluetoothListener cut = new BluetoothListener(mockContext,
                mockProbeManager,
                mockBluetoothAdapter,
                mockPermissionDeniedHandler,
                mockBluetoothInformationReceiverFactory,
                mockIntenFilterFactory);
    }

    @Test
    public void onUpdate() throws Exception {
        BluetoothListener cut = new BluetoothListener(mockContext,
                mockProbeManager,
                mockBluetoothAdapter,
                mockPermissionDeniedHandler,
                mockBluetoothInformationReceiverFactory,
                mockIntenFilterFactory);

        Probe mockProbe = mock(Probe.class);

        cut.onUpdate(mockProbe);
        verify(mockProbeManager).save(mockProbe);
    }

    @Test
    public void startListening() throws Exception {
        BluetoothListener cut = new BluetoothListener(mockContext,
                mockProbeManager,
                mockBluetoothAdapter,
                mockPermissionDeniedHandler,
                mockBluetoothInformationReceiverFactory,
                mockIntenFilterFactory);

        IntentFilter mockIntentFilter = mock(IntentFilter.class);
        when(mockIntenFilterFactory.create()).thenReturn(mockIntentFilter);

        //Testing for create inital probes
        BluetoothDevice mockBluetoothDevice1 = mock(BluetoothDevice.class);
        BluetoothDevice mockBluetoothDevice2 = mock(BluetoothDevice.class);
        BluetoothDevice mockBluetoothDevice3 = mock(BluetoothDevice.class);

        Set<BluetoothDevice> mockSet = (Set<BluetoothDevice>) mock(Set.class);
        when(mockBluetoothAdapter.getBondedDevices()).thenReturn(mockSet);

        when(mockBluetoothDevice1.getBondState()).thenReturn(BluetoothDevice.BOND_BONDING);
        when(mockBluetoothDevice2.getBondState()).thenReturn(BluetoothDevice.BOND_BONDED);
        when(mockBluetoothDevice3.getBondState()).thenReturn(23687361);

        when(mockBluetoothDevice1.getAddress()).thenReturn(null);
        when(mockBluetoothDevice2.getAddress()).thenReturn(null);
        when(mockBluetoothDevice1.getAddress()).thenReturn("AA");

        Iterator<BluetoothDevice> mockIterator = mock(Iterator.class);

        when(mockIterator.hasNext()).thenReturn(true, true, true, false);
        when(mockIterator.next()).thenReturn(mockBluetoothDevice1)
                .thenReturn(mockBluetoothDevice2)
                .thenReturn(mockBluetoothDevice3);

        when(mockSet.iterator()).thenReturn(mockIterator);

        cut.startListening();
        Assert.assertTrue(cut.isRunning());




        cut.startListening();
    }

    @Test
    public void stopListening() throws Exception {
        BluetoothListener cut = new BluetoothListener(mockContext,
                mockProbeManager,
                mockBluetoothAdapter,
                mockPermissionDeniedHandler,
                mockBluetoothInformationReceiverFactory,
                mockIntenFilterFactory);

        IntentFilter mockIntentFilter = mock(IntentFilter.class);
        when(mockIntenFilterFactory.create()).thenReturn(mockIntentFilter);

        cut.startListening();
        cut.stopListening();
        Assert.assertFalse(cut.isRunning());
    }

    @Test
    public void isRunning() throws Exception {
        BluetoothListener cut = new BluetoothListener(mockContext,
                mockProbeManager,
                mockBluetoothAdapter,
                mockPermissionDeniedHandler,
                mockBluetoothInformationReceiverFactory,
                mockIntenFilterFactory);

        IntentFilter mockIntentFilter = mock(IntentFilter.class);
        when(mockIntenFilterFactory.create()).thenReturn(mockIntentFilter);

        cut.startListening();
        Assert.assertTrue(cut.isRunning());

        cut.stopListening();
        Assert.assertFalse(cut.isRunning());
    }

    @Test
    public void getBluetoothAdapter() throws Exception {
        BluetoothListener cut = new BluetoothListener(mockContext,
                mockProbeManager,
                mockBluetoothAdapter,
                mockPermissionDeniedHandler,
                mockBluetoothInformationReceiverFactory,
                mockIntenFilterFactory);

        Assert.assertEquals(cut.getBluetoothAdapter(), mockBluetoothAdapter);
    }

    @Test
    public void getState() throws Exception {
        BluetoothListener cut = new BluetoothListener(mockContext,
                mockProbeManager,
                mockBluetoothAdapter,
                mockPermissionDeniedHandler,
                mockBluetoothInformationReceiverFactory,
                mockIntenFilterFactory);

        when(mockContext.checkPermission(anyString(), anyInt(), anyInt())).thenReturn(PackageManager.PERMISSION_GRANTED);

        cut.getState();
        verify(mockBluetoothAdapter).getState();

        when(mockContext.checkPermission(anyString(), anyInt(), anyInt())).thenReturn(PackageManager.PERMISSION_DENIED);
        Assert.assertEquals(cut.getState(), 0);
        verify(mockPermissionDeniedHandler).onPermissionDenied(Manifest.permission.BLUETOOTH);
    }

}