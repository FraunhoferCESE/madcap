package edu.umd.fcmd.sensorlisteners.model.network;

import edu.umd.fcmd.sensorlisteners.model.Probe;

/**
 * Created by MMueller on 12/28/2016.
 *
 * Probe for capturing SMS and MMS events
 */
public class MSMSProbe extends Probe {
    private String action;
    private String extra;

    /**
     * Getter for the action type.
     * @return the action.
     */
    public String getAction() {
        return action;
    }

    /**
     * Setter for the action Type.
     * @param action
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Gets added extra information
     * @return extras.
     */
    public String getExtra() {
        return extra;
    }

    /**
     * Sets extra information.
     * @param extra to add
     */
    public void setExtra(String extra) {
        this.extra = extra;
    }

    /**
     * Gets the type of an state e.g. Accelerometer
     *
     * @return the type of state.
     */
    @Override
    public String getType() {
        return "MSMS";
    }

    /**
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "{\"action\": " + (action != null ? action : "-") +
                ", \"extra\": " + extra +
                '}';
    }
}
