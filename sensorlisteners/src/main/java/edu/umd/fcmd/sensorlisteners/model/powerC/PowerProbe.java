package edu.umd.fcmd.sensorlisteners.model.powerC;

import edu.umd.fcmd.sensorlisteners.model.Probe;

/**
 * Created by MMueller on 12/12/2016.
 *
 * Model class of the remaining power on a phone.
 */
public class PowerProbe extends Probe {
    private double remainingPower;
    private double voltage;
    private double temperature;
    private double health;

    /**
     * Gets the battery health.
     * @return battery health.
     */
    public double getHealth() {
        return health;
    }

    /**
     * Sets the battery health.
     * @param health the health to set to.
     */
    public void setHealth(double health) {
        this.health = health;
    }

    /**
     * Get the voltage.
     * @return voltage.
     */
    public double getVoltage() {
        return voltage;
    }

    /**
     * Sets the voltage.
     * @param voltage to be set to.
     */
    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }

    /**
     * Gets the battery temperature.
     * @return the battery temperature.
     */
    public double getTemperature() {
        return temperature;
    }

    /**
     * Sets the battery temperature.
     * @param temperature the temperature to set to.
     */
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    /**
     * Getter for percentage of power remaining.
     * @return the remaining power.
     */
    public double getRemainingPower() {
        return remainingPower;
    }

    /**
     * Sets the remaining power.
     * @param remainingPower to set to.
     */
    public void setRemainingPower(double remainingPower) {
        this.remainingPower = remainingPower;
    }

    /**
     * Gets the type of an state e.g. Accelerometer
     *
     * @return the type of state.
     */
    @Override
    public String getType() {
        return "Power";
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
        return "{\"remainingPower\": " + remainingPower +
                ", \"voltage\": " + voltage +
                ", \"health\": " + health +
                ", \"temperature\": " + temperature +
                '}';
    }
}
