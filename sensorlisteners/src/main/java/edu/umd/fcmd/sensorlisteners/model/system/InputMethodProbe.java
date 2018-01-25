package edu.umd.fcmd.sensorlisteners.model.system;

import edu.umd.fcmd.sensorlisteners.model.Probe;

/**
 * Created by MMueller on 1/4/2017.
 *
 * Model class for input methods (keyboard english, keyboard german,
 * voice to text etc.)
 */
public class InputMethodProbe extends Probe {
    private String method;

    /**
     * Gets the method.
     * @return the method.
     */
    public String getMethod() {
        return method;
    }

    /**
     * Sets the method.
     * @param method to set to.
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * Gets the type of an method e.g. Accelerometer
     *
     * @return the type of method.
     */
    @Override
    public String getType() {
        return "InputMethod";
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
        return "{\"method\": " + "\""+(method != null? method : "-")+"\""+
                '}';
    }
}
