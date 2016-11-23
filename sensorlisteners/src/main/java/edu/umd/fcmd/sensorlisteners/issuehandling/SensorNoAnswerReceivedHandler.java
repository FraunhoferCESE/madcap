package edu.umd.fcmd.sensorlisteners.issuehandling;

/**
 * Created by MMueller on 11/23/2016.
 *
 * Used for Sensors which are using asynchronous calls and callbacks
 * to define what happens after a certain amount of time without any
 * received ansers
 */

public interface SensorNoAnswerReceivedHandler {

    /**
     * To define what should happen when no answer has been received
     * for a long time.
     */
    void onNoAnswerReceivedForLongTime(String s);
}
