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


import edu.umd.fcmd.sensorlisteners.issuehandling.PermissionsManager;
import edu.umd.fcmd.sensorlisteners.issuehandling.SensorNoAnswerReceivedHandler;
import edu.umd.fcmd.sensorlisteners.model.location.LocationProbe;
import edu.umd.fcmd.sensorlisteners.model.location.LocationServiceStatusProbe;
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
    LocationServiceStatusReceiverFactory mockLocationServiceStatusReceiverFactory;
    GoogleApiClient.ConnectionCallbacks mockConnectionCallbacks;
    GoogleApiClient.OnConnectionFailedListener mockOnConnectionFailedListener;
    PermissionsManager mockPermissionsManager;
    LocationServiceStatusReceiver mockLocationServiceStatusReceiver;
    SensorNoAnswerReceivedHandler mockSensorNoAnswerReceivedHandler;

    @Before
    public void setUp() throws Exception {
        mockContext = mock(Context.class);
        mockProbeManager = (ProbeManager<Probe>) mock(ProbeManager.class);
        mockGoogleApiClient = mock(GoogleApiClient.class);
        mockSnapshotApi = mock(Awareness.SnapshotApi.getClass());
        mockLocationServiceStatusReceiverFactory = mock(LocationServiceStatusReceiverFactory.class);
        mockConnectionCallbacks = mock(GoogleApiClient.ConnectionCallbacks.class);
        mockOnConnectionFailedListener = mock(GoogleApiClient.OnConnectionFailedListener.class);
        mockPermissionsManager = mock(PermissionsManager.class);
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
                mockLocationServiceStatusReceiverFactory,
                mockConnectionCallbacks,
                mockOnConnectionFailedListener,
                mockPermissionsManager,
                mockSensorNoAnswerReceivedHandler);
    }

    @Test
    public void onUpdate() throws Exception {
        LocationListener cut = new LocationListener(mockContext,
                mockProbeManager,
                mockGoogleApiClient,
                mockSnapshotApi,
                mockLocationServiceStatusReceiverFactory,
                mockConnectionCallbacks,
                mockOnConnectionFailedListener,
                mockPermissionsManager,
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
                mockLocationServiceStatusReceiverFactory,
                mockConnectionCallbacks,
                mockOnConnectionFailedListener,
                mockPermissionsManager,
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
                mockLocationServiceStatusReceiverFactory,
                mockConnectionCallbacks,
                mockOnConnectionFailedListener,
                mockPermissionsManager,
                mockSensorNoAnswerReceivedHandler);
        TimedLocationTask mockTimedLocationTask = mock(TimedLocationTask.class);

        cut.startListening();

        verify(mockTimedLocationTask).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Test
    public void stopListening() throws Exception {
        LocationListener cut = new LocationListener(mockContext,
                mockProbeManager,
                mockGoogleApiClient,
                mockSnapshotApi,
                mockLocationServiceStatusReceiverFactory,
                mockConnectionCallbacks,
                mockOnConnectionFailedListener,
                mockPermissionsManager,
                mockSensorNoAnswerReceivedHandler);
        TimedLocationTask mockTimedLocationTask = mock(TimedLocationTask.class);

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
                mockLocationServiceStatusReceiverFactory,
                mockConnectionCallbacks,
                mockOnConnectionFailedListener,
                mockPermissionsManager,
                mockSensorNoAnswerReceivedHandler);

        Assert.assertSame(mockContext, cut.getContext());
    }

    @Test
    public void getmGoogleApiClient(){
        LocationListener cut = new LocationListener(mockContext,
                mockProbeManager,
                mockGoogleApiClient,
                mockSnapshotApi,
                mockLocationServiceStatusReceiverFactory,
                mockConnectionCallbacks,
                mockOnConnectionFailedListener,
                mockPermissionsManager,
                mockSensorNoAnswerReceivedHandler);

        Assert.assertSame(mockGoogleApiClient, cut.getApiClient());
    }

}