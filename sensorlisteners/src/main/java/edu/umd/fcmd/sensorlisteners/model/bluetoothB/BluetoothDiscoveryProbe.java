package edu.umd.fcmd.sensorlisteners.model.bluetoothB;

import edu.umd.fcmd.sensorlisteners.model.Probe;

/**
 * Created by MMueller on 12/6/2016.
 *
 * Model class for the discovery function of a bluetooth device.
 */
public class BluetoothDiscoveryProbe extends Probe {
    private String state;

    /**
     * Getter for the state. Typically something like
     * started/finished.
     *
     * @return the state.
     */
    public String getState() {
        return state;
    }

    /**
     * Setter for the state.
     * @param state to set to.
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
        return "BluetoothDiscoveryProbe";
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