package edu.umd.fcmd.sensorlisteners.listener;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import edu.umd.fcmd.sensorlisteners.listener.location.LocationListenerTestSuite;


/**
 * Created by MMueller on 11/11/2016.
 */

@RunWith(Suite.class)

@Suite.SuiteClasses({
        LocationListenerTestSuite.class
})

public class ListenerTestSuite {
}
