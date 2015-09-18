package org.fraunhofer.cese.funf_sensor.backend.models;




import com.google.appengine.api.users.User;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * UserAccount entity.
 */
@Entity
public class UserAccount {

    /**
     * Unique identifier of this Entity in the database.
     */
    @Id
    private Long key;

    /**
     * The user first name.
     */
    private String firstName;

    /**
     * The user last name.
     */
    private String lastName;

    /**
     * The user email.
     */
    private String email;

    /**
     * Returns a boolean indicating if the user is an admin or not.
     * @param user to check.
     * @return the user authorization level.
     */
    public static boolean isAdmin(final User user) {
        return false;
    }

    /**
     * Returns the user first name.
     * @return the user first name
     */
    public final String getFirstName() {
        return firstName;
    }

    /**
     * Sets the user first name.
     * @param pFirstName the first name to set for this user.
     */
    public final void setFirstName(final String pFirstName) {
        this.firstName = pFirstName;
    }

    /**
     * Returns the user last name.
     * @return the user last name.
     */
    public final String getName() {
        return lastName;
    }

    /**
     * Sets the user last name.
     * @param pLastName the user last name to set.
     */
    public final void setName(final String pLastName) {
        this.lastName = pLastName;
    }

    /**
     * Returns the user email.
     * @return the user email.
     */
    public final String getEmail() {
        return email;
    }

    /**
     * Sets the user email.
     * @param pEmail the user email to set.
     */
    public final void setEmail(final String pEmail) {
        this.email = pEmail;
    }

}
