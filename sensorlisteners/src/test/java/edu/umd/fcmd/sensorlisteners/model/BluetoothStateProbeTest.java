package edu.umd.fcmd.sensorlisteners.model;

import android.bluetooth.BluetoothAdapter;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by MMueller on 12/7/2016.
 */
public class BluetoothStateProbeTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void getState() throws Exception {
        BluetoothStateProbe cut = new BluetoothStateProbe();

        cut.setState(1);

        Assert.assertEquals(cut.getState(), 1);
    }

    @Test
    public void setState() throws Exception {
        BluetoothStateProbe cut = new BluetoothStateProbe();

        cut.setState(1);

        Assert.assertEquals(cut.getState(), 1);
    }

    @Test
    public void getType() throws Exception {
        BluetoothStateProbe cut = new BluetoothStateProbe();

        Assert.assertEquals(cut.getType(), "BluetoothState");
    }

    @Test
    public void testToString() throws Exception {
        BluetoothStateProbe cut = new BluetoothStateProbe();

        cut.setState(BluetoothAdapter.STATE_OFF);
        Assert.assertEquals(cut.toString(), "{\"state\": " + "OFF" +
                '}');

        cut.setState(BluetoothAdapter.STATE_TURNING_OFF);
        Assert.assertEquals(cut.toString(), "{\"state\": " + "TURNING_OFF" +
                '}');

        cut.setState(BluetoothAdapter.STATE_ON);
        Assert.assertEquals(cut.toString(), "{\"state\": " + "ON" +
                '}');

        cut.setState(BluetoothAdapter.STATE_TURNING_ON);
        Assert.assertEquals(cut.toString(), "{\"state\": " + "TURNING_ON" +
                '}');

        cut.setState(200000);
        Assert.assertEquals(cut.toString(), "{\"state\": " + "INVALID" +
                '}');
    }

}