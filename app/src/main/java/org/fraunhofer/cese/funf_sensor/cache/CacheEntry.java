package org.fraunhofer.cese.funf_sensor.cache;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.fraunhofer.cese.funf_sensor.backend.models.probeDataSetApi.model.ProbeEntry;

/**
 * Created by Lucas on 10/5/2015.
 */
@DatabaseTable(tableName = "probedata")
public class CacheEntry {

    public CacheEntry() { }

    @DatabaseField(id = true)
    private String id;

    @DatabaseField
    private Long timestamp;

    @DatabaseField
    private String probeType;

    @DatabaseField
    private String sensorData;

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

    public static ProbeEntry createProbeEntry(CacheEntry entry) {
        if(entry == null)
            return null;

        ProbeEntry probeEntry = new ProbeEntry();
        probeEntry.setId(entry.getId());
        probeEntry.setTimestamp(entry.getTimestamp());
        probeEntry.setProbeType(entry.getProbeType());
        probeEntry.setSensorData(entry.getSensorData());
        return probeEntry;
    }
}
