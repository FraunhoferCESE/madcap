package edu.umd.fcmd.sensorlisteners.model.util;

import edu.umd.fcmd.sensorlisteners.model.Probe;

/**
 * Created by MMueller on 1/4/2017.
 *
 * Model class for reverse hearthbeats. Probe shows that in
 * the modelled period there have been no alive signs of the
 * application.
 *
 * This probe rocks.
 */
public class ReverseHeartBeatProbe extends Probe {
    private long deathStart;
    private long deathEnd;

    /**
     * Gets the deathStart.
     * @return the deathStart.
     */
    public long getDeathStart() {
        return deathStart;
    }

    /**
     * Sets the deathStart.
     * @param deathStart the deathStart.
     */
    public void setDeathStart(long deathStart) {
        this.deathStart = deathStart;
    }

    /**
     * Gets the deathEnd.
     * @return the deathEnd.
     */
    public long getDeathEnd() {
        return deathEnd;
    }

    /**
     * Sets the deathEnd.
     * @param deathEnd to be set.
     */
    public void setDeathEnd(long deathEnd) {
        this.deathEnd = deathEnd;
    }

    /**
     * Gets the type of an deathStart e.g. Accelerometer
     *
     * @return the type of deathStart.
     */
    @Override
    public String getType() {
        return "ReverseHeartBeat";
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
        return "{\"deathStart\": " + deathStart+
                ", \"deathEnd\": " + deathEnd +
                '}';
    }
}
