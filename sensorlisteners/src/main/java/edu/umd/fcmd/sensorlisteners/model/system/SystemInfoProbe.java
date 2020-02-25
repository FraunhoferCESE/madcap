package edu.umd.fcmd.sensorlisteners.model.system;

import edu.umd.fcmd.sensorlisteners.model.Probe;
import timber.log.Timber;

/**
 * Created by MMueller on 12/30/2016.
 *
 * Model class for the screen.
 * Tells if it is on or off and possible it's brightness.
 */
public class SystemInfoProbe extends Probe {
    private String manufacturer;
    private String model;
    private double apiLevel;
    private String madcapVersion;
    private String messageToken;

    /**
     * Gets the device manufacturer.
     * E.g. Samsung
     * @return manufacturer.
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * Sets the device manufacturer.
     * E.g. Samsung
     * @param manufacturer to be set.
     */
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    /**
     * Get the model description.
     * E.g. Galaxy S7
     * @return model/
     */
    public String getModel() {
        return model;
    }

    /**
     * Set the model description.
     * E.g. Galaxy S7
     * @param model to be set.
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Gets the api level.
     * @return api level.
     */
    public double getApiLevel() {
        return apiLevel;
    }

    /**
     * Sets the api level.
     * @param apiLevel
     */
    public void setApiLevel(double apiLevel) {
        this.apiLevel = apiLevel;
    }

    /**
     * Gets the current MadcapVersion.
     * @return the madcap version.
     */
    public String getMadcapVersion() {
        return madcapVersion;
    }

    /**
     * Sets the current MadcapVersion.
     * @param madcapVersion to be set.
     */
    public void setMadcapVersion(String madcapVersion) {
        this.madcapVersion = madcapVersion;
    }

    /**
     * Gets the current message token.
     * @return the message token.
     */
    public String getMessageToken() {
        return messageToken;
    }

    /**
     * Sets the current message token.
     * @param messageToken to be set.
     */
    public void setMessageToken(String messageToken) {
        this.messageToken = messageToken;
    }

    /**
     * Gets the type of an state e.g. Accelerometer
     *
     * @return the type of state.
     */
    @Override
    public String getType() {
        return "SystemInfo";
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
        Timber.d("System Entry To String: " + "{\"manufacturer\": " + (manufacturer!=null? manufacturer : "-") +
                ", \"model\": " + (model!=null? model : "-") +
                ", \"apiLevel\": " + apiLevel +
                ", \"madcapVersion\": " + (madcapVersion!=null? madcapVersion : "-") +
                ", \"messageToken\": \"" + messageToken + "\"" +
                '}');
        return "{\"manufacturer\": " + (manufacturer!=null? manufacturer : "-") +
                ", \"model\": " + (model!=null? model : "-") +
                ", \"apiLevel\": " + apiLevel +
                ", \"madcapVersion\": " + (madcapVersion!=null? madcapVersion : "-") +
                ", \"messageToken\": \"" + messageToken + "\"" +
                '}';
    }
}
