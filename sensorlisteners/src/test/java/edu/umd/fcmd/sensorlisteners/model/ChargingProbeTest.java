package edu.umd.fcmd.sensorlisteners.model;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.umd.fcmd.sensorlisteners.model.powerC.ChargingProbe;

/**
 * Created by MMueller on 12/12/2016.
 */
public class ChargingProbeTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void getCharging() throws Exception {
        ChargingProbe cut = new ChargingProbe();

        cut.setCharging("USB");

        Assert.assertEquals(cut.getCharging(), "USB");
    }

    @Test
    public void setCharging() throws Exception {
        ChargingProbe cut = new ChargingProbe();

        cut.setCharging("USB");

        Assert.assertEquals(cut.getCharging(), "USB");
    }

    @Test
    public void getType() throws Exception {
        ChargingProbe cut = new ChargingProbe();

        Assert.assertEquals(cut.getType(), "Charging");
    }

    @Test
    public void testToString() throws Exception {
        ChargingProbe cut = new ChargingProbe();

        Assert.assertEquals(cut.toString(),"{\"charging\": " + "-" + '}' );

        cut.setCharging("USB");
        Assert.assertEquals(cut.toString(),"{\"charging\": " + "USB" + '}' );
    }

}