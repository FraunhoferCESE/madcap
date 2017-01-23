package edu.umd.fcmd.sensorlisteners.listener.network;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static android.provider.Telephony.TextBasedSmsColumns.MESSAGE_TYPE_SENT;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by MMueller on 1/23/2017.
 */
public class SMSOutObserverTest {
    Handler mockHandler;
    NetworkListener mockNetworkListener;
    Context mockContext;

    @Before
    public void setUp() throws Exception {
        mockHandler = mock(Handler.class);
        mockNetworkListener = mock(NetworkListener.class);
        mockContext = mock(Context.class);
    }

    @Test
    public void constructorTest() throws Exception {
        SMSOutObserver cut = new SMSOutObserver(mockHandler, mockNetworkListener, mockContext);
    }

    @Test
    public void onChange() throws Exception {
        SMSOutObserver cut = new SMSOutObserver(mockHandler, mockNetworkListener, mockContext);

        Uri mockUri = mock(Uri.class);
        ContentResolver mockContentResolver = mock(ContentResolver.class);
        when(mockContext.getContentResolver()).thenReturn(mockContentResolver);

        Cursor mockCursor = mock(Cursor.class);
        when(mockContentResolver.query(
                Uri.parse("content://sms"), null, null, null, null)).thenReturn(mockCursor);

        when(mockCursor.getInt(mockCursor.getColumnIndex("type"))).thenReturn(MESSAGE_TYPE_SENT);

        when(mockCursor.moveToNext()).thenReturn(true);
        when(mockCursor.getString(mockCursor.getColumnIndex("protocol"))).thenReturn("non null");

        cut.onChange(true, mockUri);

        when(mockCursor.getString(mockCursor.getColumnIndex("protocol"))).thenReturn(null);
        cut.onChange(true, mockUri);

        when(mockContentResolver.query(
                Uri.parse("content://sms"), null, null, null, null)).thenReturn(null);

    }

    @Test
    public void deliverSelfNotifications() throws Exception {
        SMSOutObserver cut = new SMSOutObserver(mockHandler, mockNetworkListener, mockContext);

        Assert.assertFalse(cut.deliverSelfNotifications());
    }

}