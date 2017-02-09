package edu.umd.fcmd.sensorlisteners.model.util;

import edu.umd.fcmd.sensorlisteners.model.Probe;

/**
 * Created by MMueller on 1/4/2017.
 * <p>
 * Model class for reverse hearthbeats. Probe shows that in
 * the modelled period there have been no alive signs of the
 * application.
 * <p>
 * This probe rocks.
 */
public class ReverseHeartBeatProbe extends Probe {
    public static final String DEATH_START = "DEATHSTART";
    public static final String DEATH_END = "DEATHEND";

    private final String kind;

    public ReverseHeartBeatProbe(String kind) {
        this.kind = kind;
    }

    /**
     * Gets the kind.
     *
     * @return the kind.
     */
    public String getKind() {
        return kind;
    }

    /**
     * Gets the type of an kind e.g. Accelerometer
     *
     * @return the type of kind.
     */
    @Override
    public String getType() {
        return "ReverseHeartBeat";
    }

    @Override
    public String toString() {
        return "{\"kind\": " + '"' +kind + '"' +
                '}';
    }
}
