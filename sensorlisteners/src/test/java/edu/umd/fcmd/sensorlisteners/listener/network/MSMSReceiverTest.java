package edu.umd.fcmd.sensorlisteners.listener.network;

import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.umd.fcmd.sensorlisteners.model.network.MSMSProbe;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by MMueller on 12/29/2016.
 */
public class MSMSReceiverTest {
    NetworkListener mockNetworkListener;

    @Before
    public void setUp() throws Exception {
        mockNetworkListener = mock(NetworkListener.class);
    }

    @Test
    public void constructorTest() throws Exception {
        MSMSReceiver cut = new MSMSReceiver(mockNetworkListener);

    }

    @Test
    public void onReceive() throws Exception {
        MSMSReceiver cut = new MSMSReceiver(mockNetworkListener);
        Context mockContext = mock(Context.class);

        Intent mockIntent = mock(Intent.class);

        when(mockIntent.getAction()).thenReturn(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        cut.onReceive(mockContext, mockIntent);
        verify(mockNetworkListener, atLeastOnce()).onUpdate(any(MSMSProbe.class));

        when(mockIntent.getAction()).thenReturn(Telephony.Sms.Intents.DATA_SMS_RECEIVED_ACTION);
        cut.onReceive(mockContext, mockIntent);
        verify(mockNetworkListener, atLeastOnce()).onUpdate(any(MSMSProbe.class));

        when(mockIntent.getAction()).thenReturn(Telephony.Sms.Intents.SMS_CB_RECEIVED_ACTION);
        cut.onReceive(mockContext, mockIntent);
        verify(mockNetworkListener, atLeastOnce()).onUpdate(any(MSMSProbe.class));

        when(mockIntent.getAction()).thenReturn(Telephony.Sms.Intents.SMS_REJECTED_ACTION);
        cut.onReceive(mockContext, mockIntent);
        verify(mockNetworkListener, atLeastOnce()).onUpdate(any(MSMSProbe.class));

        when(mockIntent.getAction()).thenReturn(Telephony.Sms.Intents.WAP_PUSH_RECEIVED_ACTION);
        cut.onReceive(mockContext, mockIntent);
        verify(mockNetworkListener, atLeastOnce()).onUpdate(any(MSMSProbe.class));

        when(mockIntent.getAction()).thenReturn("android.provider.Telephony.SMS_SENT");
        cut.onReceive(mockContext, mockIntent);
        verify(mockNetworkListener, atLeastOnce()).onUpdate(any(MSMSProbe.class));

        when(mockIntent.getAction()).thenReturn(Telephony.Mms.Intents.CONTENT_CHANGED_ACTION);
        cut.onReceive(mockContext, mockIntent);
        verify(mockNetworkListener, atLeastOnce()).onUpdate(any(MSMSProbe.class));

        when(mockIntent.getAction()).thenReturn("Currywurst");
        cut.onReceive(mockContext, mockIntent);
        verify(mockNetworkListener, atLeastOnce()).onUpdate(any(MSMSProbe.class));
    }

}