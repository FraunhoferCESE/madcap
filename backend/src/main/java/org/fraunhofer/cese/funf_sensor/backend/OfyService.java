package org.fraunhofer.cese.funf_sensor.backend;

import org.fraunhofer.cese.funf_sensor.backend.models.*;
import org.fraunhofer.cese.funf_sensor.backend.models.ResponseDataSet;

import com.googlecode.objectify.*;
/**
 *
 */
public class OfyService {
    static {
        ObjectifyService.register(ProbeDataSet.class);
        ObjectifyService.register(ProbeEntry.class);
        ObjectifyService.register(ResponseDataSet.class);
    }

    /**
     * Returns the Objectify service wrapper.
     * @return The Objectify service wrapper.
     */
    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    /**
     * Returns the Objectify factory service.
     * @return The factory service.
     */
    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}