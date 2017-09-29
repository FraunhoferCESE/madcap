package edu.umd.fcmd.sensorlisteners.model.bluetooth;

import edu.umd.fcmd.sensorlisteners.model.Probe;

/**
 * Created by MMueller on 12/5/2016.
 *
 * Represents a state of the Bluetooth Adapter.
 * According to
 * BluetoothAdapter.STATE_OFF
 * BluetoothAdapter.STATE_TURNING_ON,
 * BluetoothAdapter.STATE_ON,
 * BluetoothAdapter.STATE_TURNING_OFF.
 */

public class BluetoothScanModeProbe extends Probe {
    public static final String INVISIBLE = "INVISIBLE";
    public static final String CONNECTABLE = "INVISIBLE BUT CONNECTABLE";
    public static final String VISIBLE = "VISIBLE";
    public static final String UNKNOWN = "UNKNOWN";

    private String state;

    /**
     * Getter for the state.
     *
     * @return a State string.
     * It is eigther
     * INVISABLE
     * INVISABLE BUT CONNECTABLE or
     * VISABLE
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the state.
     *
     * @param state the state to set.
     * Should be eigther
     * INVISABLE
     * INVISiBLE BUT CONNECTABLE or
     * VISABLE.
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
        return "BluetoothScanMode";
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
        return "{\"state\": " + state +
                '}';
    }

}

