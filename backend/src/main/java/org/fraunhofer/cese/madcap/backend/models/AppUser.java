package org.fraunhofer.cese.madcap.backend.models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * A container for all relevant information regarding a user.
 * @author llayman
 */
@Entity
public class AppUser {

    public AppUser() {

    }

    @Id
    private String id;

    private String email;

    boolean isAlpha;

    boolean isBeta;

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public boolean isAlpha() {
        return isAlpha;
    }

    public boolean isBeta() {
        return isBeta;
    }

    public AppUser(String email, String id, boolean isAlpha, boolean isBeta) {
        this.email = email;
        this.id = id;
        this.isAlpha = isAlpha;
        this.isBeta = isBeta;
    }
}