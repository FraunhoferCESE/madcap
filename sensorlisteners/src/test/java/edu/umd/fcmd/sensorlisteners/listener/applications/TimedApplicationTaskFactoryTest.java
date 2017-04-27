package edu.umd.fcmd.sensorlisteners.listener.applications;

import android.content.Context;

import org.junit.Test;

import edu.umd.fcmd.sensorlisteners.issuehandling.PermissionsManager;

import static org.mockito.Mockito.mock;

/**
 * Created by MMueller on 12/1/2016.
 */
public class TimedApplicationTaskFactoryTest {
    @Test
    public void create() throws Exception {
        ApplicationsListener mockApplicationsListener = mock(ApplicationsListener.class);
        Context mockContext = mock(Context.class);
        PermissionsManager mockPermissionsManager = mock(PermissionsManager.class);

        TimedApplicationTaskFactory cut = new TimedApplicationTaskFactory();

        TimedApplicationTask eq = new TimedApplicationTask(mockApplicationsListener, mockContext, mockPermissionsManager);

        cut.create(mockApplicationsListener, mockContext, mockPermissionsManager);
    }

}