package edu.umd.fcmd.sensorlisteners.model.bluetoothB;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by MMueller on 12/7/2016.
 */
public class BluetoothConnectionProbeTest {

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void getState() throws Exception {
        BluetoothConnectionProbe cut = new BluetoothConnectionProbe();

        cut.setState("A");

        Assert.assertEquals(cut.getState(), "A");
    }

    @Test
    public void setState() throws Exception {
        BluetoothConnectionProbe cut = new BluetoothConnectionProbe();

        cut.setState("A");

        Assert.assertEquals(cut.getState(), "A");
    }

    @Test
    public void getForeignAddress() throws Exception {
        BluetoothConnectionProbe cut = new BluetoothConnectionProbe();

        cut.setForeignName("name");

        Assert.assertEquals(cut.getForeignName(), "name");
    }

    @Test
    public void setForeignAddress() throws Exception {
        BluetoothConnectionProbe cut = new BluetoothConnectionProbe();

        cut.setForeignAddress("name");

        Assert.assertEquals(cut.getForeignAddress(), "name");
    }

    @Test
    public void getForeignName() throws Exception {
        BluetoothConnectionProbe cut = new BluetoothConnectionProbe();

        cut.setForeignAddress("name");

        Assert.assertEquals(cut.getForeignAddress(), "name");
    }

    @Test
    public void setForeignName() throws Exception {
        BluetoothConnectionProbe cut = new BluetoothConnectionProbe();

        cut.setForeignAddress("name");

        Assert.assertEquals(cut.getForeignAddress(), "name");
    }

    @Test
    public void getType() throws Exception {
        BluetoothConnectionProbe cut = new BluetoothConnectionProbe();

        Assert.assertEquals(cut.getType(), "BluetoothConnection");
    }

    @Test
    public void testToString() throws Exception {
        BluetoothConnectionProbe cut = new BluetoothConnectionProbe();

        cut.setState("state");
        cut.setForeignAddress("foreignAddress");
        cut.setForeignName("foreignName");

        Assert.assertEquals(cut.toString(), "{\"state\": " + "state" +
                ", \"foreignName\": " + "foreignName" +
                ", \"foreignAddress\": " + "\""+"foreignAddress"+"\"" +
                '}');

    }

}