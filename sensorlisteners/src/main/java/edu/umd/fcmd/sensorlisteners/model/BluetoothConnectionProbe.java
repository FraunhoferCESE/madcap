package edu.umd.fcmd.sensorlisteners.model;

/**
 * Created by MMueller on 12/6/2016.
 *
 * Probe modelling a bluetooth connection.
 */

public class BluetoothConnectionProbe extends Probe {
    private String state;
    private String foreignAddress;
    private String foreignName;

    /**
     * Getter for the state.
     * @return the state.
     */
    public String getState() {
        return state;
    }

    /**
     * Setter for the state.
     * @param state the state to set to.
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Getter for the connected devices address.
     * @return the connected devices address.
     */
    public String getForeignAddress() {
        return foreignAddress;
    }

    /**
     * Setter for the connected devices address.
     * @param foreignAddress the address to set to.
     */
    public void setForeignAddress(String foreignAddress) {
        this.foreignAddress = foreignAddress;
    }

    /**
     * Getter for the connected devices name.
     * @return the connected devices name.
     */
    public String getForeignName() {
        return foreignName;
    }

    /**
     * Setter for the connected devices name.
     * @param foreignName the name to set to.
     */
    public void setForeignName(String foreignName) {
        this.foreignName = foreignName;
    }

    /**
     * Gets the type of an state e.g. Accelerometer
     *
     * @return the type of state.
     */
    @Override
    public String getType() {
        return "BluetoothConnection";
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
                ", \"foreignName\": " + foreignName +
                ", \"foreignAddress\": " + "\""+(foreignAddress != null ? foreignAddress : "-") +"\"" +
                '}';
    }
}
