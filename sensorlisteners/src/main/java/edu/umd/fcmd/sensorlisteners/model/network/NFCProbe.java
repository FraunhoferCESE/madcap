package edu.umd.fcmd.sensorlisteners.model.network;

import edu.umd.fcmd.sensorlisteners.model.Probe;

/**
 * Created by MMueller on 12/2/2016.
 */

public class NFCProbe extends Probe {
    public static final String ON = "ENABLED";
    public static final String OFF = "DISABLED";
    public static final String UNAVAILABLE = "UNAVAILABLE";
    public static final String TURNING_ON = "TURNING_ON";
    public static final String TURNING_OFF = "TURNING_OFF";
    public static final String UNKNOWN = "UNKNOWN";
    public static final String NDEF_DISCOVERY = "NDEF";
    public static final String TECH_DISCOVERY = "TECH";
    public static final String TAG_DISCOVERY = "TAG";
    public static final String TRANSACTION_CONDUCTED = "CONDUCTED";

    private static final String NFC_TYPE = "NFC";
    private String state;
    private String tagDiscoveryState;
    private String transactionConductedState;

    /**
     * Gets the state.
     * @return state.
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the state.
     * @param state to be set.
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Gets the other device found state.
     * @return state.
     */
    public String getTagDiscoveryState() {
        return tagDiscoveryState;
    }

    /**
     * Sets the other device found state.
     * @param state to be set.
     */
    public void setTagDiscoveryState(String state) {
        this.tagDiscoveryState = state;
    }

    /**
     * Gets the transaction conducted state.
     * @return state.
     */
    public String getTransactionConductedState() {
        return transactionConductedState;
    }

    /**
     * Sets the transaction conducted state.
     * @param state to be set.
     */
    public void setTransactionConductedState(String state) {
        this.transactionConductedState = state;
    }

    /**
     * Gets the type of an state e.g. Accelerometer
     *
     * @return the type of state.
     */
    @Override
    public String getType() {
        return NFC_TYPE;
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
        return "{\"state\": " + (state != null ? state : "-") +
                ", \"deviceFoundState\": " + (tagDiscoveryState != null ? tagDiscoveryState : "-") +
                ", \"transactionConductedState\": " + (transactionConductedState != null ? transactionConductedState : "-") +
                '}';
    }
}
