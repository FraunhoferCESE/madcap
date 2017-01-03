package edu.umd.fcmd.sensorlisteners.model.system;

import edu.umd.fcmd.sensorlisteners.model.Probe;

/**
 * Created by MMueller on 1/3/2017.
 *
 * Model class for a change change by the user.
 * E.g. user sets the time from 1 am to 4 am.
 */
public class TimeChangedProbe extends Probe {
    public static final String TIMEADJUST = "TIME_ADJUSTED";

    private String change;

    /**
     * Gets the change.
     * @return the change.
     */
    public String getChange() {
        return change;
    }

    /**
     * Sets the change.
     * @param change the change to be set.
     */
    public void setChange(String change) {
        this.change = change;
    }

    /**
     * Gets the type of an state e.g. Accelerometer
     *
     * @return the type of state.
     */
    @Override
    public String getType() {
        return "TimeChange";
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
        return "{\"change\": " + (change != null ? change : "-") +
                '}';
    }
}
