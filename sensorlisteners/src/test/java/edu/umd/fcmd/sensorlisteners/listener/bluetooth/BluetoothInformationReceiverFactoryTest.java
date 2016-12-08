package edu.umd.fcmd.sensorlisteners.listener.bluetooth;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by MMueller on 12/8/2016.
 */
public class BluetoothInformationReceiverFactoryTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void create() throws Exception {
        BluetoothListener mockBluetoothListener = mock(BluetoothListener.class);

        BluetoothInformationReceiverFactory cut = new BluetoothInformationReceiverFactory();

        cut.create(mockBluetoothListener);
    }

}