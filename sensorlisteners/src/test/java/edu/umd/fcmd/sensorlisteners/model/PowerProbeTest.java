package edu.umd.fcmd.sensorlisteners.model;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by MMueller on 12/12/2016.
 */
public class PowerProbeTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void getRemainingPower() throws Exception {
        PowerProbe cut = new PowerProbe();

        cut.setRemainingPower(1.0);

        Assert.assertEquals(cut.getRemainingPower(), 1.0);
    }

    @Test
    public void setRemainingPower() throws Exception {
        PowerProbe cut = new PowerProbe();

        cut.setRemainingPower(1.0);

        Assert.assertEquals(cut.getRemainingPower(), 1.0);
    }

    @Test
    public void getType() throws Exception {
        PowerProbe cut = new PowerProbe();

        Assert.assertEquals(cut.getType(), "Power");
    }

    @Test
    public void testToString() throws Exception {
        PowerProbe cut = new PowerProbe();

        Assert.assertEquals(cut.toString(), "{\"remainingPower\": " + 0.0 + '}');
    }

}