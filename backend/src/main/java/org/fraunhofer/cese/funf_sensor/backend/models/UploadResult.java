package org.fraunhofer.cese.funf_sensor.backend.models;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.Date;
import java.util.Map;

@Entity
public class UploadResult {

    @Id
    private Long id;
    private Date timestamp;
    private Integer size;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }


    public static UploadResult create(Map<Key<SensorEntry>, SensorEntry> saveResults) {
        UploadResult result = new UploadResult();
        result.setSize(saveResults.size());
        result.setTimestamp(new Date());
        return result;
    }
}
