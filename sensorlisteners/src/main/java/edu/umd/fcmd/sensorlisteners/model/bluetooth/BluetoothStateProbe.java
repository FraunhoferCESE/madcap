package edu.umd.fcmd.sensorlisteners.model.bluetooth;

import android.bluetooth.BluetoothAdapter;

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

public class BluetoothStateProbe extends Probe {
    private final String TAG = getClass().getSimpleName();
    public static final String OFF = "OFF";
    public static final String ON = "ON";
    public static final String TURNING_OFF = "TURNING_OFF";
    public static final String TURNING_ON = "TURNING_ON";
    public static final String INVALID = "INVALID";

    private int state;

    /**
     * Getter for the state.
     *
     * @return a integer value defined in BleutoothAdapter.class.
     * It is eigther
     * BluetoothAdapter.STATE_OFF
     * BluetoothAdapter.STATE_TURNING_ON,
     * BluetoothAdapter.STATE_ON or
     * BluetoothAdapter.STATE_TURNING_OFF.
     */
    public int getState() {
        return state;
    }

    /**
     * Sets the state.
     *
     * @param state the state to set.
     * Should be eigther
     * BluetoothAdapter.STATE_OFF
     * BluetoothAdapter.STATE_TURNING_ON,
     * BluetoothAdapter.STATE_ON or
     * BluetoothAdapter.STATE_TURNING_OFF.
     */
    public void setState(int state) {
        this.state = state;
    }

    /**
     * Gets the type of an state e.g. Accelerometer
     *
     * @return the type of state.
     */
    @Override
    public String getType() {
        return "BluetoothState";
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
        return "{\"state\": " + stateToString(state) +
                '}';
    }

    /**
     * Converts the int states according to BluetoothAdapter constants
     * to a human readable string for the DataStore.
     *
     * @param state the state.
     * @return a readable string representing the state.
     */
    private String stateToString(int state){
        String s;

        switch (state){
            case BluetoothAdapter.STATE_OFF:
                s = OFF;
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                s = TURNING_OFF;
                break;
            case BluetoothAdapter.STATE_TURNING_ON:
                s = TURNING_ON;
                break;
            case BluetoothAdapter.STATE_ON:
                s = ON;
                break;
            default:
                s = INVALID;
                break;
        }

        return s;
    }
}
