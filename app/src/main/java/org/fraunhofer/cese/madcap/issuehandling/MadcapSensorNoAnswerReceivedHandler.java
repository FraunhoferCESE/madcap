package org.fraunhofer.cese.madcap.issuehandling;

import org.fraunhofer.cese.madcap.MyApplication;

import edu.umd.fcmd.sensorlisteners.issuehandling.SensorNoAnswerReceivedHandler;

import static org.fraunhofer.cese.madcap.MyApplication.TAG;

/**
 * Created by MMueller on 11/23/2016.
 */

public class MadcapSensorNoAnswerReceivedHandler implements SensorNoAnswerReceivedHandler {
    /**
     * To define what should happen when no answer has been received
     * for a long time.
     */
    @Override
    public void onNoAnswerReceivedForLongTime(String s) {
        switch(s){
            case "Location":
                MyApplication.madcapLogger.d(TAG, "Did not receive answer for location for a long time");
                break;
        }
    }
}
