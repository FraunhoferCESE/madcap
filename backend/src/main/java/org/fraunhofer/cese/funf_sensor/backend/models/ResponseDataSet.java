package org.fraunhofer.cese.funf_sensor.backend.models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.io.Serializable;
import java.util.List;

/**
 *
 */
@Entity
public class ResponseDataSet implements Serializable{

    @Id
    private Long key;
    private List<ProbeEntry> content;
    private int numberOfEntries;
    private Long timestamp;
    private int remainingEntries;
    private static final int CHUNK_SIZE = 1000;


    public static int getCHUNK_SIZE() {
        return CHUNK_SIZE;
    }

    public Long getTimestampOfLastEntry() {
        return timestampOfLastEntry;
    }

    public void setTimestampOfLastEntry(Long timestampOfLastEntry) {
        this.timestampOfLastEntry = timestampOfLastEntry;
    }

    private Long timestampOfLastEntry;

    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }

    public ResponseDataSet() {
    }

    public List<ProbeEntry> getContent() {
        return content;
    }

    public int getNumberOfEntries() {
        return numberOfEntries;
    }

    public void setNumberOfEntries(int numberOfEntries) {
        this.numberOfEntries = numberOfEntries;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public int getRemainingEntries() {
        return remainingEntries;
    }

    public void setRemainingEntries(int remainingEntries) {
        this.remainingEntries = remainingEntries;
    }

    public void setContent(List<ProbeEntry> content) {
        this.content = content;
    }

}