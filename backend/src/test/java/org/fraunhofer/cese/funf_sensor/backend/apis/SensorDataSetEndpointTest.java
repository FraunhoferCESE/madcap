package org.fraunhofer.cese.funf_sensor.backend.apis;

import com.google.appengine.repackaged.com.google.common.geometry.S2CellId;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

import org.fraunhofer.cese.funf_sensor.backend.models.SensorDataSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.logging.Logger;

import mockit.Mocked;
import mockit.Tested;

import static org.junit.Assert.*;

/**
 * Created by llayman on 9/23/2015.
 */
public class SensorDataSetEndpointTest {

    @Tested SensorDataSetEndpoint endpoint;

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    @Before
    public void setUp() {
        helper.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    public void testGetSensorDataSet() throws Exception {
        fail("Not yet implemented.");
    }

    @Test
    public void testInsertSensorDataSet() throws Exception {
        SensorDataSet testSet = new SensorDataSet();
        testSet.setProbeType("fraunhofer.sensor");
        testSet.setSensorData("this is a long string of sensor data");
        testSet.setTimestamp(new Date(1443040457));

        SensorDataSet sensorDataSet = endpoint.insertSensorDataSet(testSet);
        System.err.println(sensorDataSet);
    }
}