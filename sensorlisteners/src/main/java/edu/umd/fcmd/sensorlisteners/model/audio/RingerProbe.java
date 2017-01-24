package edu.umd.fcmd.sensorlisteners.model.audio;

import edu.umd.fcmd.sensorlisteners.model.Probe;

/**
 * Created by MMueller on 1/24/2017.
 *
 * Model class for the mode
 */
public class RingerProbe extends Probe {
    public static final String NORMAL = "NORMAL";
    public static final String SILENT = "SILENT";
    public static final String VIBRATE = "VIBRATE";

    private static final String RINGER_TYPE = "Ringer";
    private String mode;

    /**
     * Gets the mode property.
     *
     * @return mode.
     */
    String getMode() {
        return mode;
    }

    /**
     * Sets the mode property.
     */
    public void setMode(String mode) {
        this.mode = mode;
    }

    /**
     * Gets the type of an state e.g. Accelerometer
     *
     * @return the type of state.
     */
    @Override
    public String getType() {
        return RINGER_TYPE;
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
        return "{\"mode\": " + mode +
                '}';
    }
}
