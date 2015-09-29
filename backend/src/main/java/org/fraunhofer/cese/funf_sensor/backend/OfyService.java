package org.fraunhofer.cese.funf_sensor.backend;

import org.fraunhofer.cese.funf_sensor.backend.models.*;
import com.googlecode.objectify.*;
/**
 *
 */
public class OfyService {
    static {
//        factory().register(ProbeDataSet.class);
//        factory().register(Message.class);
        ObjectifyService.register(ProbeDataSet.class);
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