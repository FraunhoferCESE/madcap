package edu.umd.fcmd.sensorlisteners.model.Bluetooth;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.umd.fcmd.sensorlisteners.model.Bluetooth.BluetoothStaticAttributesProbe;

/**
 * Created by MMueller on 12/7/2016.
 */
public class BluetoothStaticAttributesProbeTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void getName() throws Exception {
        BluetoothStaticAttributesProbe cut = new BluetoothStaticAttributesProbe();

        cut.setName("name");

        Assert.assertEquals(cut.getName(), "name");
    }

    @Test
    public void setName() throws Exception {
        BluetoothStaticAttributesProbe cut = new BluetoothStaticAttributesProbe();

        cut.setName("name");

        Assert.assertEquals(cut.getName(), "name");
    }

    @Test
    public void getAddress() throws Exception {
        BluetoothStaticAttributesProbe cut = new BluetoothStaticAttributesProbe();

        cut.setAddress("address");

        Assert.assertEquals(cut.getAddress(), "address");
    }

    @Test
    public void setAddress() throws Exception {
        BluetoothStaticAttributesProbe cut = new BluetoothStaticAttributesProbe();

        cut.setAddress("address");

        Assert.assertEquals(cut.getAddress(), "address");
    }

    @Test
    public void getType() throws Exception {
        BluetoothStaticAttributesProbe cut = new BluetoothStaticAttributesProbe();

        Assert.assertEquals(cut.getType(), "BluetoothStaticAttributes");
    }

    @Test
    public void testToString() throws Exception {
        BluetoothStaticAttributesProbe cut = new BluetoothStaticAttributesProbe();

        cut.setName("name");
        cut.setAddress("address");

        Assert.assertEquals(cut.toString(),"{\"name\": " + "name" +
                ", \"address\": " + "\"" + "address" + "\"" +
                '}' );

    }

}