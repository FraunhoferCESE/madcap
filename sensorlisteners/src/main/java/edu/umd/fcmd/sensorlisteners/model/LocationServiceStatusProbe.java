package edu.umd.fcmd.sensorlisteners.model;

/**
 * Created by MMueller on 11/14/2016.
 *
 * Is indicating if the user turned location status on
 * or off.
 */

public class LocationServiceStatusProbe extends Probe {
    public static final String ON = "ON";
    public static final String OFF = "OFF";
    public static final String NO_PROVIDER = "NO PROVIDER";
    private static final String LOCATION_SERVICE_TYPE = "LocationService";
    private String locationServiceStatus;

    /**
     * Gets the LocationServiceStatus.
     *
     * @return ON or OFF.
     */
    public String getLocationServiceStatus() {
        return locationServiceStatus;
    }

    /**
     * Sets the LocationServiceStatus.
     */
    public void setLocationServiceStatus(String locationSerStatus) {
        locationServiceStatus = locationSerStatus;
    }


    /**
     * Gets the type of an state e.g. Accelerometer
     *
     * @return the type of state.
     */
    @Override
    public String getType() {
        return LOCATION_SERVICE_TYPE;
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
        return "{\"LocationServiceStatus\": " + locationServiceStatus +
                '}';
    }
}
