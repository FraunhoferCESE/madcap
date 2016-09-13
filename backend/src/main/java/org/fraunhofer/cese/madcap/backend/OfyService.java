package org.fraunhofer.cese.madcap.backend;

import org.fraunhofer.cese.madcap.backend.models.ProbeDataSet;
import org.fraunhofer.cese.madcap.backend.models.ProbeEntry;

import com.googlecode.objectify.*;
/**
 *
 */
public class OfyService {
    static {
        ObjectifyService.register(ProbeDataSet.class);
        ObjectifyService.register(ProbeEntry.class);
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