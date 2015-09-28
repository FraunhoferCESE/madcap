package org.fraunhofer.cese.funf_sensor.backend.models;


import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
public class SensorDataSet implements Serializable {

    //attributes
    @Id
    private Long id;
    private List<SensorEntry> entryList;
    private Date timestamp;


    //getters and setters


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date date) {
        this.timestamp = date;
    }

    public void setEntryList(List<SensorEntry> entryList) {
        this.entryList = entryList;
    }

    public List<SensorEntry> getEntryList() {
        return entryList;
    }
}