package edu.umd.fcmd.sensorlisteners.model.network;

import edu.umd.fcmd.sensorlisteners.model.Probe;

/**
 * Created by MMueller on 12/23/2016.
 */

public class CellLocationProbe extends Probe {
    private String cellType;
    private String areaCode;
    private double lat;
    private double lng;

    /**
     * Gets the type. E.g. GSM
     * @return the type
     */
    public String getCellType() {
        return cellType;
    }

    /**
     * Sets the type. E.g. gsm.
     * @param cellType the type to be set.
     */
    public void setCellType(String cellType) {
        this.cellType = cellType;
    }

    /**
     * Gets the area code.
     * @return area code.
     */
    public String getAreaCode() {
        return areaCode;
    }

    /**
     * Sets the area Code.
     * @param areaCode to be set.
     */
    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    /**
     * Gets the latitude.
     * @return lat.
     */
    public double getLat() {
        return lat;
    }

    /**
     * Sets the latitude.
     * @param lat to be set.
     */
    public void setLat(double lat) {
        this.lat = lat;
    }

    /**
     * Sets the longitude.
     * @return longitude.
     */
    public double getLng() {
        return lng;
    }

    /**
     * Setter for longitude.
     * @param lng the longitude.
     */
    public void setLng(double lng) {
        this.lng = lng;
    }

    /**
     * Gets the type of an state e.g. Accelerometer
     *
     * @return the type of state.
     */
    @Override
    public String getType() {
        return "CellLocation";
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
        return "{\"cellType\": " + (cellType != null ? cellType : "-") +
                ", \"areaCode\": " + (areaCode != null ? areaCode : "-") +
                ", \"lat\": " + lat +
                ", \"lng\": " + lng +
                '}';
    }
}
