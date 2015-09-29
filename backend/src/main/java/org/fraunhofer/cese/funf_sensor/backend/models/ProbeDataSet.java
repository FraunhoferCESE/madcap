package org.fraunhofer.cese.funf_sensor.backend.models;


import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
public class ProbeDataSet implements Serializable {

    //attributes
    @Id
    private Long id;
    private List<ProbeEntry> entryList;
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

    public void setEntryList(List<ProbeEntry> entryList) {
        this.entryList = entryList;
    }

    public List<ProbeEntry> getEntryList() {
        return entryList;
    }
}