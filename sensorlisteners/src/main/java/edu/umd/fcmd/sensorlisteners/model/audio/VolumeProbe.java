package edu.umd.fcmd.sensorlisteners.model.audio;

import edu.umd.fcmd.sensorlisteners.model.Probe;

/**
 * Created by MMueller on 1/24/2017.
 *
 * Model class for the volume
 */
public class VolumeProbe extends Probe {
    private static final String VOLUME_TYPE = "Volume";
    private int volume;
    private String kind;

    /**
     * Gets the Volume.
     *
     * @return int [0-100].
     */
    int getVolume() {
        return volume;
    }

    /**
     * Sets the Volume.
     */
    public void setVolume(int volume) {
        this.volume = volume;
    }

    /**
     * Gets the kind of the probe e.g.
     * System volume or ringer volume.
     *
     * @return the kind of volume probe.
     */
    public String getKind() {
        return kind;
    }

    /**
     * Sets the kind of the probe e.g.
     * System volume or ringer volume.
     *
     * @param kind the kind of volume to set to.
     */
    public void setKind(String kind) {
        this.kind = kind;
    }

    /**
     * Gets the type of an state e.g. Accelerometer
     *
     * @return the type of state.
     */
    @Override
    public String getType() {
        return VOLUME_TYPE;
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
        return "{\"volume\": " + volume +
                ", \"kind\": " + kind +
                '}';
    }
}
