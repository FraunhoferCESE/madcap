package org.fraunhofer.cese.madcap.backend.models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;


/**
 *
 */
@Entity
public class ProbeEntry implements Comparable<ProbeEntry>{

    @Id
    private String id;
    @Index
    private Long timestamp;
    private String probeType;
    private String sensorData;
    private String userID;

    //getters and setters

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

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

    @Override
    public String toString() {
        return "ProbeEntry{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", probeType='" + probeType + '\'' +
                ", sensorData='" + sensorData + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProbeEntry that = (ProbeEntry) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null)
            return false;
        if (probeType != null ? !probeType.equals(that.probeType) : that.probeType != null)
            return false;
        return sensorData != null ? sensorData.equals(that.sensorData) : that.sensorData == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + (probeType != null ? probeType.hashCode() : 0);
        result = 31 * result + (sensorData != null ? sensorData.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(ProbeEntry other) {
        if(getTimestamp()>other.getTimestamp())
                return 1;
        else if (getTimestamp().equals(other.getTimestamp()))
                return 0;
        else
                return -1;
    }
}
