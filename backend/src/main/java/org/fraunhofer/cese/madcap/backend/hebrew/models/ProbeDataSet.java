package org.fraunhofer.cese.madcap.backend.models;


import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Entity
public class ProbeDataSet implements Serializable {

    //attributes
    @Id
    private Long id;
    private Collection<ProbeEntry> entries;
    private Long timestamp;
    private String userId;


    //getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setEntryList(Collection<ProbeEntry> entries) {
        this.entries = entries;
    }

    public Collection<ProbeEntry> getEntryList() {
        return entries;
    }
}