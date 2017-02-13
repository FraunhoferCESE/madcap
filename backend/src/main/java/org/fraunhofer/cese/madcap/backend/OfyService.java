package org.fraunhofer.cese.madcap.backend;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

import org.fraunhofer.cese.madcap.backend.models.AccelerometerEntry;
import org.fraunhofer.cese.madcap.backend.models.ActivityEntry;
import org.fraunhofer.cese.madcap.backend.models.AirplaneModeEntry;
import org.fraunhofer.cese.madcap.backend.models.AppUser;
import org.fraunhofer.cese.madcap.backend.models.BluetoothConnectionEntry;
import org.fraunhofer.cese.madcap.backend.models.BluetoothRequestEntry;
import org.fraunhofer.cese.madcap.backend.models.BluetoothScanModeEntry;
import org.fraunhofer.cese.madcap.backend.models.BluetoothStateEntry;
import org.fraunhofer.cese.madcap.backend.models.BluetoothStaticAtributesEntry;
import org.fraunhofer.cese.madcap.backend.models.CallStateEntry;
import org.fraunhofer.cese.madcap.backend.models.CellEntry;
import org.fraunhofer.cese.madcap.backend.models.CellLocationEntry;
import org.fraunhofer.cese.madcap.backend.models.ChargingEntry;
import org.fraunhofer.cese.madcap.backend.models.DataCollectionEntry;
import org.fraunhofer.cese.madcap.backend.models.DockStateEntry;
import org.fraunhofer.cese.madcap.backend.models.DreamingModeEntry;
import org.fraunhofer.cese.madcap.backend.models.ForegroundBackgroundEventEntry;
import org.fraunhofer.cese.madcap.backend.models.HeadphoneEntry;
import org.fraunhofer.cese.madcap.backend.models.InputMethodEntry;
import org.fraunhofer.cese.madcap.backend.models.LocationEntry;
import org.fraunhofer.cese.madcap.backend.models.LocationServiceEntry;
import org.fraunhofer.cese.madcap.backend.models.LogOutEntry;
import org.fraunhofer.cese.madcap.backend.models.MSMSEntry;
import org.fraunhofer.cese.madcap.backend.models.NFCEntry;
import org.fraunhofer.cese.madcap.backend.models.NetworkEntry;
import org.fraunhofer.cese.madcap.backend.models.PowerEntry;
import org.fraunhofer.cese.madcap.backend.models.ProbeDataSet;
import org.fraunhofer.cese.madcap.backend.models.ProbeEntry;
import org.fraunhofer.cese.madcap.backend.models.ReverseHeartBeatEntry;
import org.fraunhofer.cese.madcap.backend.models.RingerEntry;
import org.fraunhofer.cese.madcap.backend.models.ScreenEntry;
import org.fraunhofer.cese.madcap.backend.models.ScreenOffTimeoutEntry;
import org.fraunhofer.cese.madcap.backend.models.SystemInfoEntry;
import org.fraunhofer.cese.madcap.backend.models.SystemUptimeEntry;
import org.fraunhofer.cese.madcap.backend.models.TimeChangeEntry;
import org.fraunhofer.cese.madcap.backend.models.TimezoneEntry;
import org.fraunhofer.cese.madcap.backend.models.UploadLogEntry;
import org.fraunhofer.cese.madcap.backend.models.UserPresenceEntry;
import org.fraunhofer.cese.madcap.backend.models.TelecomServiceEntry;
import org.fraunhofer.cese.madcap.backend.models.VolumeEntry;
import org.fraunhofer.cese.madcap.backend.models.WiFiEntry;


/**
 *
 */
@SuppressWarnings({"UtilityClass", "UtilityClassCanBeEnum", "NonFinalUtilityClass", "UtilityClassWithoutPrivateConstructor"})
public class OfyService {
    static {
        ObjectifyService.register(AppUser.class);
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
        ObjectifyService.register(AirplaneModeEntry.class);
        ObjectifyService.register(SystemInfoEntry.class);
        ObjectifyService.register(UserPresenceEntry.class);
        ObjectifyService.register(SystemUptimeEntry.class);
        ObjectifyService.register(TimeChangeEntry.class);
        ObjectifyService.register(TimezoneEntry.class);
        ObjectifyService.register(InputMethodEntry.class);
        ObjectifyService.register(DockStateEntry.class);
        ObjectifyService.register(CallStateEntry.class);
        ObjectifyService.register(CellEntry.class);
        ObjectifyService.register(CellLocationEntry.class);
        ObjectifyService.register(TelecomServiceEntry.class);
        ObjectifyService.register(NetworkEntry.class);
        ObjectifyService.register(WiFiEntry.class);
        ObjectifyService.register(HeadphoneEntry.class);
        ObjectifyService.register(RingerEntry.class);
        ObjectifyService.register(VolumeEntry.class);
        ObjectifyService.register(ReverseHeartBeatEntry.class);
        ObjectifyService.register(MSMSEntry.class);
        ObjectifyService.register(UploadLogEntry.class);
        ObjectifyService.register(NFCEntry.class);
        ObjectifyService.register(ScreenOffTimeoutEntry.class);
        ObjectifyService.register(LogOutEntry.class);
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