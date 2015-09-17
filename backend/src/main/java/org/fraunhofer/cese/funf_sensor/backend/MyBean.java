package org.fraunhofer.cese.funf_sensor.backend;

import com.google.appengine.repackaged.com.google.api.client.util.DateTime;

/**
 * The object model for the data we are sending through endpoints
 */
public class MyBean {

    private long id;
    private DateTime timestamp;
    private String value;
    private String key;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}