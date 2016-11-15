package edu.umd.fcmd.sensorlisteners.listener.location;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.SnapshotApi;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;


import edu.umd.fcmd.sensorlisteners.model.LocationProbe;
import edu.umd.fcmd.sensorlisteners.service.StateManager;

import static org.mockito.Mockito.*;


/**
 * Created by MMueller on 11/9/2016.
 */
public class LocationListenerTest {
    Context mockContext;
    StateManager<LocationProbe> mockStateManager;
    GoogleApiClient mockGoogleApiClient;
    SnapshotApi mockSnapshotApi;
    TimedLocationTaskFactory mockTimedLocationTaskFactory;

    @Before
    public void setUp() throws Exception {
        mockContext = mock(Context.class);
        mockStateManager = (StateManager<LocationProbe>) mock(StateManager.class);
        mockGoogleApiClient = mock(GoogleApiClient.class);
        mockSnapshotApi = mock(Awareness.SnapshotApi.getClass());
        mockTimedLocationTaskFactory = mock(TimedLocationTaskFactory.class);

        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception {

    }

//    @Test
//    public void constructorTest(){
//        //LocationListener cut = new LocationListener(mockContext, mockStateManager);
//    }
//
//    @Test
//    public void onUpdate() throws Exception {
//        LocationListener cut = new LocationListener(mockContext, mockStateManager, mockGoogleApiClient, mockSnapshotApi, mockTimedLocationTaskFactory);
//        LocationProbe mockState = mock(LocationProbe.class);
//
//        cut.onUpdate(mockState);
//        verify(mockStateManager).save(mockState);
//    }
//
//    @Test
//    public void startListening() throws Exception {
//        LocationListener cut = new LocationListener(mockContext, mockStateManager, mockGoogleApiClient, mockSnapshotApi, mockTimedLocationTaskFactory);
//        TimedLocationTask mockTimedLocationTask = mock(TimedLocationTask.class);
//        when(mockTimedLocationTaskFactory.create(cut, mockSnapshotApi)).thenReturn(mockTimedLocationTask);
//
//        cut.startListening();
//
//        verify(mockTimedLocationTask).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//    }
//
//    @Test
//    public void stopListening() throws Exception {
//        LocationListener cut = new LocationListener(mockContext, mockStateManager, mockGoogleApiClient, mockSnapshotApi, mockTimedLocationTaskFactory);
//        TimedLocationTask mockTimedLocationTask = mock(TimedLocationTask.class);
//        when(mockTimedLocationTaskFactory.create(cut, mockSnapshotApi)).thenReturn(mockTimedLocationTask);
//
//        // Make sure nothing happens when the task has not been instanciated before
//        cut.stopListening();
//        verify(mockTimedLocationTask, times(0)).cancel(anyBoolean());
//
//        // Make sure the task gets cancelled when the task has been instanciated.
//        when(mockTimedLocationTask.cancel(true)).thenReturn(true);
//        cut.startListening();
//        cut.stopListening();
//        verify(mockGoogleApiClient).disconnect();
//        verify(mockTimedLocationTask).cancel(true);
//    }
//
//    @Test
//    public void onConnected() throws Exception {
//        LocationListener cut = new LocationListener(mockContext, mockStateManager, mockGoogleApiClient, mockSnapshotApi, mockTimedLocationTaskFactory);
//
//        cut.onConnected(null);
//    }
//
//    @Test
//    public void onConnectionSuspended() throws Exception {
//        LocationListener cut = new LocationListener(mockContext, mockStateManager, mockGoogleApiClient, mockSnapshotApi, mockTimedLocationTaskFactory);
//
//        cut.onConnectionSuspended(0);
//    }
//
//    @Test
//    public void onConnectionFailed() throws Exception {
//        LocationListener cut = new LocationListener(mockContext, mockStateManager, mockGoogleApiClient, mockSnapshotApi, mockTimedLocationTaskFactory);
//        ConnectionResult mockConnectionResult = new ConnectionResult(1);
//
//        cut.onConnectionFailed(mockConnectionResult);
//    }
//
//    @Test
//    public void getContext() {
//        LocationListener cut = new LocationListener(mockContext, mockStateManager, mockGoogleApiClient, mockSnapshotApi, mockTimedLocationTaskFactory);
//
//        Assert.assertSame(mockContext, cut.getContext());
//    }
//
//    @Test
//    public void getmGoogleApiClient(){
//        LocationListener cut = new LocationListener(mockContext, mockStateManager, mockGoogleApiClient, mockSnapshotApi, mockTimedLocationTaskFactory);
//
//        Assert.assertSame(mockGoogleApiClient, cut.getmGoogleApiClient());
//    }

}