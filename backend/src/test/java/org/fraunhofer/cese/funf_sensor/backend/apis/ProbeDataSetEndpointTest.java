package org.fraunhofer.cese.funf_sensor.backend.apis;

import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ConflictException;
import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

import org.fraunhofer.cese.funf_sensor.backend.OfyService;
import org.fraunhofer.cese.funf_sensor.backend.models.ProbeDataSet;
import org.fraunhofer.cese.funf_sensor.backend.models.ProbeEntry;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Tested;
import mockit.Verifications;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Lucas on 9/28/2015.
 */
public class ProbeDataSetEndpointTest {

    @Tested
    ProbeDataSetEndpoint endpoint;

    @Test
    public void testInsertSensorDataSet(@Mocked final ProbeDataSet mockDataSet, @Mocked final ProbeEntry entry1, @Mocked final ProbeEntry entry2, @Mocked final Objectify mockOfy, @Mocked final Map<Key<ProbeEntry>, ProbeEntry> entities, @Mocked final HttpServletRequest req, @Mocked final User user) throws Exception {

        new MockUp<OfyService>() {
            @Mock
            public Objectify ofy() {
                return mockOfy;
            }
        };


        new NonStrictExpectations() {{
            entry1.getId(); result = "123-4567-889";
            entry1.getProbeType(); result = "funf.accelerometer";
            entry1.getSensorData(); result = "this.is.not.real.data";
            entry1.getTimestamp(); result = 1234567;

            entry2.getId(); result = "098130-31";
            entry2.getProbeType(); result = "funf.callprobe";
            entry2.getSensorData(); result = "status:offhook";
            entry2.getTimestamp(); result = 2234567;

            List<ProbeEntry> entryList = Arrays.asList(entry1, entry2);
            mockDataSet.getTimestamp(); result = 555555;
            mockDataSet.getEntryList(); result = entryList;

            entities.size(); result = 2;
            mockOfy.save().entities(entryList).now(); result = entities;

            req.getRemoteAddr(); result = "1.1.1.1";
            user.getUserId(); result= "unitTestUser";
        }};

        final Collection<ProbeEntry> result = endpoint.insertSensorDataSet(mockDataSet);

//        new Verifications() {{
//            assertEquals(2, result.size());
//            mockOfy.save().entity(any); maxTimes = 1;
//        }};
    }

    @Test(expected = BadRequestException.class)
    public void testInsertNullDataSet() throws ConflictException, BadRequestException {
        endpoint.insertSensorDataSet(null);
    }

    @Test(expected = BadRequestException.class)
    public void testInsertNullEntryList(@Mocked final ProbeDataSet mockDataSet) throws ConflictException, BadRequestException {

        new Expectations() {{
            mockDataSet.getEntryList(); result = null;
        }};

        endpoint.insertSensorDataSet(mockDataSet);
    }

    @Test(expected = BadRequestException.class)
    public void testInsertEmptyEntryList(@Mocked final ProbeDataSet mockDataSet) throws ConflictException, BadRequestException {

        new Expectations() {{
            mockDataSet.getEntryList(); result = new ArrayList<>();
        }};

        endpoint.insertSensorDataSet(mockDataSet);
    }


}