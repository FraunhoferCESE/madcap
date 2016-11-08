package edu.umd.fcmd.sensorlisteners.model;

import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by MMueller on 11/8/2016.
 */
public class LocationStateTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void getAccuracy() throws Exception {
        LocationState cut = new LocationState();

        Double d = 21d;
        cut.setAccuracy(d);

        Assert.assertEquals(d, cut.getAccuracy());
    }

    @Test
    public void setAccuracy() throws Exception {
        LocationState cut = new LocationState();

        Double d = 21d;
        cut.setAccuracy(d);

        Assert.assertEquals(d, cut.getAccuracy());
    }

    @Test
    public void getAltitude() throws Exception {
        LocationState cut = new LocationState();

        Double d = 21d;
        cut.setAltitude(d);

        Assert.assertEquals(d, cut.getAltitude());
    }

    @Test
    public void setAltitude() throws Exception {
        LocationState cut = new LocationState();

        Double d = 21d;
        cut.setAltitude(d);

        Assert.assertEquals(d, cut.getAltitude());
    }

    @Test
    public void getBearing() throws Exception {
        LocationState cut = new LocationState();

        Double d = 21d;
        cut.setBearing(d);

        Assert.assertEquals(d, cut.getBearing());
    }

    @Test
    public void setBearing() throws Exception {
        LocationState cut = new LocationState();

        Double d = 21d;
        cut.setBearing(d);

        Assert.assertEquals(d, cut.getBearing());
    }

    @Test
    public void getExtras() throws Exception {
        LocationState cut = new LocationState();

        Bundle mockExtras = spy(Bundle.class);
        cut.setExtras(mockExtras);

        Assert.assertEquals(mockExtras, cut.getExtras());
    }

    @Test
    public void setExtras() throws Exception {
        LocationState cut = new LocationState();

        Bundle mockExtras = spy(Bundle.class);
        cut.setExtras(mockExtras);

        Assert.assertEquals(mockExtras, cut.getExtras());
    }

    @Test
    public void getLatitude() throws Exception {
        LocationState cut = new LocationState();

        Double d = 21d;
        cut.setLatitude(d);

        Assert.assertEquals(d, cut.getLatitude());
    }

    @Test
    public void setLatitude() throws Exception {
        LocationState cut = new LocationState();

        Double d = 21d;
        cut.setLatitude(d);

        Assert.assertEquals(d, cut.getLatitude());
    }

    @Test
    public void getLongitude() throws Exception {
        LocationState cut = new LocationState();

        Double d = 21d;
        cut.setLongitude(d);

        Assert.assertEquals(d, cut.getLongitude());
    }

    @Test
    public void setLongitude() throws Exception {
        LocationState cut = new LocationState();

        Double d = 21d;
        cut.setLongitude(d);

        Assert.assertEquals(d, cut.getLongitude());
    }

    @Test
    public void getType() throws Exception {
        LocationState cut = new LocationState();

        Assert.assertEquals("Location", cut.getType());
    }

    @Test
    public void testToString() throws Exception {
        LocationState cut = new LocationState();

        Double d = 21d;
        cut.setLatitude(d);
        cut.setLongitude(d);
        cut.setAltitude(d);
        cut.setAccuracy(d);
        cut.setBearing(d);

        Assert.assertEquals("If extra is not set it should print a \"-\" instead.", "{\"latitude\": 21.0, \"longitude\": 21.0, \"altitude\": 21.0, \"accuracy\": 21.0, \"bearing\": 21.0, \"extras\": -}", cut.toString());

        Bundle mockExtras = mock(Bundle.class);
        when(mockExtras.toString()).thenReturn("Extraobject");
        cut.setExtras(mockExtras);

        Assert.assertEquals("If extra is set it should return the extra.", "{\"latitude\": 21.0, \"longitude\": 21.0, \"altitude\": 21.0, \"accuracy\": 21.0, \"bearing\": 21.0, \"extras\": Extraobject}", cut.toString());
    }

}