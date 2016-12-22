package edu.umd.fcmd.sensorlisteners.model.bluetooth;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by MMueller on 12/7/2016.
 */
public class BluetoothDiscoveryProbeTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void getState() throws Exception {
        BluetoothDiscoveryProbe cut = new BluetoothDiscoveryProbe();

        cut.setState("state");

        Assert.assertEquals(cut.getState(), "state");
    }

    @Test
    public void setState() throws Exception {
        BluetoothDiscoveryProbe cut = new BluetoothDiscoveryProbe();

        cut.setState("state");

        Assert.assertEquals(cut.getState(), "state");
    }

    @Test
    public void getType() throws Exception {
        BluetoothDiscoveryProbe cut = new BluetoothDiscoveryProbe();

        Assert.assertEquals(cut.getType(), "BluetoothDiscoveryProbe");
    }

    @Test
    public void testToString() throws Exception {
        BluetoothDiscoveryProbe cut = new BluetoothDiscoveryProbe();

        cut.setState("state");
        Assert.assertEquals(cut.toString(), "{\"state\": " + "state" +
                '}');

    }

}