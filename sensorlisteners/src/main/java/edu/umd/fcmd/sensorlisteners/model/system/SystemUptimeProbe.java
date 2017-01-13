package edu.umd.fcmd.sensorlisteners.model.system;

import edu.umd.fcmd.sensorlisteners.model.Probe;

/**
 * Created by MMueller on 1/3/2017.
 *
 * Model class for capturing on boot complete and shutdown events.
 */
public class SystemUptimeProbe extends Probe {
    public static final String SHUTDOWN = "SHUTDOWN";
    public static final String BOOT = "BOOT_COMPLETE";

    private String state;

    /**
     * Gets the state.
     * @return the state.
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the state.
     * @param state the state to be set.
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Gets the type of an state e.g. Accelerometer
     *
     * @return the type of state.
     */
    @Override
    public String getType() {
        return "SystemUptime";
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
        return "{\"state\": " + (state != null? state : "-") +
                '}';
    }
}
