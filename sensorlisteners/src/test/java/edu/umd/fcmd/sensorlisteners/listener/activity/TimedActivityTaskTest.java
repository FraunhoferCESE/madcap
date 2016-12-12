package edu.umd.fcmd.sensorlisteners.listener.activity;

import android.bluetooth.BluetoothDevice;

import com.google.android.gms.awareness.SnapshotApi;
import com.google.android.gms.awareness.snapshot.DetectedActivityResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

//        GoogleApiClient mockClient = mock(GoogleApiClient.class);
//        when(mockActivityListener.getGoogleApiClient()).thenReturn(mockClient);
//
//        PendingResult<DetectedActivityResult> mockPendingResult= mock(PendingResult.class);
//        when(mockSnapshotApi.getDetectedActivity(mockClient)).thenReturn(mockPendingResult);
//        cut.doInBackground();
    }

    @Test
    public void onProgressUpdate() throws Exception {
        TimedActivityTask cut = new TimedActivityTask(mockActivityListener, mockSnapshotApi);

        ActivityRecognitionResult mockActivityRecognitionResult = mock(ActivityRecognitionResult.class);

        List<DetectedActivity> mockActivityList = (List<DetectedActivity>) mock(List.class);
        when(mockActivityRecognitionResult.getProbableActivities()).thenReturn(mockActivityList);

        DetectedActivity mockActivity0 = mock(DetectedActivity.class);
        DetectedActivity mockActivity1 = mock(DetectedActivity.class);
        DetectedActivity mockActivity2 = mock(DetectedActivity.class);
        DetectedActivity mockActivity3 = mock(DetectedActivity.class);
        DetectedActivity mockActivity4 = mock(DetectedActivity.class);
        DetectedActivity mockActivity5 = mock(DetectedActivity.class);
        DetectedActivity mockActivity6 = mock(DetectedActivity.class);
        DetectedActivity mockActivity7 = mock(DetectedActivity.class);
        DetectedActivity mockActivity8 = mock(DetectedActivity.class);
        DetectedActivity mockActivity9 = mock(DetectedActivity.class);

        when(mockActivity0.getType()).thenReturn(DetectedActivity.IN_VEHICLE);
        when(mockActivity1.getType()).thenReturn(DetectedActivity.ON_BICYCLE);
        when(mockActivity2.getType()).thenReturn(DetectedActivity.ON_FOOT);
        when(mockActivity3.getType()).thenReturn(DetectedActivity.RUNNING);
        when(mockActivity4.getType()).thenReturn(DetectedActivity.STILL);
        when(mockActivity5.getType()).thenReturn(DetectedActivity.TILTING);
        when(mockActivity6.getType()).thenReturn(DetectedActivity.WALKING);
        when(mockActivity7.getType()).thenReturn(DetectedActivity.UNKNOWN);
        when(mockActivity8.getType()).thenReturn(-1000);
        when(mockActivity9.getType()).thenReturn(25391);

        Iterator<DetectedActivity> mockIterator = mock(Iterator.class);
        when(mockIterator.hasNext()).thenReturn(true, true, true, true, true, true, true, true, true, true, false);
        when(mockIterator.next()).thenReturn(mockActivity0)
                .thenReturn(mockActivity1)
                .thenReturn(mockActivity2)
                .thenReturn(mockActivity3)
                .thenReturn(mockActivity4)
                .thenReturn(mockActivity5)
                .thenReturn(mockActivity6)
                .thenReturn(mockActivity7)
                .thenReturn(mockActivity8)
                .thenReturn(mockActivity9);

        when(mockActivityList.iterator()).thenReturn(mockIterator);

        cut.onProgressUpdate(mockActivityRecognitionResult);

    }

}