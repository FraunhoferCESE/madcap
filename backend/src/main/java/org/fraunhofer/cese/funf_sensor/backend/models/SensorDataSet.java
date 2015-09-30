package org.fraunhofer.cese.funf_sensor.backend.models;


import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.sun.org.apache.xpath.internal.operations.Plus;

import java.io.Serializable;
import java.util.Date;

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
//@Entity
public class SensorDataSet implements Serializable{

    //attributes
    @Id
    private Long id;                //maybe it will be assigned automatically
    private Date timestamp;
    private String probeType;       //maybe we should use an enum here
    private String sensorData;
    private String accountName;



    //getters and setters
    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date date) {
        this.timestamp = date;
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
//    public SensorDataSet(DateTime timestamp, String probeType, String sensorData){
//
//        this.timestamp=timestamp;
//        this.probeType=probeType;
//        this.sensorData=sensorData;
//        this.accountName= Plus.AccountApi.getAccountName(mGoogleApiClient);
//
//
//    }

}