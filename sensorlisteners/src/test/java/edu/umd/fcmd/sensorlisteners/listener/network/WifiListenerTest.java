package edu.umd.fcmd.sensorlisteners.listener.network;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.telephony.TelephonyManager;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;

import edu.umd.fcmd.sensorlisteners.issuehandling.PermissionsManager;
import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.model.network.NFCProbe;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;

import static android.content.Context.TELEPHONY_SERVICE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by MMueller on 12/27/2016.
 */
public class WifiListenerTest {
    Context mockContext;
    ProbeManager<Probe> mockProbeManager;
    ConnectionInfoReceiverFactory mockConnectionInfoReceiverFactory;
    TelephonyListenerFactory mockTelephonyListenerFactory;
    PermissionsManager mockPermissionsManager;
    MSMSReceiverFactory mockMSMSReceiverFactory;
    SMSOutObserverFactory mockSMSOutObserverFactory;
    MMSOutObserverFactory mockMMSOutObserverFactory;

    @Before
    public void setUp() throws Exception {
        mockContext = mock(Context.class);
        mockProbeManager = mock(ProbeManager.class);
        mockConnectionInfoReceiverFactory = mock(ConnectionInfoReceiverFactory.class);
        mockTelephonyListenerFactory = mock(TelephonyListenerFactory.class);
        mockPermissionsManager = mock(PermissionsManager.class);
        mockMSMSReceiverFactory = mock(MSMSReceiverFactory.class);
        mockSMSOutObserverFactory = mock(SMSOutObserverFactory.class);
        mockMMSOutObserverFactory = mock(MMSOutObserverFactory.class);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void consturctorTest() throws Exception {
        WifiListener cut = new WifiListener(mockContext,
                mockProbeManager,
                mockConnectionInfoReceiverFactory,
                mockMSMSReceiverFactory,
                mockTelephonyListenerFactory,
                mockSMSOutObserverFactory,
                mockMMSOutObserverFactory,
                mockPermissionsManager);
    }

    @Test
    public void onUpdate() throws Exception {
        WifiListener cut = new WifiListener(mockContext,
                mockProbeManager,
                mockConnectionInfoReceiverFactory,
                mockMSMSReceiverFactory,
                mockTelephonyListenerFactory,
                mockSMSOutObserverFactory,
                mockMMSOutObserverFactory,
                mockPermissionsManager);

        Probe mockProbe = mock(Probe.class);

        cut.onUpdate(mockProbe);
        verify(mockProbeManager).save(mockProbe);

    }

    @Test
    public void startListening() throws Exception {
        WifiListener cut = new WifiListener(mockContext,
                mockProbeManager,
                mockConnectionInfoReceiverFactory,
                mockMSMSReceiverFactory,
                mockTelephonyListenerFactory,
                mockSMSOutObserverFactory,
                mockMMSOutObserverFactory,
                mockPermissionsManager);

        TelephonyListener mockTelephonyListener = mock(TelephonyListener.class);
        when(mockTelephonyListenerFactory.create(mockContext, cut)).thenReturn(mockTelephonyListener);

        TelephonyManager mockTelephonyManager = mock(TelephonyManager.class);
        when(mockContext.getSystemService(TELEPHONY_SERVICE)).thenReturn(mockTelephonyManager);

        WifiManager mockWifiManager = mock(WifiManager.class);
        when(mockContext.getSystemService(Context.WIFI_SERVICE)).thenReturn(mockWifiManager);
        WifiInfo mockWifiInfo = mock(WifiInfo.class);
        when(mockWifiManager.getConnectionInfo()).thenReturn(mockWifiInfo);
        when(mockWifiInfo.getIpAddress()).thenReturn(128001);

        SMSOutObserver mockSMSOutObserver = mock(SMSOutObserver.class);
        when(mockSMSOutObserverFactory.create(cut, mockContext)).thenReturn(mockSMSOutObserver);

        MMSOutObserver mockMMSOutObserver = mock(MMSOutObserver.class);
        when(mockMMSOutObserverFactory.create(cut)).thenReturn(mockMMSOutObserver);

        ContentResolver mockContentResolver = mock(ContentResolver.class);
        when(mockContext.getContentResolver()).thenReturn(mockContentResolver);

        NfcManager mockNfcManager = mock(NfcManager.class);
        when(mockContext.getSystemService(Context.NFC_SERVICE)).thenReturn(mockNfcManager);
        NfcAdapter mockNfcAdapter = mock(NfcAdapter.class);
        when(mockNfcManager.getDefaultAdapter()).thenReturn(mockNfcAdapter);

        cut.startListening();

        Assert.assertTrue(cut.isRunning());

    }

    @Test
    public void stopListening() throws Exception {
        WifiListener cut = new WifiListener(mockContext,
                mockProbeManager,
                mockConnectionInfoReceiverFactory,
                mockMSMSReceiverFactory,
                mockTelephonyListenerFactory,
                mockSMSOutObserverFactory,
                mockMMSOutObserverFactory,
                mockPermissionsManager);

        TelephonyListener mockTelephonyListener = mock(TelephonyListener.class);
        when(mockTelephonyListenerFactory.create(mockContext, cut)).thenReturn(mockTelephonyListener);

        TelephonyManager mockTelephonyManager = mock(TelephonyManager.class);
        when(mockContext.getSystemService(TELEPHONY_SERVICE)).thenReturn(mockTelephonyManager);

        WifiManager mockWifiManager = mock(WifiManager.class);
        when(mockContext.getSystemService(Context.WIFI_SERVICE)).thenReturn(mockWifiManager);
        WifiInfo mockWifiInfo = mock(WifiInfo.class);
        when(mockWifiManager.getConnectionInfo()).thenReturn(mockWifiInfo);
        when(mockWifiInfo.getIpAddress()).thenReturn(128001);

        SMSOutObserver mockSMSOutObserver = mock(SMSOutObserver.class);
        when(mockSMSOutObserverFactory.create(cut, mockContext)).thenReturn(mockSMSOutObserver);

        MMSOutObserver mockMMSOutObserver = mock(MMSOutObserver.class);
        when(mockMMSOutObserverFactory.create(cut)).thenReturn(mockMMSOutObserver);

        ContentResolver mockContentResolver = mock(ContentResolver.class);
        when(mockContext.getContentResolver()).thenReturn(mockContentResolver);

        NfcManager mockNfcManager = mock(NfcManager.class);
        when(mockContext.getSystemService(Context.NFC_SERVICE)).thenReturn(mockNfcManager);
        NfcAdapter mockNfcAdapter = mock(NfcAdapter.class);
        when(mockNfcManager.getDefaultAdapter()).thenReturn(mockNfcAdapter);

        cut.startListening();
        cut.stopListening();

        Assert.assertFalse(cut.isRunning());

    }

    @Test
    public void isRunning() throws Exception {
        WifiListener cut = new WifiListener(mockContext,
        mockProbeManager,
                mockConnectionInfoReceiverFactory,
                mockMSMSReceiverFactory,
                mockTelephonyListenerFactory,
                mockSMSOutObserverFactory,
                mockMMSOutObserverFactory,
                mockPermissionsManager);

        TelephonyListener mockTelephonyListener = mock(TelephonyListener.class);
        when(mockTelephonyListenerFactory.create(mockContext, cut)).thenReturn(mockTelephonyListener);

        TelephonyManager mockTelephonyManager = mock(TelephonyManager.class);
        when(mockContext.getSystemService(TELEPHONY_SERVICE)).thenReturn(mockTelephonyManager);

        WifiManager mockWifiManager = mock(WifiManager.class);
        when(mockContext.getSystemService(Context.WIFI_SERVICE)).thenReturn(mockWifiManager);
        WifiInfo mockWifiInfo = mock(WifiInfo.class);
        when(mockWifiManager.getConnectionInfo()).thenReturn(mockWifiInfo);
        when(mockWifiInfo.getIpAddress()).thenReturn(128001);

        SMSOutObserver mockSMSOutObserver = mock(SMSOutObserver.class);
        when(mockSMSOutObserverFactory.create(cut, mockContext)).thenReturn(mockSMSOutObserver);

        MMSOutObserver mockMMSOutObserver = mock(MMSOutObserver.class);
        when(mockMMSOutObserverFactory.create(cut)).thenReturn(mockMMSOutObserver);

        ContentResolver mockContentResolver = mock(ContentResolver.class);
        when(mockContext.getContentResolver()).thenReturn(mockContentResolver);

        NfcManager mockNfcManager = mock(NfcManager.class);
        when(mockContext.getSystemService(Context.NFC_SERVICE)).thenReturn(mockNfcManager);
        NfcAdapter mockNfcAdapter = mock(NfcAdapter.class);
        when(mockNfcManager.getDefaultAdapter()).thenReturn(mockNfcAdapter);

        cut.startListening();
        Assert.assertTrue(cut.isRunning());

        cut.stopListening();
        Assert.assertFalse(cut.isRunning());

    }

    @Test
    public void getWifiState() throws Exception {
        WifiListener cut = new WifiListener(mockContext,
                mockProbeManager,
                mockConnectionInfoReceiverFactory,
                mockMSMSReceiverFactory,
                mockTelephonyListenerFactory,
                mockSMSOutObserverFactory,
                mockMMSOutObserverFactory,
                mockPermissionsManager);

        Intent mockIntent = mock(Intent.class);

        when(mockIntent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0)).thenReturn(WifiManager.WIFI_STATE_ENABLED);
        Assert.assertEquals("ENABLED", cut.getWifiState(mockIntent));

        when(mockIntent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0)).thenReturn(WifiManager.WIFI_STATE_DISABLED);
        Assert.assertEquals("DISABLED", cut.getWifiState(mockIntent));

        when(mockIntent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0)).thenReturn(WifiManager.WIFI_STATE_UNKNOWN);
        Assert.assertEquals("UNKNOWN", cut.getWifiState(mockIntent));

        when(mockIntent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0)).thenReturn(1331355431);
        Assert.assertEquals("-", cut.getWifiState(mockIntent));

        //========//

        Assert.assertEquals("ENABLED", cut.getWifiState(WifiManager.WIFI_STATE_ENABLED));

        Assert.assertEquals("DISABLED", cut.getWifiState(WifiManager.WIFI_STATE_DISABLED));

        Assert.assertEquals("ENABLING", cut.getWifiState(WifiManager.WIFI_STATE_ENABLING));

        Assert.assertEquals("DISABLING", cut.getWifiState(WifiManager.WIFI_STATE_DISABLING));

        Assert.assertEquals("UNKNOWN", cut.getWifiState(WifiManager.WIFI_STATE_UNKNOWN));

        Assert.assertEquals("-", cut.getWifiState(12135682));

    }

    @Test
    public void getCurrentSecurityLevel() throws Exception {
        WifiListener cut = new WifiListener(mockContext,
                mockProbeManager,
                mockConnectionInfoReceiverFactory,
                mockMSMSReceiverFactory,
                mockTelephonyListenerFactory,
                mockSMSOutObserverFactory,
                mockMMSOutObserverFactory,
                mockPermissionsManager);

        WifiManager mockWifiManager = mock(WifiManager.class);
        when(mockContext.getSystemService(Context.WIFI_SERVICE)).thenReturn(mockWifiManager);

        List<ScanResult> mockResultList = mock(List.class);
        when(mockWifiManager.getScanResults()).thenReturn(mockResultList);

        WifiInfo mockWifiInfo = mock(WifiInfo.class);
        when(mockWifiInfo.getSSID()).thenReturn("\""+"Haehnchen"+"\"");
        when(mockWifiManager.getConnectionInfo()).thenReturn(mockWifiInfo);

        ScanResult mockScanResult1 = mock(ScanResult.class);
        ScanResult mockScanResult2 = mock(ScanResult.class);
        ScanResult mockScanResult3 = mock(ScanResult.class);
        ScanResult mockScanResult4 = mock(ScanResult.class);

        mockScanResult1.SSID = "Haehnchen";
        mockScanResult2.SSID = "Haehnchen";
        mockScanResult3.SSID = "Haehnchen";
        mockScanResult4.SSID = "Haehnchen";

        mockScanResult1.capabilities="WPA2";
        mockScanResult2.capabilities="WPA";
        mockScanResult3.capabilities="WEP";
        mockScanResult4.capabilities="Open";

        Iterator<ScanResult> mockIterator = mock(Iterator.class);

        when(mockResultList.iterator()).thenReturn(mockIterator);

        when(mockIterator.hasNext()).thenReturn(true, false);
        when(mockIterator.next()).thenReturn(mockScanResult1);
        Assert.assertEquals("WPA2", cut.getCurrentSecurityLevel());

        when(mockIterator.hasNext()).thenReturn(true, false);
        when(mockIterator.next()).thenReturn(mockScanResult2);
        Assert.assertEquals("WPA", cut.getCurrentSecurityLevel());

        when(mockIterator.hasNext()).thenReturn(true, false);
        when(mockIterator.next()).thenReturn(mockScanResult3);
        Assert.assertEquals("WEP", cut.getCurrentSecurityLevel());

        when(mockIterator.hasNext()).thenReturn(true, false);
        when(mockIterator.next()).thenReturn(mockScanResult4);
        Assert.assertEquals("OPEN", cut.getCurrentSecurityLevel());

        when(mockWifiManager.getScanResults()).thenReturn(null);
        Assert.assertEquals("-", cut.getCurrentSecurityLevel());

    }

    @Test
    public void getCurrentNFCState(){
        WifiListener cut = new WifiListener(mockContext,
                mockProbeManager,
                mockConnectionInfoReceiverFactory,
                mockMSMSReceiverFactory,
                mockTelephonyListenerFactory,
                mockSMSOutObserverFactory,
                mockMMSOutObserverFactory,
                mockPermissionsManager);

        NfcManager mockNfcManager = mock(NfcManager.class);
        when(mockContext.getSystemService(Context.NFC_SERVICE)).thenReturn(mockNfcManager);
        NfcAdapter mockNfcAdapter = mock(NfcAdapter.class);
        when(mockNfcManager.getDefaultAdapter()).thenReturn(mockNfcAdapter);

        when(mockNfcAdapter.isEnabled()).thenReturn(true);
        Assert.assertEquals(NFCProbe.ON, cut.getCurrentNFCState());

        when(mockNfcAdapter.isEnabled()).thenReturn(false);
        Assert.assertEquals(NFCProbe.OFF, cut.getCurrentNFCState());
    }

}