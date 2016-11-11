package edu.umd.fcmd.sensorlisteners.listener.location;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.test.AndroidTestCase;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.SnapshotApi;
import com.google.android.gms.awareness.snapshot.LocationResult;
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

import javax.xml.datatype.Duration;

import edu.umd.fcmd.sensorlisteners.model.LocationState;
import edu.umd.fcmd.sensorlisteners.model.State;

import static junit.framework.TestCase.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * Created by MMueller on 11/10/2016.
 */
public class TimedLocationTaskTest{
    LocationListener mockLocationListener;
    SnapshotApi mockSnapshotApi;

    @Captor
    private ArgumentCaptor<ResultCallback<LocationResult>> resultCallbackLocationResultCaptor;

    @Before
    public void setUp() throws Exception {
        mockLocationListener = mock(LocationListener.class);
        mockSnapshotApi = mock(Awareness.SnapshotApi.getClass());

        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void create() throws Exception {
        TimedLocationTask cut = TimedLocationTask.create(mockLocationListener, mockSnapshotApi);

    }

    @Test
    public void constructor() throws Exception {
        try{
            TimedLocationTask.create(null, null);
            fail("With at least one null argument it should always throw a nullpointer exception");
        }catch (NullPointerException e){}

        try{
            TimedLocationTask.create(null, mockSnapshotApi);
            fail("With at least one null argument it should always throw a nullpointer exception");
        }catch (NullPointerException e){}

        try{
            TimedLocationTask.create(mockLocationListener, null);
            fail("With at least one null argument it should always throw a nullpointer exception");
        }catch (NullPointerException e){}
    }

    @Test
    public void doInBackground() throws Exception {
//        TimedLocationTask cut = TimedLocationTask.create(mockLocationListener, mockSnapshotApi);
//        Context mockContext = mock(Context.class);
//        GoogleApiClient mockGoogleApiClient = mock(GoogleApiClient.class);
//
//        // Mock that no permission is granted
//        when(mockLocationListener.getContext()).thenReturn(mockContext);
//        when(mockContext.checkPermission(anyString(), anyInt(), anyInt())).thenReturn(PackageManager.PERMISSION_DENIED);
//        // When there is no permission null should be returned
//        Assert.assertNull(cut.doInBackground());
//
//        // Mock that the permission is granted anyway
//        cut.setRunUntilCancelled(false);
//        when(mockContext.checkPermission(anyString(), anyInt(), anyInt())).thenReturn(PackageManager.PERMISSION_GRANTED);
//
//        PendingResult mockResult =  spy(PendingResult.class);
//        when(mockLocationListener.getmGoogleApiClient()).thenReturn(mockGoogleApiClient);
//        when(mockSnapshotApi.getLocation(mockGoogleApiClient)).thenReturn(mockResult);
//
//        cut.doInBackground();
//        verify(mockSnapshotApi).getLocation(mockGoogleApiClient);
//
//        //verify(mockResult).setResultCallback((ResultCallback<LocationResult>)any(ResultCallback.class));
//        verify(mockResult).setResultCallback(resultCallbackLocationResultCaptor.capture());
//
//        LocationResult mockLocationResult = spy(LocationResult.class);
//        Location mockLocation = mock(Location.class);
//
//        Status status = new Status(CommonStatusCodes.CANCELED);
//        when(mockLocationResult.getStatus()).thenReturn(status);
//        when(mockLocationResult.getLocation()).thenReturn(mockLocation);
//        resultCallbackLocationResultCaptor.getValue().onResult(mockLocationResult);
//
//        Status status2 = new Status(CommonStatusCodes.SUCCESS);
//        when(mockLocationResult.getStatus()).thenReturn(status2);
//        when(mockLocationResult.getLocation()).thenReturn(mockLocation);
//        resultCallbackLocationResultCaptor.getValue().onResult(mockLocationResult);
//
//        Assert.assertNull(cut.doInBackground());

    }

    @Test
    public void onProgressUpdate() throws Exception {
        TimedLocationTask cut = TimedLocationTask.create(mockLocationListener, mockSnapshotApi);

        Location mockLocation = mock(Location.class);
        cut.onProgressUpdate(mockLocation);
        verify(mockLocationListener).onUpdate(any(LocationState.class));
    }

}