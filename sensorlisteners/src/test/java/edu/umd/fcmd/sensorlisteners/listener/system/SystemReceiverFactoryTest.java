package edu.umd.fcmd.sensorlisteners.listener.system;

import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by MMueller on 1/3/2017.
 */
public class SystemReceiverFactoryTest {
    SystemListener mockSystemListener;
    Context mockContext;

    @Before
    public void setUp() throws Exception {
        mockSystemListener = mock(SystemListener.class);
        mockContext = mock(Context.class);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void constructorTest() throws Exception {
        SystemReceiverFactory cut = new SystemReceiverFactory();
    }

    @Test
    public void create() throws Exception {
        SystemReceiverFactory cut = new SystemReceiverFactory();

        cut.create(mockSystemListener, mockContext);
    }

}