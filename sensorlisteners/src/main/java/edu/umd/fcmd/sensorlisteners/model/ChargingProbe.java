package edu.umd.fcmd.sensorlisteners.model;

import edu.umd.fcmd.sensorlisteners.model.Probe;

/**
 * Created by MMueller on 12/12/2016.
 *
 * Model class showing the the device is now charging.
 */
public class ChargingProbe extends Probe {
    public static final String USB = "USB";
    public static final String AC = "AC";
    public static final String NONE = "-";

    private String charging = NONE;

    /**
     * Gets the charging method.
     * @return the charging.
     */
    public String getCharging() {
        return charging;
    }

    /**
     * Sets the charging method
     * @param charging to set to.
     */
    public void setCharging(String charging) {
        if(charging == null){
            this.charging = NONE;
        }
        this.charging = charging;
    }

    /**
     * Gets the type of an state e.g. Accelerometer
     *
     * @return the type of state.
     */
    @Override
    public String getType() {
        return "Charging";
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
        return "{\"charging\": " + charging +
                '}';
    }
}
