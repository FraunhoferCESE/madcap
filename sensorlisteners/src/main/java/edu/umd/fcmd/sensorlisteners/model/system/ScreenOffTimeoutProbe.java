package edu.umd.fcmd.sensorlisteners.model.system;

import edu.umd.fcmd.sensorlisteners.model.Probe;

/**
 * Created by MMueller on 12/30/2016.
 *
 * Model Probe class for the dreaming timeout of a phone.
 *
 * A phone is dreaming when it has not been interacted with
 * for a longer time period and the screen turns a little
 * darker.
 */
public class ScreenOffTimeoutProbe extends Probe {
    private int timeout;

    /**
     * Gets the timeout.
     * @return either ON of OFF.
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Sets the timeout.
     * @param timeout to be either ON or OFF.
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * Gets the type of an timeout e.g. Accelerometer
     *
     * @return the type of timeout.
     */
    @Override
    public String getType() {
        return "ScreenOffTimeout";
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
        return "{\"timeout\": " + timeout+
                '}';
    }
}
