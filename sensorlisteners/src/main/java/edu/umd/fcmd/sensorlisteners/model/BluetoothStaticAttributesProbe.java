package edu.umd.fcmd.sensorlisteners.model;

/**
 * Created by MMueller on 12/6/2016.
 *
 * Model Class for static attributes of a Bluetooth Adapter.
 */

public class BluetoothStaticAttributesProbe extends Probe {
    private String name;
    private String address;

    /**
     * Getter for the name th user can configure.
     * Something like "Bob's phone"
     * @return the bleutooth device name.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the name.
     * @param name name to be set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the bluetooth Adapter address.
     * @return the address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Setter for the Address.
     * @param address to be set.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the type of an state e.g. Accelerometer
     *
     * @return the type of state.
     */
    @Override
    public String getType() {
        return "BluetoothStaticAttributes";
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
        return "{\"name\": " + name +
                ", \"address\": " + "\"" + address + "\"" +
                '}';
    }
}
