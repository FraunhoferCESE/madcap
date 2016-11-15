package edu.umd.fcmd.sensorlisteners.listener.location;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by MMueller on 11/15/2016.
 */
public class LocationServiceStatusReceiverFactoryTest {
    @Test
    public void create() throws Exception {
        LocationServiceStatusReceiverFactory cut = new LocationServiceStatusReceiverFactory();

        LocationListener mockLocationListener = mock(LocationListener.class);
        cut.create(mockLocationListener);
    }

}