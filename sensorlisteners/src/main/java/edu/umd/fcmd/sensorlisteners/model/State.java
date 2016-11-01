package edu.umd.fcmd.sensorlisteners.model;

import java.util.Date;

/**
 * Created by ANepaul on 10/28/2016.
 */

public abstract class State {
    private Date date;
    private String userId;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public StringBuilder getBaseData() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\n\tTimestamp:").append(date.getTime())
                .append(",\n\tUser ID: ").append(userId);

        return stringBuilder;
    }

    public abstract String getData();
}
