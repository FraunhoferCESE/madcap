package org.fraunhofer.cese.madcap.backend;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

import org.fraunhofer.cese.madcap.backend.models.AccelerometerEntry;
import org.fraunhofer.cese.madcap.backend.models.LocationEntry;
import org.fraunhofer.cese.madcap.backend.models.ProbeDataSet;
import org.fraunhofer.cese.madcap.backend.models.ProbeEntry;


/**
 *
 */
@SuppressWarnings({"UtilityClass", "UtilityClassCanBeEnum", "NonFinalUtilityClass", "UtilityClassWithoutPrivateConstructor"})
public class OfyService {
    static {
        ObjectifyService.register(ProbeDataSet.class);
        ObjectifyService.register(ProbeEntry.class);
        ObjectifyService.register(LocationEntry.class);
        ObjectifyService.register(AccelerometerEntry.class);
    }

    /**
     * Returns the Objectify service wrapper.
     * @return The Objectify service wrapper.
     */
    @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
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