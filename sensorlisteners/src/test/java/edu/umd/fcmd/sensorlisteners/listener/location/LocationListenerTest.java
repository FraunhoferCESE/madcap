package edu.umd.fcmd.sensorlisteners.listener.location;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.SnapshotApi;
import com.google.android.gms.common.api.GoogleApiClient;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;


import edu.umd.fcmd.sensorlisteners.issuehandling.PermissionDeniedHandler;
import edu.umd.fcmd.sensorlisteners.issuehandling.SensorNoAnswerReceivedHandler;
import edu.umd.fcmd.sensorlisteners.model.LocationProbe;
import edu.umd.fcmd.sensorlisteners.model.LocationServiceStatusProbe;
import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;

import static org.mockito.Mockito.*;


/**
 * Created by MMueller on 11/9/2016.
 */
public class LocationListenerTest {
    Context mockContext;
    ProbeManager<Probe> mockProbeManager;
    GoogleApiClient mockGoogleApiClient;
    SnapshotApi mockSnapshotApi;
    TimedLocationTaskFactory mockTimedLocationTaskFactory;
    LocationServiceStatusReceiverFactory mockLocationServiceStatusReceiverFactory;
    GoogleApiClient.ConnectionCallbacks mockConnectionCallbacks;
    GoogleApiClient.OnConnectionFailedListener mockOnConnectionFailedListener;
    PermissionDeniedHandler mockPermissionDeniedHandler;
    LocationServiceStatusReceiver mockLocationServiceStatusReceiver;
    SensorNoAnswerReceivedHandler mockSensorNoAnswerReceivedHandler;

    @Before
    public void setUp() throws Exception {
        mockContext = mock(Context.class);
        mockProbeManager = (ProbeManager<Probe>) mock(ProbeManager.class);
        mockGoogleApiClient = mock(GoogleApiClient.class);
        mockSnapshotApi = mock(Awareness.SnapshotApi.getClass());
        mockTimedLocationTaskFactory = mock(TimedLocationTaskFactory.class);
        mockLocationServiceStatusReceiverFactory = mock(LocationServiceStatusReceiverFactory.class);
        mockConnectionCallbacks = mock(GoogleApiClient.ConnectionCallbacks.class);
        mockOnConnectionFailedListener = mock(GoogleApiClient.OnConnectionFailedListener.class);
        mockPermissionDeniedHandler = mock(PermissionDeniedHandler.class);
        mockLocationServiceStatusReceiver = mock(LocationServiceStatusReceiver.class);
        mockSensorNoAnswerReceivedHandler = mock(SensorNoAnswerReceivedHandler.class);

        when(mockLocationServiceStatusReceiverFactory.create(any(LocationListener.class))).thenReturn(mockLocationServiceStatusReceiver);

        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void constructorTest(){
        LocationListener cut = new LocationListener(mockContext,
                mockProbeManager,
                mockGoogleApiClient,
                mockSnapshotApi,
                mockTimedLocationTaskFactory,
                mockLocationServiceStatusReceiverFactory,
                mockConnectionCallbacks,
                mockOnConnectionFailedListener,
                mockPermissionDeniedHandler,
                mockSensorNoAnswerReceivedHandler);
    }

    @Test
    public void onUpdate() throws Exception {
        LocationListener cut = new LocationListener(mockContext,
                mockProbeManager,
                mockGoogleApiClient,
                mockSnapshotApi,
                mockTimedLocationTaskFactory,
                mockLocationServiceStatusReceiverFactory,
                mockConnectionCallbacks,
                mockOnConnectionFailedListener,
                mockPermissionDeniedHandler,
                mockSensorNoAnswerReceivedHandler);

        //Testing onUpdate with a Location Probe
        LocationProbe mockState = mock(LocationProbe.class);

        cut.onUpdate(mockState);
        verify(mockProbeManager).save(mockState);

        //Testin onUpdate with a LocationServiceStatus Probe
        ProbeManager mockProbeManager2 = mock(ProbeManager.class);

        cut = new LocationListener(mockContext,
                mockProbeManager2,
                mockGoogleApiClient,
                mockSnapshotApi,
                mockTimedLocationTaskFactory,
                mockLocationServiceStatusReceiverFactory,
                mockConnectionCallbacks,
                mockOnConnectionFailedListener,
                mockPermissionDeniedHandler,
                mockSensorNoAnswerReceivedHandler);

        LocationServiceStatusProbe mockServiceState = mock(LocationServiceStatusProbe.class);

        cut.onUpdate(mockServiceState);
        verify(mockProbeManager2).save(mockServiceState);
    }

    @Test
    public void startListening() throws Exception {
        LocationListener cut = new LocationListener(mockContext,
                mockProbeManager,
                mockGoogleApiClient,
                mockSnapshotApi,
                mockTimedLocationTaskFactory,
                mockLocationServiceStatusReceiverFactory,
                mockConnectionCallbacks,
                mockOnConnectionFailedListener,
                mockPermissionDeniedHandler,
                mockSensorNoAnswerReceivedHandler);
        TimedLocationTask mockTimedLocationTask = mock(TimedLocationTask.class);
        when(mockTimedLocationTaskFactory.create(cut, mockSnapshotApi, mockPermissionDeniedHandler, mockSensorNoAnswerReceivedHandler)).thenReturn(mockTimedLocationTask);

        cut.startListening();

        verify(mockTimedLocationTask).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Test
    public void stopListening() throws Exception {
        LocationListener cut = new LocationListener(mockContext,
                mockProbeManager,
                mockGoogleApiClient,
                mockSnapshotApi,
                mockTimedLocationTaskFactory,
                mockLocationServiceStatusReceiverFactory,
                mockConnectionCallbacks,
                mockOnConnectionFailedListener,
                mockPermissionDeniedHandler,
                mockSensorNoAnswerReceivedHandler);
        TimedLocationTask mockTimedLocationTask = mock(TimedLocationTask.class);
        when(mockTimedLocationTaskFactory.create(cut, mockSnapshotApi, mockPermissionDeniedHandler, mockSensorNoAnswerReceivedHandler)).thenReturn(mockTimedLocationTask);

        // Make sure nothing happens when the task has not been instanciated before
        cut.stopListening();
        verify(mockTimedLocationTask, times(0)).cancel(anyBoolean());

        // Make sure the task gets cancelled when the task has been instanciated.
        when(mockTimedLocationTask.cancel(true)).thenReturn(true);
        cut.startListening();
        cut.stopListening();
        verify(mockGoogleApiClient).disconnect();
        verify(mockTimedLocationTask).cancel(true);
    }
    @Test
    public void getContext() {
        LocationListener cut = new LocationListener(mockContext,
                mockProbeManager,
                mockGoogleApiClient,
                mockSnapshotApi,
                mockTimedLocationTaskFactory,
                mockLocationServiceStatusReceiverFactory,
                mockConnectionCallbacks,
                mockOnConnectionFailedListener,
                mockPermissionDeniedHandler,
                mockSensorNoAnswerReceivedHandler);

        Assert.assertSame(mockContext, cut.getContext());
    }

    @Test
    public void getmGoogleApiClient(){
        LocationListener cut = new LocationListener(mockContext,
                mockProbeManager,
                mockGoogleApiClient,
                mockSnapshotApi,
                mockTimedLocationTaskFactory,
                mockLocationServiceStatusReceiverFactory,
                mockConnectionCallbacks,
                mockOnConnectionFailedListener,
                mockPermissionDeniedHandler,
                mockSensorNoAnswerReceivedHandler);

        Assert.assertSame(mockGoogleApiClient, cut.getmGoogleApiClient());
    }

}