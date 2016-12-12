package edu.umd.fcmd.sensorlisteners.listener.activity;

import android.os.Bundle;

import com.google.android.gms.awareness.SnapshotApi;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by MMueller on 12/12/2016.
 */
public class ActivityListenerTest {
    ProbeManager<Probe> mockProbeManager;
    GoogleApiClient mockGoogleApiClient;
    SnapshotApi mockSnapshotApi;
    TimedActivityTaskFactory mockTimedActivityTaskFactory;

    @Before
    public void setUp() throws Exception {
        mockProbeManager = mock(ProbeManager.class);
        mockGoogleApiClient = mock(GoogleApiClient.class);
        mockSnapshotApi = mock(SnapshotApi.class);
        mockTimedActivityTaskFactory = mock(TimedActivityTaskFactory.class);
    }

    @Test
    public void constructorTest() throws Exception {
        ActivityListener cut = new ActivityListener(mockProbeManager, mockGoogleApiClient, mockSnapshotApi, mockTimedActivityTaskFactory);
    }

    @Test
    public void onUpdate() throws Exception {
        ActivityListener cut = new ActivityListener(mockProbeManager, mockGoogleApiClient, mockSnapshotApi, mockTimedActivityTaskFactory);

        Probe mockProbe = mock(Probe.class);
        cut.onUpdate(mockProbe);
        verify(mockProbeManager).save(mockProbe);
    }

    @Test
    public void startListening() throws Exception {
        ActivityListener cut = new ActivityListener(mockProbeManager, mockGoogleApiClient, mockSnapshotApi, mockTimedActivityTaskFactory);

        cut.startListening();
        Assert.assertTrue(cut.isRunning());
    }

    @Test
    public void stopListening() throws Exception {
        ActivityListener cut = new ActivityListener(mockProbeManager, mockGoogleApiClient, mockSnapshotApi, mockTimedActivityTaskFactory);

        TimedActivityTask mockTimedActivityTask = mock(TimedActivityTask.class);

        when(mockTimedActivityTaskFactory.create(cut, mockSnapshotApi)).thenReturn(mockTimedActivityTask);

        cut.startListening();
        cut.onConnected(null);
        cut.stopListening();

        Assert.assertFalse(cut.isRunning());
    }

    @Test
    public void isRunning() throws Exception {
        ActivityListener cut = new ActivityListener(mockProbeManager, mockGoogleApiClient, mockSnapshotApi, mockTimedActivityTaskFactory);

        TimedActivityTask mockTimedActivityTask = mock(TimedActivityTask.class);

        when(mockTimedActivityTaskFactory.create(cut, mockSnapshotApi)).thenReturn(mockTimedActivityTask);

        cut.startListening();
        Assert.assertTrue(cut.isRunning());

        cut.onConnected(null);
        cut.stopListening();
        Assert.assertFalse(cut.isRunning());
    }

    @Test
    public void onConnected() throws Exception {
        ActivityListener cut = new ActivityListener(mockProbeManager, mockGoogleApiClient, mockSnapshotApi, mockTimedActivityTaskFactory);

        Bundle mockBundle = mock(Bundle.class);
        TimedActivityTask mockTimedActivityTask = mock(TimedActivityTask.class);
        when(mockTimedActivityTaskFactory.create(cut, mockSnapshotApi)).thenReturn(mockTimedActivityTask);

        cut.onConnected(null);
        cut.onConnected(mockBundle);
    }

    @Test
    public void onConnectionSuspended() throws Exception {
        ActivityListener cut = new ActivityListener(mockProbeManager, mockGoogleApiClient, mockSnapshotApi, mockTimedActivityTaskFactory);

        cut.onConnectionSuspended(100000);
    }

    @Test
    public void onConnectionFailed() throws Exception {
        ActivityListener cut = new ActivityListener(mockProbeManager, mockGoogleApiClient, mockSnapshotApi, mockTimedActivityTaskFactory);

        ConnectionResult mockConnectionResult = new ConnectionResult(1);
        cut.onConnectionFailed(mockConnectionResult);
    }

    @Test
    public void getGoogleApiClient() throws Exception {
        ActivityListener cut = new ActivityListener(mockProbeManager, mockGoogleApiClient, mockSnapshotApi, mockTimedActivityTaskFactory);

        Assert.assertEquals(cut.getGoogleApiClient(), mockGoogleApiClient);
    }

}