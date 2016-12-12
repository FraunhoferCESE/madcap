package edu.umd.fcmd.sensorlisteners.listener.activity;

import com.google.android.gms.awareness.SnapshotApi;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.umd.fcmd.sensorlisteners.listener.applications.ApplicationsListener;
import edu.umd.fcmd.sensorlisteners.listener.applications.TimedApplicationTaskFactory;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by MMueller on 12/12/2016.
 */
public class TimedActivityTaskFactoryTest {
    ActivityListener mockActivityListener;
    SnapshotApi mockSnapshotApi;

    @Before
    public void setUp() throws Exception {
        mockActivityListener = mock(ActivityListener.class);
        mockSnapshotApi = mock(SnapshotApi.class);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void create() throws Exception {
        TimedActivityTaskFactory cut = new TimedActivityTaskFactory();

        Assert.assertEquals((cut.create(mockActivityListener, mockSnapshotApi)).getClass(), TimedActivityTask.class);
    }

}