package org.fraunhofer.cese.funf_sensor.backend.models;

import com.google.appengine.api.users.User;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.Date;

@Entity
public class UploadResult {

    @Id
    private Long id;
    private Date timestamp;
    private Integer size;
    private String remoteAddr;
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

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

    public static UploadResult create(int size, String remoteAddr, User user) {
        UploadResult result = new UploadResult();
        result.setSize(size);
        result.setTimestamp(new Date());
        result.setUser(user);
        result.setRemoteAddr(remoteAddr);
        return result;
    }

    @Override
    public String toString() {
        return "UploadResult{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", size=" + size +
                ", remoteAddr='" + remoteAddr + '\'' +
                ", user=" + user +
                '}';
    }
}
