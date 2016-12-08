package edu.umd.fcmd.sensorlisteners.model;

/**
 * Created by MMueller on 12/8/2016.
 *
 * Probe representing recognized activity encoded in all possibile
 * activities alongside with their detected possibility.
 */
public class ActivityProbe extends Probe {
    private double onBicycle;
    private double inVehicle;
    private double onFoot;
    private double running;
    private double still;
    private double tilting;
    private double walking;
    private double unknown;

    /**
     * Gets the propability for the user being on a bike.
     * @return the propability.
     */
    public double getOnBicycle() {
        return onBicycle;
    }

    public void setOnBicycle(double onBicycle) {
        this.onBicycle = onBicycle;
    }

    /**
     * Gets the propability for the user being in a vehicle.
     * @return the propability.
     */
    public double getInVehicle() {
        return inVehicle;
    }

    /**
     * Sets the propability for the user being in a vehicle.
     * @param inVehicle propability.
     */
    public void setInVehicle(double inVehicle) {
        this.inVehicle = inVehicle;
    }

    /**
     * Gets the propability for the user being on foot.
     * @return the propability.
     */
    public double getOnFoot() {
        return onFoot;
    }

    /**
     * Sets the propability for the user being onFoot.
     * @param onFoot propability.
     */
    public void setOnFoot(double onFoot) {
        this.onFoot = onFoot;
    }

    /**
     * Gets the propability for the user running.
     * @return the propability.
     */
    public double getRunning() {
        return running;
    }

    /**
     * Sets the propability for the user being running.
     * @param running propability.
     */
    public void setRunning(double running) {
        this.running = running;
    }

    /**
     * Gets the propability for the user to be standing still.
     * @return the propability.
     */
    public double getStill() {
        return still;
    }

    /**
     * Sets the propability for the user standing still.
     * @param still propability.
     */
    public void setStill(double still) {
        this.still = still;
    }

    /**
     * Gets the propability for the user tilting his phone.
     * @return the propability.
     */
    public double getTilting() {
        return tilting;
    }

    /**
     * Sets the propability for the user tilting his device.
     * @param tilting propability.
     */
    public void setTilting(double tilting) {
        this.tilting = tilting;
    }

    /**
     * Gets the propability for the user walking.
     * @return propability.
     */
    public double getWalking() {
        return walking;
    }

    /**
     * Sets the propability for the user being walking.
     * @param walking propability.
     */
    public void setWalking(double walking) {
        this.walking = walking;
    }

    /**
     * Gets the propability that it is no predefined activity.
     * @return the propability.
     */
    public double getUnknown() {
        return unknown;
    }

    /**
     * Sets the propability that it is no predefined activity.
     * @param unknown propability;
     */
    public void setUnknown(double unknown) {
        this.unknown = unknown;
    }

    /**
     * Gets the type of an state e.g. Accelerometer
     *
     * @return the type of state.
     */
    @Override
    public String getType() {
        return "Activity";
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
        return "{\"onBicycle\": " + onBicycle +
                ", \"inVehicle\": " + inVehicle +
                ", \"onFoot\": " + onFoot +
                ", \"running\": " + running +
                ", \"still\": " + still +
                ", \"tilting\": " + tilting +
                ", \"walking\": " + walking +
                ", \"unknown\": " + unknown +
                '}';
    }
}
