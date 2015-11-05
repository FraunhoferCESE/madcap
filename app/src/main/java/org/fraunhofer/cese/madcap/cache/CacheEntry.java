package org.fraunhofer.cese.madcap.cache;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.fraunhofer.cese.funf_sensor.backend.models.probeDataSetApi.model.ProbeEntry;

/**
 * Local cache entry for Probe data. This holds the same data as ProbeEntry, but this data needs to be duplicated locally within the app
 * since an ORM cannot be built for data types imported from the backend API.
 *
 * @author Lucas
 * @see ProbeEntry
 */
@DatabaseTable(tableName = "probedata")
public class CacheEntry {

    public CacheEntry() { }

    @DatabaseField(id = true)
    private String id;
    static final String ID_FIELD_NAME = "id";

    @DatabaseField
    private Long timestamp;
    static final String TIMESTAMP_FIELD_NAME = "timestamp";

    @DatabaseField
    private String probeType;

    @DatabaseField
    private String sensorData;

    @DatabaseField
    private String userID;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getProbeType() {
        return probeType;
    }

    public void setProbeType(String probeType) {
        this.probeType = probeType;
    }

    public String getSensorData() {
        return sensorData;
    }

    public void setSensorData(String sensorData) {
        this.sensorData = sensorData;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public static ProbeEntry createProbeEntry(CacheEntry entry) {
        if(entry == null)
            return null;

        ProbeEntry probeEntry = new ProbeEntry();
        probeEntry.setId(entry.getId());
        probeEntry.setTimestamp(entry.getTimestamp());
        probeEntry.setProbeType(entry.getProbeType());
        probeEntry.setSensorData(entry.getSensorData());
        probeEntry.setUserID(entry.getUserID());
        return probeEntry;
    }
}
