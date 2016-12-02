package edu.umd.fcmd.sensorlisteners.model;

/**
 * Created by MMueller on 12/2/2016.
 * <p>
 *  Model class for a Network state.
 *  Has Status ON/OFF, SSID and Security Level.
 */

public class NetworkProbe extends Probe {
    private static final String NETWORK_TYPE = "Network";
    private String status;
    private String ssid;
    private String securityLevel;

    /**
     * Gets the status (ON/OFF).
     *
     * @return the status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status (ON/OFF).
     */
     public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the SSID.
     *
     * @return the SSID.
     */
    public String getSsid() {
        return ssid;
    }

    /**
     * Sets the SSID.
     */
    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    /**
     * Gets the SecurityLevel.
     *
     * @return the SecurityLevel.
     */
    public String getSecurityLevel() {
        return securityLevel;
    }

    /**
     * Sets the SecurityLevel.
     */
    public void setSecurityLevel(String securityLevel) {
        this.securityLevel = securityLevel;
    }

    /**
     * Gets the type of an state e.g. Accelerometer
     *
     * @return the type of state.
     */
    @Override
    public String getType() {
        return NETWORK_TYPE;
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
        return "{\"status\": " + (status != null ? status : "-") +
                ", \"ssid\": " + (ssid != null ? ssid : "-") +
                ", \"securityLevel\": " + (securityLevel != null ? securityLevel : "-") +
                '}';
    }
}
