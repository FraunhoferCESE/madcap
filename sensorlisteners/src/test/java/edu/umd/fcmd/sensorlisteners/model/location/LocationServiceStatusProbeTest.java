package edu.umd.fcmd.sensorlisteners.model.location;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by MMueller on 11/15/2016.
 */
public class LocationServiceStatusProbeTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void getLocationServiceStatus() throws Exception {
        LocationServiceStatusProbe cut = new LocationServiceStatusProbe();

        cut.setLocationServiceStatus("ON");

        Assert.assertEquals(cut.getLocationServiceStatus(), "ON");
    }

    @Test
    public void setLocationServiceStatus() throws Exception {
        LocationServiceStatusProbe cut = new LocationServiceStatusProbe();

        cut.setLocationServiceStatus("ON");

        Assert.assertEquals(cut.getLocationServiceStatus(), "ON");
    }

    @Test
    public void getType() throws Exception {
        LocationServiceStatusProbe cut = new LocationServiceStatusProbe();

        Assert.assertEquals(cut.getType(), "LocationService");
    }

    @Test
    public void testToString() throws Exception {
        LocationServiceStatusProbe cut = new LocationServiceStatusProbe();

        cut.setLocationServiceStatus("ON");

        Assert.assertEquals(cut.toString(), "{\"LocationServiceStatus\": " + "ON" +
                '}');
    }

}