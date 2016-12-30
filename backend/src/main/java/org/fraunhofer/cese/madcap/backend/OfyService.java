package org.fraunhofer.cese.madcap.backend;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

import org.fraunhofer.cese.madcap.backend.models.AccelerometerEntry;
import org.fraunhofer.cese.madcap.backend.models.ActivityEntry;
import org.fraunhofer.cese.madcap.backend.models.BluetoothConnectionEntry;
import org.fraunhofer.cese.madcap.backend.models.BluetoothRequestEntry;
import org.fraunhofer.cese.madcap.backend.models.BluetoothScanModeEntry;
import org.fraunhofer.cese.madcap.backend.models.BluetoothStateEntry;
import org.fraunhofer.cese.madcap.backend.models.BluetoothStaticAtributesEntry;
import org.fraunhofer.cese.madcap.backend.models.ChargingEntry;
import org.fraunhofer.cese.madcap.backend.models.DataCollectionEntry;
import org.fraunhofer.cese.madcap.backend.models.DreamingModeEntry;
import org.fraunhofer.cese.madcap.backend.models.ForegroundBackgroundEventEntry;
import org.fraunhofer.cese.madcap.backend.models.LocationEntry;
import org.fraunhofer.cese.madcap.backend.models.LocationServiceEntry;
import org.fraunhofer.cese.madcap.backend.models.PowerEntry;
import org.fraunhofer.cese.madcap.backend.models.ProbeDataSet;
import org.fraunhofer.cese.madcap.backend.models.ProbeEntry;
import org.fraunhofer.cese.madcap.backend.models.ScreenEntry;

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
        ObjectifyService.register(LocationServiceEntry.class);
        ObjectifyService.register(ForegroundBackgroundEventEntry.class);
        ObjectifyService.register(BluetoothStateEntry.class);
        ObjectifyService.register(BluetoothStaticAtributesEntry.class);
        ObjectifyService.register(BluetoothConnectionEntry.class);
        ObjectifyService.register(BluetoothRequestEntry.class);
        ObjectifyService.register(BluetoothScanModeEntry.class);
        ObjectifyService.register(ActivityEntry.class);
        ObjectifyService.register(PowerEntry.class);
        ObjectifyService.register(ChargingEntry.class);
        ObjectifyService.register(DataCollectionEntry.class);
        ObjectifyService.register(DreamingModeEntry.class);
        ObjectifyService.register(ScreenEntry.class);
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