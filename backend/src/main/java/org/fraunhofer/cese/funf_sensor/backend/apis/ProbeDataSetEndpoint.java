package org.fraunhofer.cese.funf_sensor.backend.apis;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ConflictException;

import org.fraunhofer.cese.funf_sensor.backend.models.ProbeDataSet;
import org.fraunhofer.cese.funf_sensor.backend.models.ProbeEntry;
import org.fraunhofer.cese.funf_sensor.backend.models.ProbeSaveResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import static org.fraunhofer.cese.funf_sensor.backend.OfyService.ofy;

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
    public ProbeSaveResult insertSensorDataSet(ProbeDataSet probeDataSet) throws ConflictException, BadRequestException {
        if (probeDataSet == null) {
            throw new BadRequestException("sensorDataSet cannot be null");
        }

        Collection<ProbeEntry> entryList = probeDataSet.getEntryList();
        if (entryList == null || entryList.isEmpty()) {
            throw new BadRequestException("entryList is null or empty");
        }
        logger.info("Logging request received. Data: " + entryList);

        Collection<String> saved = new ArrayList<>();
        Collection<String> alreadyExists = new ArrayList<>();

        for(ProbeEntry entry : entryList) {
            ProbeEntry result = ofy().load().type(ProbeEntry.class).id(entry.getId()).now();
            if(result == null) {
                saved.add(entry.getId());
            }
            else {
                alreadyExists.add(result.getId());
            }
        }

        logger.info("Num Saved: " + saved.size() +", Num already existing: "+alreadyExists.size());
        return ProbeSaveResult.create(saved, alreadyExists);
    }
}