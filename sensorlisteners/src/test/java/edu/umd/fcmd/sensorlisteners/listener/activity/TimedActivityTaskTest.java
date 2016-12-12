package edu.umd.fcmd.sensorlisteners.listener.activity;

import android.content.Context;

import com.google.android.gms.awareness.SnapshotApi;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by MMueller on 12/12/2016.
 */
public class TimedActivityTaskTest {
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
    public void constructorTEst() throws Exception {
        TimedActivityTask cut = new TimedActivityTask(mockActivityListener, mockSnapshotApi);
    }

    @Test
    public void doInBackground() throws Exception {
        TimedActivityTask cut = new TimedActivityTask(mockActivityListener, mockSnapshotApi);

        cut.doInBackground();
    }

    @Test
    public void onProgressUpdate() throws Exception {

    }

}