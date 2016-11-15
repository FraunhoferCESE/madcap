package edu.umd.fcmd.sensorlisteners.model;

import com.google.android.gms.common.ConnectionResult;

/**
 * Created by MMueller on 11/14/2016.
 */

public class LocationServiceStatusState extends State {
    private static final String ON = "ON";
    private static final String OFF = "OFF";
    private static final String LOCATION_SERVICE_TYPE = "LocationService";
    private ConnectionResult googleConnectionResult;
    private String locationServiceStatus;

    /**
     * Gets the connectionResult.
     *
     * @return connectionResult.
     */
    public ConnectionResult getGoogleConnectionResult() {
        return googleConnectionResult;
    }

    /**
     * Sets the connectionResult.
     */
    public void setGoogleConnectionResult(ConnectionResult googleConnectionResult) {
        this.googleConnectionResult = googleConnectionResult;
    }

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
    public void setLocationServiceStatus(String locationServiceStatus) {
        this.locationServiceStatus = locationServiceStatus;
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
                ", \"GoogleApiClientConnectionResult\": " + googleConnectionResult.getErrorCode() +
                '}';
    }
}
