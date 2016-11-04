package edu.umd.fcmd.sensorlisteners.model;

import android.os.Bundle;

/**
 * Created by MMueller on 11/4/2016.
 */

public class LocationState extends State {
    private float accuracy;
    private double altitude;private float bearing;
    private Bundle extras;
    private double latitude;
    private double longitude;

    /**
     * Gets the Accuracy.
     * @return Accuracy.
     */
    public float getAccuracy() {
        return accuracy;
    }

    /**
     * Sets the Accuracy.
     */
    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    /**
     * Gets the Altitude.
     * @return Altitude.
     */
    public double getAltitude() {
        return altitude;
    }

    /**
     * Sets the Altitude.
     */
    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    /**
     * Gets the Bearing.
     * @return bearing.
     */
    public float getBearing() {
        return bearing;
    }

    /**
     * Sets the Bearing.
     */
    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    /**
     * Gets the Extras.
     * @return extras as a bundle.
     */
    public Bundle getExtras() {
        return extras;
    }

    /**
     * Sets the Extras.
     */
    public void setExtras(Bundle extras) {
        this.extras = extras;
    }

    /**
     * Gets the Latitude.
     * @return Latitude.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Gets the latitude
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets the Longitude.
     * @return Longitude.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets the Longitude.
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets the type of an state e.g. Location
     *
     * @return the type of state.
     */
    @Override
    public String getType() {
        return "Location";
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
        return "{\"latitude\": "+latitude+"\"longitude\": "+longitude+"\"altitude\": "+altitude+"\"accuracy\": "+accuracy+"\"bearing\": "+bearing+
                "\"extras\": "+extras.toString()+"}";
    }
}
