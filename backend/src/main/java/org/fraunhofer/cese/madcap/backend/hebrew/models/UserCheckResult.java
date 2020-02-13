package org.fraunhofer.cese.madcap.backend.models;

/**
 * Created by MMueller on 2/17/2017.
 *
 * To be returned after a user gets checked if he signed up for MADCAP.
 */

public class UserCheckResult {
    private final boolean authorized;

    public UserCheckResult(boolean result){
        this.authorized = result;
    }

    public boolean isAuthorized() {
        return authorized;
    }
}
