package edu.umd.fcmd.sensorlisteners.listener.location;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Created by MMueller on 11/11/2016.
 */
@RunWith(Suite.class)

@Suite.SuiteClasses({
        LocationListenerTest.class,
        TimedLocationTaskTest.class,
        TimedLocationTaskFactoryTest.class,
        LocationServiceStatusReceiverFactoryTest.class,
        LocationServiceStatusReceiverTest.class
})

public class LocationListenerTestSuite {
}
