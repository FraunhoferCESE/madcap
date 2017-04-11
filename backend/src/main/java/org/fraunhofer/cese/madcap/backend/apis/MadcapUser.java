package org.fraunhofer.cese.madcap.backend.apis;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * A container for relevant information regarding a user.
 *
 * @author llayman
 */

@Entity
public class MadcapUser {
    public MadcapUser() {

    }

    @Id
    private String email;

    @Index
    private String userId;

    boolean isAlpha;

    boolean isBeta;

    public String getEmail() {
        return email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isAlpha() {
        return isAlpha;
    }

    public boolean isBeta() {
        return isBeta;
    }

    public MadcapUser(String email, String userId, boolean isAlpha, boolean isBeta) {
        this.email = email;
        this.userId = userId;
        this.isAlpha = isAlpha;
        this.isBeta = isBeta;
    }

}
