package org.fraunhofer.cese.funf_senseor.backend.models;

import com.google.appengine.repackaged.com.google.api.client.util.DateTime;
import com.googlecode.objectify.annotation.Entity;

/**
 * the result of a single measurement of any kind of device sensor.
 * Currently you can not assign a particular ID to a SensorDataSet.
 * The current constructor provides consecutive IDs.
 *
 * @param id            the unique ID of a SensorDataSet
 * @param timestamp     the moment a SensorDataSet was captured
 * @param probeType     information about what sensor the data was captured by as a String
 * @param sensorData    String containing the sensor information captured
 */

@Entity
public class SensorDataSet {

    //attributes
    @Id
    private long id;
    private DateTime timestamp;
    private String probeType;       //maybe we should use an enum here
    private String sensorData;

    private static long lastID;

    //getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(DateTime timestamp) {
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

    //Object methods
    @Override
    public String toString() {
        return "Dataset-No.: " + id + "  Captured on: " + timestamp.toString() +
                "  Type of probe: " + probeType + "  Dataset: " + sensorData;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == this)
            return true;

        if (!(obj instanceof SensorDataSet))
            return false;

        SensorDataSet object = (SensorDataSet) obj;

        return this.id == object.id && this.timestamp.equals(object.getTimestamp())
                && this.probeType.equals(object.getProbeType())
                && this.sensorData.equals(object.getSensorData());
    }

    @Override
    public int hashCode(){

        int hashCode=17;

        hashCode=31*hashCode+(int)(this.id^(this.id>>>32));
        hashCode=31*hashCode+this.timestamp.hashCode();
        hashCode=31*hashCode+this.probeType.hashCode();
        hashCode=31*hashCode+this.sensorData.hashCode();


        return hashCode;
    }

    //Constructor(s)

    /**
     * !!This constructor assigns consecutive IDs to SensorDataSets!!
     * They can not be assigned manually.
     *
     */
    public SensorDataSet(DateTime timestamp, String probeType, String sensorData){

        this.timestamp=timestamp;
        this.probeType=probeType;
        this.sensorData=sensorData;

        lastID++;
        this.id=lastID;
    }


}