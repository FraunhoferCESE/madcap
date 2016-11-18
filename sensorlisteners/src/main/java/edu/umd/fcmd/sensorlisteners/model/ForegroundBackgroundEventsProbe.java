package edu.umd.fcmd.sensorlisteners.model;

/**
 * Created by MMueller on 11/18/2016.
 */

public class ForegroundBackgroundEventsProbe extends Probe{
    private static final String TYPE = "ForegroundBackgroundEvent";
    private int eventType;
    private String packageName;
    private String className;
    private double accuracy;

    /**
     * Gets the Event Type (now in Background, now in Foreground,
     * etc) .
     *
     * @return Type.
     */
    public int getEventType() {
        return eventType;
    }

    /**
     * Sets the Event Type (now in Background, now in Foreground,
     * etc) .
     */
    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    /**
     * Gets the PackageName.
     *
     * @return PackageName.
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Sets the PackageName.
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * Gets the ClassName.
     *
     * @return ClassName.
     */
    public String getClassName() {
        return className;
    }

    /**
     * Sets the ClassName.
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Gets the Accuracy. Accuracy differs between API levels
     * because the methods how to get the events are different.
     *
     * @return Accuracy.
     */
    public double getAccuracy() {
        return accuracy;
    }

    /**
     * Sets the Accuracy.
     */
    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    /**
     * Gets the type of an state e.g. Accelerometer
     *
     * @return the type of state.
     */
    @Override
    public String getType() {
        return TYPE;
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
        return "{\"eventType\": " + eventType +
                ", \"packageName\": " + packageName +
                ", \"accuracy\": " + accuracy +
                '}';
    }
}
