package org.fraunhofer.cese.madcap.backend.models;

/**
 * Created by MMueller on 11/8/2016.
 */

public interface DatastoreEntry {

    String getId();
    void setId(String s);

    Long getTimestamp();
    void setTimestamp(Long l);

    String getUserID();
    void setUserID(String s);
}
