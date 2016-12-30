package edu.umd.fcmd.sensorlisteners.model.system;

import edu.umd.fcmd.sensorlisteners.model.Probe;

/**
 * Created by MMueller on 12/30/2016.
 *
 * Model class for the screen.
 * Tells if it is on or off and possible it's brightness.
 */
public class ScreenProbe extends Probe {
    public static final String ON = "ON";
    public static final String OFF = "OFF";

    private String state;
    private double brightness;

    /**
     * Gets the state.
     * @return either ON of OFF.
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the state.
     * @param state to be either ON or OFF.
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Gets the screen's brightness.
     * @return the brightness.
     */
    public double getBrightness() {
        return brightness;
    }

    /**
     * Sets the screens brightness
     * @param brightness to be in range [0,100]
     */
    public void setBrightness(double brightness) {
        this.brightness = brightness;
    }

    /**
     * Gets the type of an state e.g. Accelerometer
     *
     * @return the type of state.
     */
    @Override
    public String getType() {
        return "Screen";
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
                ", \"brightness\": " + brightness +
                '}';
    }
}
