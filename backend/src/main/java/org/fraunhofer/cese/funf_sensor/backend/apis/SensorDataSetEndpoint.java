package org.fraunhofer.cese.funf_sensor.backend.apis;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ConflictException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Result;


import static org.fraunhofer.cese.funf_sensor.backend.OfyService.ofy;

import org.fraunhofer.cese.funf_sensor.backend.models.SensorDataSet;
import org.fraunhofer.cese.funf_sensor.backend.models.SensorEntry;
import org.fraunhofer.cese.funf_sensor.backend.models.UploadResult;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Named;

/**
 * An endpoint class we are exposing
 */
@Api(
        name = "sensorDataSetApi",
        version = "v1",
        resource = "sensorDataSet",
        namespace = @ApiNamespace(
                ownerDomain = "models.backend.funf_sensor.cese.fraunhofer.org",
                ownerName = "models.backend.funf_sensor.cese.fraunhofer.org",
                packagePath = ""
        )
)
public class SensorDataSetEndpoint {

    private static final Logger logger = Logger.getLogger(SensorDataSetEndpoint.class.getName());

//    /**
//     * This method gets the <code>SensorDataSet</code> object associated with the specified <code>id</code>.
//     *
//     * @param id The id of the object to be returned.
//     * @return The <code>SensorDataSet</code> associated with <code>id</code>.
//     */
//    @ApiMethod(name = "getSensorDataSet")
//    public SensorDataSet getSensorDataSet(@Named("id") Long id) {
//        // TODO: Implement this function
//        logger.info("Calling getSensorDataSet method");
//        return ofy().load().type(SensorDataSet.class).id(id).now();
//    }

    /**
     * This inserts a new <code>SensorDataSet</code> object.
     *
     * @param sensorDataSet The object to be added.
     * @return The object to be added.
     */
    @ApiMethod(name = "insertSensorDataSet")
    public UploadResult insertSensorDataSet(SensorDataSet sensorDataSet) throws ConflictException, BadRequestException {
        if (sensorDataSet == null) {
            throw new BadRequestException("sensorDataSet cannot be null");
        }

        List<SensorEntry> entryList = sensorDataSet.getEntryList();
        if (entryList == null || entryList.isEmpty()) {
            throw new BadRequestException("entryList is null or empty");
        }

        UploadResult result = UploadResult.create(ofy().save().entities(entryList).now());
        ofy().save().entity(result).now();
        return result;
    }
}