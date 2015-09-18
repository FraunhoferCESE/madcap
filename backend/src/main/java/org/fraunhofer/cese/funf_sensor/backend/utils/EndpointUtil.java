package org.fraunhofer.cese.funf_sensor.backend.utils;

import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.users.User;
import org.fraunhofer.cese.funf_sensor.backend.models.UserAccount;

/**
 *
 */


public final class EndpointUtil {

    /**
     * Default constructor, never called.
     */
    private EndpointUtil() {
    }

    /**
     * Throws an exception if the user is not an admin.
     * @param user User object to be checked if it represents an admin.
     * @throws com.google.api.server.spi.response.UnauthorizedException when the
     *      user object does not represent an admin.
     */
    public static void throwIfNotAdmin(final User user) throws
            UnauthorizedException {
        if (!UserAccount.isAdmin(user)) {
            throw new UnauthorizedException(
                    "You are not authorized to perform this operation");
        }
    }

    /**
     * Throws an exception if the user object doesn't represent an authenticated
     * call.
     * @param user User object to be checked if it represents an authenticated
     *      caller.
     * @throws com.google.api.server.spi.response.UnauthorizedException when the
     *      user object does not represent an admin.
     */
    public static void throwIfNotAuthenticated(final User user) throws
            UnauthorizedException {
        if (user == null || user.getEmail() == null) {
            throw new UnauthorizedException(
                    "Only authenticated users may invoke this operation");
        }
    }


}
