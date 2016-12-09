package edu.umd.fcmd.sensorlisteners.listener.activity;

import com.google.android.gms.awareness.SnapshotApi;
import com.google.android.gms.awareness.fence.FenceState;
import com.google.android.gms.common.api.GoogleApiClient;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by MMueller on 12/9/2016.
 */
public class ActivityFenceReceiverTest {
    SnapshotApi mockSnapshotApi;
    ActivityListener mockActivityListener;
    GoogleApiClient mockGoogleApiClient;
    FenceState mockFenceState;

    @Before
    public void setUp() throws Exception {
        mockSnapshotApi = mock(SnapshotApi.class);
        mockActivityListener = mock(ActivityListener.class);
        mockGoogleApiClient = mock(GoogleApiClient.class);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void onReceive() throws Exception {
        ActivityFenceReceiver cut = new ActivityFenceReceiver(mockSnapshotApi, mockActivityListener, mockGoogleApiClient);
    }

    @Test
    public void createProbeFromActivityResult() throws Exception {

    }

}