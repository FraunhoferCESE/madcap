package org.fraunhofer.cese.funf_sensor.backend.apis;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ConflictException;
import com.google.appengine.api.users.User;


import static org.fraunhofer.cese.funf_sensor.backend.OfyService.ofy;

import org.fraunhofer.cese.funf_sensor.backend.models.ProbeDataSet;
import org.fraunhofer.cese.funf_sensor.backend.models.ProbeEntry;
import org.fraunhofer.cese.funf_sensor.backend.models.UploadResult;

import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

/**
 * An endpoint class we are exposing
 */
@Api(
        name = "probeDataSetApi",
        version = "v1",
        resource = "probeDataSet",
        namespace = @ApiNamespace(
                ownerDomain = "models.backend.funf_sensor.cese.fraunhofer.org",
                ownerName = "models.backend.funf_sensor.cese.fraunhofer.org",
                packagePath = ""
        )
)
public class ProbeDataSetEndpoint {

    private static final Logger logger = Logger.getLogger(ProbeDataSetEndpoint.class.getName());

    /**
     * This inserts a new <code>ProbeDataSet</code> object.
     *
     * @param probeDataSet The object to be added.
     * @return The object to be added.
     */
    @ApiMethod(name = "insertSensorDataSet")
    public UploadResult insertSensorDataSet(HttpServletRequest req, User user, ProbeDataSet probeDataSet) throws ConflictException, BadRequestException {
        if (probeDataSet == null) {
            throw new BadRequestException("sensorDataSet cannot be null");
        }

        List<ProbeEntry> entryList = probeDataSet.getEntryList();
        if (entryList == null || entryList.isEmpty()) {
            throw new BadRequestException("entryList is null or empty");
        }

        UploadResult result = UploadResult.create(ofy().save().entities(entryList).now().size(), req.getRemoteAddr(), user);
        logger.info(result.toString());
        ofy().save().entity(result).now();
        return result;
    }
}