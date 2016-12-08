package edu.umd.fcmd.sensorlisteners.model;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by MMueller on 12/7/2016.
 */
public class BluetoothScanModeProbeTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void getState() throws Exception {
        BluetoothScanModeProbe cut = new BluetoothScanModeProbe();

        cut.setState("state");

        Assert.assertEquals(cut.getState(), "state");
    }

    @Test
    public void setState() throws Exception {
        BluetoothScanModeProbe cut = new BluetoothScanModeProbe();

        cut.setState("state");

        Assert.assertEquals(cut.getState(), "state");
    }

    @Test
    public void getType() throws Exception {
        BluetoothScanModeProbe cut = new BluetoothScanModeProbe();

        Assert.assertEquals(cut.getType(), "BluetoothScanMode");
    }

    @Test
    public void testToString() throws Exception {
        BluetoothScanModeProbe cut = new BluetoothScanModeProbe();

        cut.setState("state");

        Assert.assertEquals(cut.toString(), "{\"state\": " + "state" +
                '}');
    }

}