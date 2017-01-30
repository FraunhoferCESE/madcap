package edu.umd.fcmd.sensorlisteners.listener.network;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.umd.fcmd.sensorlisteners.model.network.NetworkProbe;
import edu.umd.fcmd.sensorlisteners.model.network.WiFiProbe;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by MMueller on 12/27/2016.
 */
public class ConnectionInfoReceiverTest {
    NetworkListener mockNetworkListener;

    @Before
    public void setUp() throws Exception {
        mockNetworkListener = mock(NetworkListener.class);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void constructorTest() throws Exception {
        ConnectionInfoReceiver cut = new ConnectionInfoReceiver(mockNetworkListener);

    }

    @Test
    public void onReceive() throws Exception {
        ConnectionInfoReceiver cut = new ConnectionInfoReceiver(mockNetworkListener);

        Context mockContext = mock(Context.class);
        Intent mockIntent = mock(Intent.class);

        when(mockIntent.getAction()).thenReturn(ConnectivityManager.CONNECTIVITY_ACTION);
        cut.onReceive(mockContext, mockIntent);
        verify(mockNetworkListener).onUpdate(any(NetworkProbe.class));

        when(mockIntent.getAction()).thenReturn(WifiManager.WIFI_STATE_CHANGED_ACTION);
        cut.onReceive(mockContext, mockIntent);
        verify(mockNetworkListener, atLeastOnce()).onUpdate(any(WiFiProbe.class));

        when(mockIntent.getAction()).thenReturn(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        when(mockIntent.hasExtra(WifiManager.EXTRA_NETWORK_INFO)).thenReturn(true);
        NetworkInfo mockNetworkInfo = mock(NetworkInfo.class);
        when(mockIntent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO)).thenReturn(mockNetworkInfo);
        when(mockNetworkInfo.toString()).thenReturn("Bratwurst");
        when(mockIntent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)).thenReturn(true);
        cut.onReceive(mockContext, mockIntent);
        verify(mockNetworkListener, atLeastOnce()).onUpdate(any(WiFiProbe.class));
        verify(mockNetworkListener, atLeastOnce()).onUpdate(any(NetworkProbe.class));

        when(mockIntent.getAction()).thenReturn(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        when(mockIntent.hasExtra(WifiManager.EXTRA_NETWORK_INFO)).thenReturn(true);
        NetworkInfo mockNetworkInfo2 = mock(NetworkInfo.class);
        when(mockIntent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO)).thenReturn(mockNetworkInfo2);
        when(mockNetworkInfo.toString()).thenReturn("Bratwurst");
        cut.onReceive(mockContext, mockIntent);
        verify(mockNetworkListener, atLeastOnce()).onUpdate(any(WiFiProbe.class));

        when(mockIntent.getAction()).thenReturn(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED);
        NfcManager mockNfcManager = mock(NfcManager.class);
        when(mockContext.getSystemService(Context.NFC_SERVICE)).thenReturn(mockNfcManager);
        NfcAdapter mockNfcAdapter = mock(NfcAdapter.class);
        when(mockNfcManager.getDefaultAdapter()).thenReturn(mockNfcAdapter);
        cut.onReceive(mockContext, mockIntent);
        verify(mockNetworkListener, atLeastOnce()).onUpdate(any(WiFiProbe.class));

        when(mockIntent.getAction()).thenReturn("Frikadelle");
        cut.onReceive(mockContext, mockIntent);
        verify(mockNetworkListener, atLeastOnce()).onUpdate(any(WiFiProbe.class));


    }

}