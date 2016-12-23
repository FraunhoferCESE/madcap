package edu.umd.fcmd.sensorlisteners.model.network;

import edu.umd.fcmd.sensorlisteners.model.Probe;

/**
 * Created by MMueller on 12/2/2016.
 * <p>
 *  Model class for a Network state.
 *  Has Status ON/OFF, SSID and Security Level.
 */

public class WiFiProbe extends Probe {
    private static final String NETWORK_TYPE = "WiFi";
    private String state;
    private String ssid;
    private String networkSecurity;
    private String ip;
    private String networkState;

    /**
     * Gets the state (ON/OFF).
     *
     * @return the state.
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the state (ON/OFF).
     */
     public void setState(String state) {
        this.state = state;
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
    public String getNetworkSecurity() {
        return networkSecurity;
    }

    /**
     * Sets the SecurityLevel.
     */
    public void setNetworkSecurity(String networkSecurity) {
        this.networkSecurity = networkSecurity;
    }

    /**
     * Getter for the Wifi network State.
     * @return the wifi network state.
     */
    public String getNetworkState() {
        return networkState;
    }

    /**
     * Setter for the Wifi network state.
     * @param networkState state to be set to.
     */
    public void setNetworkState(String networkState) {
        this.networkState = networkState;
    }

    /**
     * Gets the Ip address.
     * @return Ip address.
     */
    public String getIp() {
        return ip;
    }

    /**
     * Sets the ip address.
     * @param ip address.
     */
    public void setIp(String ip) {
        this.ip = ip;
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
        return "{\"state\": " + (state != null ? state : "-") +
                ", \"ssid\": " + (ssid != null ? ssid : "-") +
                ", \"networkSecurity\": " + (networkSecurity != null ? "\""+networkSecurity+"\"" : "-") +
                ", \"ip\": " + (ip != null ? "\""+ip+"\"" : "-") +
                ", \"networkState\": " + (networkState != null ? networkState : "-") +
                '}';
    }
}
