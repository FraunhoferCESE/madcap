package edu.umd.fcmd.sensorlisteners.model;

import android.os.Bundle;

/**
 * Created by MMueller on 11/4/2016.
 * <p>
 * Model class for a Location state.
 * Has acurracy, altitiude, bearing, longitue and latitude as double.
 * Extras is a bundle and differs from manufacturer to manufacturer.
 * Most of the time it is null.
 */

public class LocationProbe extends Probe {
    private static final String LOCATION_TYPE = "Location";
    private double accuracy;
    private double altitude;
    private double bearing;
    private double speed;
    private Bundle extras;
    private double latitude;
    private double longitude;
    private String origin;

    /**
     * Gets the Accuracy.
     *
     * @return Accuracy.
     */
    final double getAccuracy() {
        return accuracy;
    }

    /**
     * Sets the Accuracy.
     */
    public final void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    /**
     * Gets the Altitude.
     *
     * @return Altitude.
     */
    final double getAltitude() {
        return altitude;
    }

    /**
     * Sets the Altitude.
     */
    public final void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    /**
     * Gets the Bearing.
     *
     * @return bearing.
     */
    final double getBearing() {
        return bearing;
    }

    /**
     * Sets the Bearing.
     */
    public final void setBearing(double bearing) {
        this.bearing = bearing;
    }

    /**
     * Gets the Extras.
     *
     * @return extras as a bundle.
     */
    final Bundle getExtras() {
        return extras;
    }

    /**
     * Sets the Extras.
     */
    public final void setExtras(Bundle extras) {
        this.extras = extras;
    }

    /**
     * Gets the Latitude.
     *
     * @return Latitude.
     */
    final double getLatitude() {
        return latitude;
    }

    /**
     * Gets the latitude
     */
    public final void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets the Longitude.
     *
     * @return Longitude.
     */
    final double getLongitude() {
        return longitude;
    }

    /**
     * Sets the Longitude.
     */
    public final void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Getter for the speed.
     * @return the speed.
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Setter for the speed.
     * @param speed the speed to be set to.
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * Gets the origin provider of the location data
     * @return origin.
     */
    public String getOrigin() {
        return origin;
    }

    /**
     * Set the origin provider of the data.
     * @param origin to be set to.
     */
    public void setOrigin(String origin) {
        this.origin = origin;
    }

    /**
     * Gets the type of an state e.g. Location
     *
     * @return the type of state.
     */
    @Override
    public final String getType() {
        return LOCATION_TYPE;
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
    public final String toString() {
        return "{\"latitude\": " + latitude +
                ", \"longitude\": " + longitude +
                ", \"altitude\": " + altitude +
                ", \"accuracy\": " + accuracy +
                ", \"bearing\": " + bearing +
                ", \"speed\": " + speed +
                ", \"origin\": " + origin +
                ", \"extras\": " + (extras != null ? "\""+extras.toString()+"\"" : "-") +
                '}';
    }
}
