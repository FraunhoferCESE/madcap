package edu.umd.fcmd.sensorlisteners.listener.location;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.SnapshotApi;
import com.google.android.gms.common.api.GoogleApiClient;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;


import edu.umd.fcmd.sensorlisteners.model.LocationState;
import edu.umd.fcmd.sensorlisteners.service.StateManager;

import static org.mockito.Mockito.*;


/**
 * Created by MMueller on 11/9/2016.
 */
public class LocationListenerTest {
    Context mockContext;
    StateManager<LocationState> mockStateManager;
    GoogleApiClient mockGoogleApiClient;
    SnapshotApi mockSnapshotApi;
    TimedLocationTaskFactory mockTimedLocationTaskFactory;

    @Before
    public void setUp() throws Exception {
        mockContext = mock(Context.class);
        mockStateManager = (StateManager<LocationState>) mock(StateManager.class);
        mockGoogleApiClient = mock(GoogleApiClient.class);
        mockSnapshotApi = mock(Awareness.SnapshotApi.getClass());
        mockTimedLocationTaskFactory = mock(TimedLocationTaskFactory.class);

        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void constructorTest(){
        //LocationListener cut = new LocationListener(mockContext, mockStateManager);
    }

    @Test
    public void onUpdate() throws Exception {
        LocationListener cut = new LocationListener(mockContext, mockStateManager, mockGoogleApiClient, mockSnapshotApi, mockTimedLocationTaskFactory);
        LocationState mockState = mock(LocationState.class);

        cut.onUpdate(mockState);
        verify(mockStateManager).save(mockState);
    }

    @Test
    public void startListening() throws Exception {
        LocationListener cut = new LocationListener(mockContext, mockStateManager, mockGoogleApiClient, mockSnapshotApi, mockTimedLocationTaskFactory);
        TimedLocationTask mockTimedLocationTask = mock(TimedLocationTask.class);

        cut.startListening();
    }

    @Test
    public void stopListening() throws Exception {
        LocationListener cut = new LocationListener(mockContext, mockStateManager, mockGoogleApiClient, mockSnapshotApi, mockTimedLocationTaskFactory);
        TimedLocationTask mockTimedLocationTask = mock(TimedLocationTask.class);

        // Make sure nothing happens when the task has not been instanciated before
        cut.stopListening();
        verify(mockTimedLocationTask, times(0)).cancel(anyBoolean());

        // Make sure the task gets cancelled when the task has been instanciated.
        when(mockTimedLocationTask.cancel(true)).thenReturn(true);
        //when(mockTimedLocationTaskFactory.create()).thenReturn(mockTimedLocationTask);
        cut.startListening();
    }
//
//
//    @Test
//    public void TimedLocationTaskTest() throws Exception {
//        LocationListener outerClass = new LocationListener(mockContext, mockStateManager, mockGoogleApiClient, mockTimedLocationTaskFactory);
//        LocationListener.TimedLocationTask cut = outerClass.new TimedLocationTask();
//        SnapshotApi mockSnapshotApi = mock(SnapshotApi.class);
//        cut.setSnapshotApi(mockSnapshotApi);
//
//
//
//        // === Test onProgressUpdate ===
//
//        Location mockLocation2 = mock(Location.class);
//        cut.onProgressUpdate(mockLocation2);
//    }
//
//    @Test
//    public void onConnected() throws Exception {
//        LocationListener cut = new LocationListener(mockContext, mockStateManager, mockGoogleApiClient, mockTimedLocationTaskFactory);
//
//        cut.onConnected(null);
//    }
//
//    @Test
//    public void onConnectionSuspended() throws Exception {
//        LocationListener cut = new LocationListener(mockContext, mockStateManager, mockGoogleApiClient, mockTimedLocationTaskFactory);
//
//        cut.onConnectionSuspended(0);
//    }
//
//    @Test
//    public void onConnectionFailed() throws Exception {
//        LocationListener cut = new LocationListener(mockContext, mockStateManager, mockGoogleApiClient, mockTimedLocationTaskFactory);
//        ConnectionResult mockConnectionResult = new ConnectionResult(1);
//
//        cut.onConnectionFailed(mockConnectionResult);
//    }
//
//    @Test
//    public void getContext() {
//        LocationListener cut = new LocationListener(mockContext, mockStateManager, mockGoogleApiClient, mockTimedLocationTaskFactory);
//
//        Assert.assertSame(mockContext, cut.getContext());
//    }
//
//    @Test
//    public void getmStateManager(){
//        LocationListener cut = new LocationListener(mockContext, mockStateManager, mockGoogleApiClient, mockTimedLocationTaskFactory);
//
//        Assert.assertSame(mockStateManager, cut.getmStateManager());
//    }
//
//    @Test
//    public void getmGoogleApiClient(){
//        LocationListener cut = new LocationListener(mockContext, mockStateManager, mockGoogleApiClient, mockTimedLocationTaskFactory);
//
//        Assert.assertSame(mockGoogleApiClient, cut.getmGoogleApiClient());
//    }

}