package org.fraunhofer.cese.funf_sensor.backend.apis;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.Result;
import com.googlecode.objectify.cmd.Saver;

import org.fraunhofer.cese.funf_sensor.backend.OfyService;
import org.fraunhofer.cese.funf_sensor.backend.models.SensorDataSet;
import org.fraunhofer.cese.funf_sensor.backend.models.SensorEntry;
import org.fraunhofer.cese.funf_sensor.backend.models.UploadResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Tested;

import static org.junit.Assert.*;

/**
 * Created by Lucas on 9/28/2015.
 */
public class SensorDataSetEndpointTest {

    @Tested SensorDataSetEndpoint endpoint;

    @org.junit.Test
    public void testInsertSensorDataSet(@Mocked final SensorDataSet mockDataSet, @Mocked final SensorEntry entry1, @Mocked final SensorEntry entry2, @Mocked final Objectify mockOfy, @Mocked final Map<Key<SensorEntry>, SensorEntry> entities) throws Exception {

        new MockUp<OfyService>() {
            @Mock
            public Objectify ofy() {
                return mockOfy;
            }
        };


        new NonStrictExpectations() {{
            entry1.getProbeType(); result = "funf.accelerometer";
            entry1.getSensorData(); result = "this.is.not.real.data";
            entry1.getTimestamp(); result = new Date(1234567);

            entry2.getProbeType(); result = "funf.callprobe";
            entry2.getSensorData(); result = "status:offhook";
            entry2.getTimestamp(); result = new Date(2234567);

            List<SensorEntry> entryList = Arrays.asList(entry1, entry2);
            mockDataSet.getTimestamp(); result = new Date(555555);
            mockDataSet.getEntryList(); result = entryList;

            entities.size(); result = 2;
            mockOfy.save().entities(entryList).now(); result = entities;
        }};

        UploadResult uploadResult = endpoint.insertSensorDataSet(mockDataSet);
        assertEquals(new Integer(2), uploadResult.getSize());
        assertNotNull(uploadResult.getTimestamp());
    }
}