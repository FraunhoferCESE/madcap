package edu.umd.fcmd.sensorlisteners.listener.location;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.SnapshotApi;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static junit.framework.Assert.fail;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by MMueller on 11/11/2016.
 */
public class TimedLocationTaskFactoryTest {
    LocationListener mockLocationListener;
    SnapshotApi mockSnapshotApi;

    @Before
    public void setUp() throws Exception {
        mockLocationListener = mock(LocationListener.class);
        mockSnapshotApi = mock(Awareness.SnapshotApi.getClass());

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void create() throws Exception {
        TimedLocationTaskFactory cut = new TimedLocationTaskFactory();
        try{
            TimedLocationTask t = cut.create(null, null);
            fail("With at least one null argument it should always throw a nullpointer exception");
        }catch (NullPointerException e){}

        try{
            TimedLocationTask t = cut.create(null, mockSnapshotApi);
            fail("With at least one null argument it should always throw a nullpointer exception");
        }catch (NullPointerException e){}

        try{
            TimedLocationTask t = cut.create(mockLocationListener, null);
            fail("With at least one null argument it should always throw a nullpointer exception");
        }catch (NullPointerException e){}
    }
}