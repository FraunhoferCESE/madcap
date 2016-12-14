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
    public void setVoltage() throws Exception {
        PowerProbe cut = new PowerProbe();

        cut.setVoltage(1.0);

        Assert.assertEquals(cut.getVoltage(), 1.0);
    }

    @Test
    public void getVoltage() throws Exception {
        PowerProbe cut = new PowerProbe();

        cut.setVoltage(1.0);

        Assert.assertEquals(cut.getVoltage(), 1.0);
    }

    @Test
    public void setTemperature() throws Exception {
        PowerProbe cut = new PowerProbe();

        cut.setTemperature(1.0);

        Assert.assertEquals(cut.getTemperature(), 1.0);
    }

    @Test
    public void getTemperature() throws Exception {
        PowerProbe cut = new PowerProbe();

        cut.setTemperature(1.0);

        Assert.assertEquals(cut.getTemperature(), 1.0);
    }

    @Test
    public void setHealth() throws Exception {
        PowerProbe cut = new PowerProbe();

        cut.setHealth(1.0);

        Assert.assertEquals(cut.getHealth(), 1.0);
    }

    @Test
    public void getHealth() throws Exception {
        PowerProbe cut = new PowerProbe();

        cut.setHealth(1.0);

        Assert.assertEquals(cut.getHealth(), 1.0);
    }

    @Test
    public void testToString() throws Exception {
        PowerProbe cut = new PowerProbe();

        Assert.assertEquals(cut.toString(), "{\"remainingPower\": " + 0.0 +
                ", \"voltage\": " + 0.0 +
                ", \"health\": " + 0.0 +
                ", \"temperature\": " + 0.0 +
                '}');

        cut.setHealth(1.0);
        cut.setRemainingPower(1.0);
        cut.setVoltage(1.0);
        cut.setTemperature(1.0);

        Assert.assertEquals(cut.toString(), "{\"remainingPower\": " + 1.0 +
                ", \"voltage\": " + 1.0 +
                ", \"health\": " + 1.0 +
                ", \"temperature\": " + 1.0 +
                '}');

    }

}