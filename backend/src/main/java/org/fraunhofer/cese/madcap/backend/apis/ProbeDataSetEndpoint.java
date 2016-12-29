package org.fraunhofer.cese.madcap.backend.apis;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.ConflictException;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.repackaged.com.google.protobuf.MapEntry;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.cmd.LoadType;

import org.fraunhofer.cese.madcap.backend.models.AccelerometerEntry;
import org.fraunhofer.cese.madcap.backend.models.ActivityEntry;
import org.fraunhofer.cese.madcap.backend.models.BluetoothConnectionEntry;
import org.fraunhofer.cese.madcap.backend.models.BluetoothDiscoveryEntry;
import org.fraunhofer.cese.madcap.backend.models.BluetoothRequestEntry;
import org.fraunhofer.cese.madcap.backend.models.BluetoothScanModeEntry;
import org.fraunhofer.cese.madcap.backend.models.BluetoothStateEntry;
import org.fraunhofer.cese.madcap.backend.models.BluetoothStaticAtributesEntry;
import org.fraunhofer.cese.madcap.backend.models.CallStateEntry;
import org.fraunhofer.cese.madcap.backend.models.CellEntry;
import org.fraunhofer.cese.madcap.backend.models.CellLocationEntry;
import org.fraunhofer.cese.madcap.backend.models.ChargingEntry;
import org.fraunhofer.cese.madcap.backend.models.DataCollectionEntry;
import org.fraunhofer.cese.madcap.backend.models.DatastoreEntry;
import org.fraunhofer.cese.madcap.backend.models.ForegroundBackgroundEventEntry;
import org.fraunhofer.cese.madcap.backend.models.LocationEntry;
import org.fraunhofer.cese.madcap.backend.models.LocationServiceEntry;
import org.fraunhofer.cese.madcap.backend.models.MSMSEntry;
import org.fraunhofer.cese.madcap.backend.models.NetworkEntry;
import org.fraunhofer.cese.madcap.backend.models.PowerEntry;
import org.fraunhofer.cese.madcap.backend.models.ProbeDataSet;
import org.fraunhofer.cese.madcap.backend.models.ProbeEntry;
import org.fraunhofer.cese.madcap.backend.models.ProbeSaveResult;
import org.fraunhofer.cese.madcap.backend.models.TelecomServiceEntry;
import org.fraunhofer.cese.madcap.backend.models.WiFiEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import static org.fraunhofer.cese.madcap.backend.OfyService.ofy;

/**
 * An endpoint class we are exposing
 */
@SuppressWarnings("ResourceParameter")
@Api(
        name = "probeEndpoint",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "madcap.cese.fraunhofer.org",
                ownerName = "madcap.cese.fraunhofer.org",
                packagePath = "backend"
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
    @ApiMethod(
            name = "insertProbeDataset"
    )
    public ProbeSaveResult insertSensorDataSet(HttpServletRequest req, ProbeDataSet probeDataSet) throws OAuthRequestException, ConflictException, BadRequestException {

        long startTime = System.currentTimeMillis();
        logger.info("Upload request received from " + req.getRemoteAddr());

        // Check if data gets passed at all
        if (probeDataSet == null) {
            throw new BadRequestException("sensorDataSet cannot be null");
        }

        Collection<ProbeEntry> entryList = probeDataSet.getEntryList();

        // Check if passed list has no entries
        if (entryList == null || entryList.isEmpty()) {
            throw new BadRequestException("entryList is null or empty");
        }

        logger.fine("Logging request received. Data: " + entryList);
        logger.info("Number of entries received: " + entryList.size() + ", Request size: " + humanReadableByteCount(Long.parseLong(req.getHeader("Content-Length")), false));


        Collection<String> saved = new ArrayList<>();
        Collection<String> alreadyExists = new ArrayList<>();

        Collection<ProbeEntry> toSave = new ArrayList<>();

        Collection<String> uploadedIds = new ArrayList<>();
//        for (ProbeEntry entry : entryList) {
//            uploadedIds.add(entry.getId());
//        }

        //==============

        //A map from the probe type to all entries from that type in the current set
        Map<String, Collection> entryMap = new HashMap<>();

        for (ProbeEntry entry : entryList) {
            switch (entry.getProbeType()) {
                case "Location":
                    if (!entryMap.containsKey(entry.getProbeType())) {
                        entryMap.put(entry.getProbeType(), new ArrayList<LocationEntry>());
                    }
                    Collection<LocationEntry> llist = entryMap.get(entry.getProbeType());
                    LocationEntry locationEntry = new LocationEntry(entry);
                    llist.add(locationEntry);
                    break;
                case "Accelerometer":
                    if (!entryMap.containsKey(entry.getProbeType())) {
                        entryMap.put(entry.getProbeType(), new ArrayList<AccelerometerEntry>());
                    }
                    Collection<AccelerometerEntry> alist = entryMap.get(entry.getProbeType());
                    AccelerometerEntry accelerometerEntry = new AccelerometerEntry(entry);
                    alist.add(accelerometerEntry);
                    break;
                case "LocationService":
                    if (!entryMap.containsKey(entry.getProbeType())) {
                        entryMap.put(entry.getProbeType(), new ArrayList<LocationServiceEntry>());
                    }
                    Collection<LocationServiceEntry> blist = entryMap.get(entry.getProbeType());
                    LocationServiceEntry locationServiceEntry = new LocationServiceEntry(entry);
                    blist.add(locationServiceEntry);
                    break;
                case "ForegroundBackgroundEvent":
                    if (!entryMap.containsKey(entry.getProbeType())) {
                        entryMap.put(entry.getProbeType(), new ArrayList<ForegroundBackgroundEventEntry>());
                    }
                    Collection<ForegroundBackgroundEventEntry> clist = entryMap.get(entry.getProbeType());
                    ForegroundBackgroundEventEntry foregroundBackgroundEventEntry = new ForegroundBackgroundEventEntry(entry);
                    clist.add(foregroundBackgroundEventEntry);
                    break;
                case "BluetoothState":
                    if (!entryMap.containsKey(entry.getProbeType())) {
                        entryMap.put(entry.getProbeType(), new ArrayList<BluetoothStateEntry>());
                    }
                    Collection<BluetoothStateEntry> btlist = entryMap.get(entry.getProbeType());
                    BluetoothStateEntry bluetoothStateEntry = new BluetoothStateEntry(entry);
                    btlist.add(bluetoothStateEntry);
                    break;
                case "BluetoothStaticAttributes":
                    if (!entryMap.containsKey(entry.getProbeType())) {
                        entryMap.put(entry.getProbeType(), new ArrayList<BluetoothStaticAtributesEntry>());
                    }
                    Collection<BluetoothStaticAtributesEntry> bsalist = entryMap.get(entry.getProbeType());
                    BluetoothStaticAtributesEntry bluetoothStaticAtributesEntry = new BluetoothStaticAtributesEntry(entry);
                    bsalist.add(bluetoothStaticAtributesEntry);
                    break;
                case "BluetoothDiscoveryProbe":
                    if (!entryMap.containsKey(entry.getProbeType())) {
                        entryMap.put(entry.getProbeType(), new ArrayList<BluetoothDiscoveryEntry>());
                    }
                    Collection<BluetoothDiscoveryEntry> bsdlist = entryMap.get(entry.getProbeType());
                    BluetoothDiscoveryEntry bluetoothDiscoveryEntry = new BluetoothDiscoveryEntry(entry);
                    bsdlist.add(bluetoothDiscoveryEntry);
                    break;
                case "BluetoothConnection":
                    if (!entryMap.containsKey(entry.getProbeType())) {
                        entryMap.put(entry.getProbeType(), new ArrayList<BluetoothConnectionEntry>());
                    }
                    Collection<BluetoothConnectionEntry> bcelist = entryMap.get(entry.getProbeType());
                    BluetoothConnectionEntry bluetoothConnectionEntry = new BluetoothConnectionEntry(entry);
                    bcelist.add(bluetoothConnectionEntry);
                    break;
                case "Bluetoothrequest":
                    if (!entryMap.containsKey(entry.getProbeType())) {
                        entryMap.put(entry.getProbeType(), new ArrayList<BluetoothRequestEntry>());
                    }
                    Collection<BluetoothRequestEntry> brlist = entryMap.get(entry.getProbeType());
                    BluetoothRequestEntry bluetoothRequestEntry = new BluetoothRequestEntry(entry);
                    brlist.add(bluetoothRequestEntry);
                    break;
                case "BluetoothScanMode":
                    if (!entryMap.containsKey(entry.getProbeType())) {
                        entryMap.put(entry.getProbeType(), new ArrayList<BluetoothScanModeEntry>());
                    }
                    Collection<BluetoothScanModeEntry> bsmlist = entryMap.get(entry.getProbeType());
                    BluetoothScanModeEntry bluetoothScanModeEntry = new BluetoothScanModeEntry(entry);
                    bsmlist.add(bluetoothScanModeEntry);
                    break;
                case "Activity":
                    if (!entryMap.containsKey(entry.getProbeType())) {
                        entryMap.put(entry.getProbeType(), new ArrayList<ActivityEntry>());
                    }
                    Collection<ActivityEntry> aclist = entryMap.get(entry.getProbeType());
                    ActivityEntry activityEntry = new ActivityEntry(entry);
                    aclist.add(activityEntry);
                    break;
                case "Power":
                    if (!entryMap.containsKey(entry.getProbeType())) {
                        entryMap.put(entry.getProbeType(), new ArrayList<PowerEntry>());
                    }
                    Collection<PowerEntry> powlist = entryMap.get(entry.getProbeType());
                    PowerEntry powerEntry = new PowerEntry(entry);
                    powlist.add(powerEntry);
                    break;
                case "Charging":
                    if (!entryMap.containsKey(entry.getProbeType())) {
                        entryMap.put(entry.getProbeType(), new ArrayList<ChargingEntry>());
                    }
                    Collection<ChargingEntry> charlist = entryMap.get(entry.getProbeType());
                    ChargingEntry chargingEntry = new ChargingEntry(entry);
                    charlist.add(chargingEntry);
                    break;
                case "DataCollection":
                    if (!entryMap.containsKey(entry.getProbeType())) {
                        entryMap.put(entry.getProbeType(), new ArrayList<DataCollectionEntry>());
                    }
                    Collection<DataCollectionEntry> dalist = entryMap.get(entry.getProbeType());
                    DataCollectionEntry dataCollectionEntry = new DataCollectionEntry(entry);
                    dalist.add(dataCollectionEntry);
                    break;
                case "CallState":
                    if (!entryMap.containsKey(entry.getProbeType())) {
                        entryMap.put(entry.getProbeType(), new ArrayList<CallStateEntry>());
                    }
                    Collection<CallStateEntry> cslist = entryMap.get(entry.getProbeType());
                    CallStateEntry callStateEntry = new CallStateEntry(entry);
                    cslist.add(callStateEntry);
                    break;
                case "CellLocation":
                    if (!entryMap.containsKey(entry.getProbeType())) {
                        entryMap.put(entry.getProbeType(), new ArrayList<CellLocationEntry>());
                    }
                    Collection<CellLocationEntry> cllist = entryMap.get(entry.getProbeType());
                    CellLocationEntry cellLocationEntry = new CellLocationEntry(entry);
                    cllist.add(cellLocationEntry);
                    break;
                case "Cellular":
                    if (!entryMap.containsKey(entry.getProbeType())) {
                        entryMap.put(entry.getProbeType(), new ArrayList<CellEntry>());
                    }
                    Collection<CellEntry> celist = entryMap.get(entry.getProbeType());
                    CellEntry cellEntry = new CellEntry(entry);
                    celist.add(cellEntry);
                    break;
                case "Network":
                    if (!entryMap.containsKey(entry.getProbeType())) {
                        entryMap.put(entry.getProbeType(), new ArrayList<NetworkEntry>());
                    }
                    Collection<NetworkEntry> nelist = entryMap.get(entry.getProbeType());
                    NetworkEntry networkEntry = new NetworkEntry(entry);
                    nelist.add(networkEntry);
                    break;
                case "TelecomService":
                    if (!entryMap.containsKey(entry.getProbeType())) {
                        entryMap.put(entry.getProbeType(), new ArrayList<TelecomServiceEntry>());
                    }
                    Collection<TelecomServiceEntry> tslist = entryMap.get(entry.getProbeType());
                    TelecomServiceEntry telecomServiceEntry = new TelecomServiceEntry(entry);
                    tslist.add(telecomServiceEntry);
                    break;
                case "WiFi":
                    if (!entryMap.containsKey(entry.getProbeType())) {
                        entryMap.put(entry.getProbeType(), new ArrayList<WiFiEntry>());
                    }
                    Collection<WiFiEntry> wflist = entryMap.get(entry.getProbeType());
                    WiFiEntry wiFiEntry = new WiFiEntry(entry);
                    wflist.add(wiFiEntry);
                    break;
                case "MSMS":
                    if (!entryMap.containsKey(entry.getProbeType())) {
                        entryMap.put(entry.getProbeType(), new ArrayList<MSMSEntry>());
                    }
                    Collection<MSMSEntry> mslist = entryMap.get(entry.getProbeType());
                    MSMSEntry msmsEntry = new MSMSEntry(entry);
                    mslist.add(msmsEntry);
                    break;
                default:
                    throw new IllegalArgumentException("Unmateched Probe Type");
            }
        }

        Set<String> keySet = entryMap.keySet();

        for (String key : keySet) {
            Collection<DatastoreEntry> currentEntrySet = entryMap.get(key);
            Collection<DatastoreEntry> currentToSave = new ArrayList<>();

            Class type = (currentEntrySet.toArray())[0].getClass();
            for (DatastoreEntry entry : currentEntrySet) {
                uploadedIds.add(entry.getId());
            }
            Objectify of = ofy();
            LoadType loadType = of.load().type(type);

            Map alreadyUploadedIds = loadType.ids(uploadedIds);

            for (DatastoreEntry entry : currentEntrySet) {
                if (alreadyUploadedIds.get(entry.getId()) == null) {
                    saved.add(entry.getId());
                    currentToSave.add(entry);
                } else {
                    alreadyExists.add(entry.getId());
                }
            }

            ofy().save().entities(currentToSave).now();
            ofy().clear();
        }


//        Map<String, LocationEntry> locationIds = ofy().load().type(LocationEntry.class).ids(uploadedIds);
//        ids.add(locationIds);
//        Map<String, AccelerometerEntry> accelerometerIds = ofy().load().type(AccelerometerEntry.class).ids(uploadedIds);
//        ids.add(accelerometerIds);
//
//        Collection<LocationEntry> toSaveLocation = new ArrayList<>();
//        Collection<AccelerometerEntry> toSaveAccelerometer = new ArrayList<>();
//
//        for (ProbeEntry entry : entryList) {
//            String type = entry.getProbeType();
//            if (ids.get(entry.getId()) == null) {
//                saved.add(entry.getId());
//                switch (type) {
//                    case "Location":
//                        toSaveLocation.add(new LocationEntry(entry));
//                        break;
//                    case "Accelerometer":
//                        toSaveAccelerometer.add(new);
//                }
//
//            } else {
//                alreadyExists.add(entry.getId());
//            }
//
//        }

//        ofy().save().entities(toSaveLocation).now();
//        ofy().clear();
//        ofy().save().entities(toSaveAccelerometer).now();
//        ofy().clear();

        // ===============


//        Map<String, ProbeEntry> ids = ofy().load().type(ProbeEntry.class).ids(uploadedIds);
//        for (ProbeEntry entry : entryList) {
//            if (ids.get(entry.getId()) == null) {
//                saved.add(entry.getId());
//                toSave.add(entry);
//            } else {
//                alreadyExists.add(entry.getId());
//            }
//        }
//        ofy().save().entities(toSave).now();
//        ofy().clear();

        logger.info("Num Saved: " + saved.size() + ", Num already existing: " + alreadyExists.size() + ", Time taken: " + ((System.currentTimeMillis() - startTime) / 1000) + "s");
        return ProbeSaveResult.create(saved, alreadyExists);
    }


    /**
     * Displays raw byte counts (e.g., 1024) in human readable format (e.g., 1.0 KiB).
     * <p/>
     * From <a href="http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java">http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java</a>
     *
     * @param bytes size in bytes
     * @param si    use si units or not
     * @return a human readable string of the byte size
     */
    @SuppressWarnings("NonReproducibleMathCall")
    private static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format(Locale.ENGLISH, "%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}