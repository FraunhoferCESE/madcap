package edu.umd.fcmd.sensorlisteners.listener.network;

import android.content.Context;
import android.telephony.CellLocation;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.umd.fcmd.sensorlisteners.model.network.CallStateProbe;
import edu.umd.fcmd.sensorlisteners.model.network.CellProbe;
import edu.umd.fcmd.sensorlisteners.model.network.TelecomServiceProbe;

import static android.content.Context.TELEPHONY_SERVICE;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by MMueller on 12/27/2016.
 */
public class TelephonyListenerTest {
    Context mockContext;
    NetworkListener mockNetworkListener;

    @Before
    public void setUp() throws Exception {
        mockContext = mock(Context.class);
        mockNetworkListener = mock(NetworkListener.class);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void constructorTest() throws Exception {
        TelephonyListener cut = new TelephonyListener(mockContext, mockNetworkListener);
    }

    @Test
    public void onDataConnectionStateChanged() throws Exception {
        TelephonyListener cut = new TelephonyListener(mockContext, mockNetworkListener);

        TelephonyManager mockTelephonyManager = mock(TelephonyManager.class);
        when(mockContext.getSystemService(TELEPHONY_SERVICE)).thenReturn(mockTelephonyManager);

        cut.onDataConnectionStateChanged(1, 1);
        verify(mockNetworkListener, atLeastOnce()).onUpdate(any(CellProbe.class));

    }

    @Test
    public void onServiceStateChanged() throws Exception {
        TelephonyListener cut = new TelephonyListener(mockContext, mockNetworkListener);

        ServiceState mockServiceState = mock(ServiceState.class);

        when(mockServiceState.getRoaming()).thenReturn(true);
        when(mockServiceState.getState()).thenReturn(122346);
        cut.onServiceStateChanged(mockServiceState);

        when(mockServiceState.getRoaming()).thenReturn(false);
        when(mockServiceState.getState()).thenReturn(ServiceState.STATE_IN_SERVICE);
        cut.onServiceStateChanged(mockServiceState);

        when(mockServiceState.getRoaming()).thenReturn(true);
        when(mockServiceState.getState()).thenReturn(ServiceState.STATE_OUT_OF_SERVICE);
        cut.onServiceStateChanged(mockServiceState);

        when(mockServiceState.getRoaming()).thenReturn(true);
        when(mockServiceState.getState()).thenReturn(ServiceState.STATE_EMERGENCY_ONLY);
        cut.onServiceStateChanged(mockServiceState);

        when(mockServiceState.getRoaming()).thenReturn(true);
        when(mockServiceState.getState()).thenReturn(ServiceState.STATE_POWER_OFF);
        cut.onServiceStateChanged(mockServiceState);

        verify(mockNetworkListener, atLeastOnce()).onUpdate(any(TelecomServiceProbe.class));
    }

    @Test
    public void onCallStateChanged() throws Exception {
        TelephonyListener cut = new TelephonyListener(mockContext, mockNetworkListener);

        cut.onCallStateChanged(1, "number");
        verify(mockNetworkListener, atLeastOnce()).onUpdate(any(CallStateProbe.class));
    }

    @Test
    public void onCellLocationChanged() throws Exception {
        TelephonyListener cut = new TelephonyListener(mockContext, mockNetworkListener);

        CellLocation mockCellLocation = mock(CellLocation.class);

        cut.onCellLocationChanged(mockCellLocation);

    }

    @Test
    public void createCellLocationProbe() throws Exception {
        TelephonyListener cut = new TelephonyListener(mockContext, mockNetworkListener);

        GsmCellLocation mockGSMCellLocation = mock(GsmCellLocation.class);
        when(mockGSMCellLocation.getLac()).thenReturn(1000);

        cut.createCellLocationProbe(mockGSMCellLocation);

        CdmaCellLocation mockCdmaCellLocation = mock(CdmaCellLocation.class);
        when(mockCdmaCellLocation.getBaseStationId()).thenReturn(1000);
        when(mockCdmaCellLocation.getBaseStationLatitude()).thenReturn(1000);
        when(mockCdmaCellLocation.getBaseStationLongitude()).thenReturn(1000);

        cut.createCellLocationProbe(mockCdmaCellLocation);

    }

    @Test
    public void createNewCallStateProbe() throws Exception {
        TelephonyListener cut = new TelephonyListener(mockContext, mockNetworkListener);

        Assert.assertEquals("IDLE", cut.createNewCallStateProbe(TelephonyManager.CALL_STATE_IDLE, "Number").getState());
        Assert.assertEquals("RINGING", cut.createNewCallStateProbe(TelephonyManager.CALL_STATE_RINGING, "Number").getState());
        Assert.assertEquals("OFFHOOK", cut.createNewCallStateProbe(TelephonyManager.CALL_STATE_OFFHOOK, "Number").getState());
        Assert.assertEquals("UNKNOWN", cut.createNewCallStateProbe(123456, "Number").getState());

    }

    @Test
    public void createNewCellularProbe() throws Exception {
        TelephonyListener cut = new TelephonyListener(mockContext, mockNetworkListener);

        TelephonyManager mockTelephonyManager = mock(TelephonyManager.class);
        when(mockContext.getSystemService(TELEPHONY_SERVICE)).thenReturn(mockTelephonyManager);

        when(mockTelephonyManager.getDataState()).thenReturn(TelephonyManager.DATA_CONNECTED);
        cut.createNewCellularProbe();

        when(mockTelephonyManager.getDataState()).thenReturn(TelephonyManager.DATA_DISCONNECTED);
        cut.createNewCellularProbe();

        when(mockTelephonyManager.getDataState()).thenReturn(TelephonyManager.DATA_CONNECTING);
        cut.createNewCellularProbe();

        when(mockTelephonyManager.getDataState()).thenReturn(TelephonyManager.DATA_SUSPENDED);
        cut.createNewCellularProbe();

        when(mockTelephonyManager.getDataState()).thenReturn(123456);
        cut.createNewCellularProbe();

    }

}