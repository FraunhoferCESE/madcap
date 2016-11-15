package edu.umd.fcmd.sensorlisteners.listener.location;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import edu.umd.fcmd.sensorlisteners.model.LocationServiceStatusProbe;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by MMueller on 11/15/2016.
 */
public class LocationServiceStatusReceiverTest {
    LocationListener mockLocationListener;

    @Before
    public void setUp() throws Exception {
        mockLocationListener = mock(LocationListener.class);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void constrctorTest() throws Exception {
        LocationServiceStatusReceiver cut = new LocationServiceStatusReceiver(mockLocationListener);
    }

    @Test
    public void onReceive() throws Exception {
        LocationServiceStatusReceiver cut = new LocationServiceStatusReceiver(mockLocationListener);
        Context mockContext = mock(Context.class);
        Intent mockIntentProvidersFalse = mock(Intent.class);
        Intent mockIntentProvidersTrue = mock(Intent.class);

        //Case that not the right intent is being captured
        when(mockIntentProvidersTrue.getAction()).thenReturn("android.location.PROVIDERS_CHANGED");

        cut.onReceive(mockContext, mockIntentProvidersTrue);

        //Case that not the right intent is being captured
        when(mockIntentProvidersFalse.getAction()).thenReturn("Something else");

        cut.onReceive(mockContext, mockIntentProvidersFalse);

        //Testing inside of checkForStatus
        LocationManager mockLocationManager = mock(LocationManager.class);

        when((LocationManager) mockContext.getSystemService(Context.LOCATION_SERVICE)).thenReturn(mockLocationManager);

        //Case location service is enabled
        when(mockLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(true);

        cut.onReceive(mockContext, mockIntentProvidersTrue);
        verify(mockLocationListener, atLeastOnce()).onUpdate(any(LocationServiceStatusProbe.class));

        //Case location service is enabled
        when(mockLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)).thenReturn(false);

        cut.onReceive(mockContext, mockIntentProvidersTrue);
        verify(mockLocationListener, atLeastOnce()).onUpdate(any(LocationServiceStatusProbe.class));
    }

    @Test
    public void sendInitialProbe() throws Exception {
        LocationServiceStatusReceiver cut = new LocationServiceStatusReceiver(mockLocationListener);
        Context mockContext = mock(Context.class);

        cut.sendInitialProbe(mockContext);
    }

}