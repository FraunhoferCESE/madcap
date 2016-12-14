package edu.umd.fcmd.sensorlisteners.model;

/**
 * Created by MMueller on 12/6/2016.
 *
 * Model class representing requests by other apps to
 * invoke methods with the Bluetooth.
 */
public class BluetoothRequestProbe extends Probe {
    private String kind;

    /**
     * Gets what kind of request it is.
     * @return the kind.
     */
    public String getKind() {
        return kind;
    }

    /**
     * Sets what kind it is.
     * Should be
     * ENABLE or
     * DISCOVERABle
     * @param kind
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
        return "BluetoothRequest";
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
        return "{\"kind\": " + kind +
                '}';
    }
}
