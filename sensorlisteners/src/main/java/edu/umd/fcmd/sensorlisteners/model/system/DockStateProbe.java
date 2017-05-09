package edu.umd.fcmd.sensorlisteners.model.system;

import edu.umd.fcmd.sensorlisteners.model.Probe;

/**
 * Created by MMueller on 1/4/2017.
 *
 * Model class docking state to a car or something else.
 */
public class DockStateProbe extends Probe {
    public static final String DOCKED = "DOCKED";
    public static final String UNDOCKED = "NOT_DOCKED";
    public static final String UNKNOWN = "UNKNOWN";

    public static final String CAR = "CAR";
    public static final String DESK = "DESK";

    private String state;
    private String kind;

    /**
     * Gets the state.
     * @return either DOCKED or UNDOCKED.
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the state.
     * @param state to be either DOCKED or UNDOCKED.
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Gets the kind of the docked aim.
     * @return the kind of the docked enpoint.
     */
    public String getKind() {
        return kind;
    }

    /**
     * Sets the kind of the dock enpoint
     * @param kind to be set.
     */
    public void setKind(String kind) {
        this.kind = kind;
    }

    /**
     * Gets the type of an state e.g. Accelerometer
     *
     * @return the type of state.
     */
    @Override
    public String getType() {
        return "DockState";
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
        return "{\"state\": " + (state != null? state : "-")+
                ", \"kind\": " + "\""+(kind != null ? kind : "-") +"\"" +
                '}';
    }
}
