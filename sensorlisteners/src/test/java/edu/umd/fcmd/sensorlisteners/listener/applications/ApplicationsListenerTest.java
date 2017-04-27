package edu.umd.fcmd.sensorlisteners.listener.applications;

import android.content.Context;
import android.os.AsyncTask;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.umd.fcmd.sensorlisteners.issuehandling.PermissionsManager;
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
    private PermissionsManager mockPermissionsManager;

    @Before
    public void setUp() throws Exception {
        mockContext = mock(Context.class);
        mockProbeProbeManager = (ProbeManager<Probe>) mock(ProbeManager.class);
        mockTimedApplicationTaskFactory = mock(TimedApplicationTaskFactory.class);
        mockPermissionsManager = mock(PermissionsManager.class);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void constructorTest(){
        ApplicationsListener cut = new ApplicationsListener(mockContext, mockProbeProbeManager, mockTimedApplicationTaskFactory, mockPermissionsManager);
    }

    @Test
    public void onUpdate() throws Exception {
        ApplicationsListener cut = new ApplicationsListener(mockContext, mockProbeProbeManager, mockTimedApplicationTaskFactory, mockPermissionsManager);

        Probe mockProbe = mock(Probe.class);

        cut.onUpdate(mockProbe);
        verify(mockProbeProbeManager).save(mockProbe);

    }

    @Test
    public void startListening() throws Exception {
        ApplicationsListener cut = new ApplicationsListener(mockContext, mockProbeProbeManager, mockTimedApplicationTaskFactory, mockPermissionsManager);

        TimedApplicationTask mockTimedApplicationTask = mock(TimedApplicationTask.class);
        when(mockTimedApplicationTaskFactory.create(cut, mockContext, mockPermissionsManager)).thenReturn(mockTimedApplicationTask);

        cut.startListening();
        verify(mockTimedApplicationTask).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Test
    public void stopListening() throws Exception {
        ApplicationsListener cut = new ApplicationsListener(mockContext, mockProbeProbeManager, mockTimedApplicationTaskFactory, mockPermissionsManager);

        cut.stopListening();

        TimedApplicationTask mockTimedApplicationTask = mock(TimedApplicationTask.class);
        when(mockTimedApplicationTaskFactory.create(cut, mockContext, mockPermissionsManager)).thenReturn(mockTimedApplicationTask);

        cut.startListening();
        cut.stopListening();

        verify(mockTimedApplicationTask).cancel(true);

    }

}