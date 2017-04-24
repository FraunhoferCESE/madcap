package edu.umd.fcmd.sensorlisteners.listener.applications;

import android.content.Context;
import android.os.AsyncTask;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.umd.fcmd.sensorlisteners.issuehandling.PermissionDeniedHandler;
import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by MMueller on 12/1/2016.
 */
public class ApplicationsListenerTest {
    private Context mockContext;
    private ProbeManager<Probe> mockProbeProbeManager;
    private TimedApplicationTaskFactory mockTimedApplicationTaskFactory;
    private PermissionDeniedHandler mockPermissionDeniedHandler;

    @Before
    public void setUp() throws Exception {
        mockContext = mock(Context.class);
        mockProbeProbeManager = (ProbeManager<Probe>) mock(ProbeManager.class);
        mockTimedApplicationTaskFactory = mock(TimedApplicationTaskFactory.class);
        mockPermissionDeniedHandler = mock(PermissionDeniedHandler.class);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void constructorTest(){
        ApplicationsListener cut = new ApplicationsListener(mockContext, mockProbeProbeManager, mockTimedApplicationTaskFactory, mockPermissionDeniedHandler);
    }

    @Test
    public void onUpdate() throws Exception {
        ApplicationsListener cut = new ApplicationsListener(mockContext, mockProbeProbeManager, mockTimedApplicationTaskFactory, mockPermissionDeniedHandler);

        Probe mockProbe = mock(Probe.class);

        cut.onUpdate(mockProbe);
        verify(mockProbeProbeManager).save(mockProbe);

    }

    @Test
    public void startListening() throws Exception {
        ApplicationsListener cut = new ApplicationsListener(mockContext, mockProbeProbeManager, mockTimedApplicationTaskFactory, mockPermissionDeniedHandler);

        TimedApplicationTask mockTimedApplicationTask = mock(TimedApplicationTask.class);
        when(mockTimedApplicationTaskFactory.create(cut, mockContext, mockPermissionDeniedHandler)).thenReturn(mockTimedApplicationTask);

        cut.startListening();
        verify(mockTimedApplicationTask).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Test
    public void stopListening() throws Exception {
        ApplicationsListener cut = new ApplicationsListener(mockContext, mockProbeProbeManager, mockTimedApplicationTaskFactory, mockPermissionDeniedHandler);

        cut.stopListening();

        TimedApplicationTask mockTimedApplicationTask = mock(TimedApplicationTask.class);
        when(mockTimedApplicationTaskFactory.create(cut, mockContext, mockPermissionDeniedHandler)).thenReturn(mockTimedApplicationTask);

        cut.startListening();
        cut.stopListening();

        verify(mockTimedApplicationTask).cancel(true);

    }

}