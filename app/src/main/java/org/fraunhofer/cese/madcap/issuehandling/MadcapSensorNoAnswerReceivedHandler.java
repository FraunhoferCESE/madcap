package org.fraunhofer.cese.madcap.issuehandling;

import edu.umd.fcmd.sensorlisteners.issuehandling.SensorNoAnswerReceivedHandler;
import timber.log.Timber;


/**
 * Created by MMueller on 11/23/2016.
 */

public class MadcapSensorNoAnswerReceivedHandler implements SensorNoAnswerReceivedHandler {

    private static final String TAG = "MadcapSensorNoAnswerReceivedHandler";
    /**
     * To define what should happen when no answer has been received
     * for a long time.
     */
    @Override
    public void onNoAnswerReceivedForLongTime(String s) {
        switch(s){
            case "Location":
                Timber.d("Did not receive answer for location for a long time");
                break;
        }
    }
}
