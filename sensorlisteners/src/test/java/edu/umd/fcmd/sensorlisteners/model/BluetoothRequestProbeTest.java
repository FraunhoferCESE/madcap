package edu.umd.fcmd.sensorlisteners.model;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by MMueller on 12/7/2016.
 */
public class BluetoothRequestProbeTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void getKind() throws Exception {
        BluetoothRequestProbe cut = new BluetoothRequestProbe();

        cut.setKind("kind");

        Assert.assertEquals(cut.getKind(), "kind");
    }

    @Test
    public void setKind() throws Exception {
        BluetoothRequestProbe cut = new BluetoothRequestProbe();

        cut.setKind("kind");

        Assert.assertEquals(cut.getKind(), "kind");
    }

    @Test
    public void getType() throws Exception {
        BluetoothRequestProbe cut = new BluetoothRequestProbe();

        Assert.assertEquals(cut.getType(), "BluetoothRequest");
    }

    @Test
    public void testToString() throws Exception {
        BluetoothRequestProbe cut = new BluetoothRequestProbe();

        cut.setKind("kind");

        Assert.assertEquals(cut.toString(), "{\"kind\": " + "kind" +
                '}');
    }

}