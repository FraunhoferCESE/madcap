package edu.umd.fcmd.sensorlisteners.listener.location;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.SnapshotApi;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;

import edu.umd.fcmd.sensorlisteners.listener.location.LocationListener;
import edu.umd.fcmd.sensorlisteners.model.LocationState;
import edu.umd.fcmd.sensorlisteners.service.StateManager;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by MMueller on 11/9/2016.
 */
public class LocationListenerTest {
    Context mockContext;
    StateManager<LocationState> mockStateManager;
    GoogleApiClient.Builder mockBuilder;
    SnapshotApi mockSnapshotApi;

    @Captor
    private ArgumentCaptor<ResultCallback<LocationResult>> resultCallbackLocationResultCaptor;

    @Before
    public void setUp() throws Exception {
        mockContext = spy(Context.class);
        mockStateManager = (StateManager<LocationState>) spy(StateManager.class);
        mockBuilder = mock(GoogleApiClient.Builder.class);
        mockSnapshotApi = mock(Awareness.SnapshotApi.getClass());

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
        LocationListener cut = new LocationListener(mockContext, mockStateManager, mockBuilder, mockSnapshotApi);
//        LocationState mockState = mock(LocationState.class);
//
//        cut.onUpdate(mockState);
//        verify(mockStateManager).save(mockState);
    }

//    @Test
//    public void startListening() throws Exception {
//        LocationListener cut = new LocationListener(mockContext, mockStateManager, mockGoogleApiClient, mockTimedLocationTaskFactory);
//        LocationListener.TimedLocationTask mockTimedLocationTask = mock(LocationListener.TimedLocationTask.class);
//        when(mockTimedLocationTaskFactory.create()).thenReturn(mockTimedLocationTask);
//
//        cut.startListening();
//        verify(mockTimedLocationTask).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//    }
//
//    @Test
//    public void stopListening() throws Exception {
//        LocationListener cut = new LocationListener(mockContext, mockStateManager, mockGoogleApiClient, mockTimedLocationTaskFactory);
//        LocationListener.TimedLocationTask mockTimedLocationTask = mock(LocationListener.TimedLocationTask.class);
//
//        // Make sure nothing happens when the task has not been instanciated before
//        cut.stopListening();
//        verify(mockTimedLocationTask, times(0)).cancel(anyBoolean());
//
//        // Make sure the task gets cancelled when the task has been instanciated.
//        when(mockTimedLocationTask.cancel(true)).thenReturn(true);
//        when(mockTimedLocationTaskFactory.create()).thenReturn(mockTimedLocationTask);
//        cut.startListening();
//    }
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