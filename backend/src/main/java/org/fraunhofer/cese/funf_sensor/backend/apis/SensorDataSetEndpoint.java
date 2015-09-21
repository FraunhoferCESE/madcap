package org.fraunhofer.cese.funf_sensor.backend.apis;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import org.fraunhofer.cese.funf_sensor.backend.models.SensorDataSet;

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

    /**
     * This method gets the <code>SensorDataSet</code> object associated with the specified <code>id</code>.
     *
     * @param id The id of the object to be returned.
     * @return The <code>SensorDataSet</code> associated with <code>id</code>.
     */
    @ApiMethod(name = "getSensorDataSet")
    public SensorDataSet getSensorDataSet(@Named("id") Long id) {
        // TODO: Implement this function
        logger.info("Calling getSensorDataSet method");
        return null;
    }

    /**
     * This inserts a new <code>SensorDataSet</code> object.
     *
     * @param sensorDataSet The object to be added.
     * @return The object to be added.
     */
    @ApiMethod(name = "insertSensorDataSet")
    public SensorDataSet insertSensorDataSet(SensorDataSet sensorDataSet) {
        // TODO: Implement this function
        logger.info("Calling insertSensorDataSet method");
        return sensorDataSet;
    }
}