package edu.umd.fcmd.sensorlisteners.model;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.umd.fcmd.sensorlisteners.model.applications.ForegroundBackgroundEventsProbe;

/**
 * Created by MMueller on 11/22/2016.
 */
public class ForegroundBackgroundEventsProbeTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void getEventType() throws Exception {
        ForegroundBackgroundEventsProbe cut = new ForegroundBackgroundEventsProbe();

        cut.setEventType(1);
        Assert.assertEquals(cut.getEventType(), 1);
    }

    @Test
    public void setEventType() throws Exception {
        ForegroundBackgroundEventsProbe cut = new ForegroundBackgroundEventsProbe();

        cut.setEventType(1);
        Assert.assertEquals(cut.getEventType(), 1);
    }

    @Test
    public void getPackageName() throws Exception {
        ForegroundBackgroundEventsProbe cut = new ForegroundBackgroundEventsProbe();

        cut.setPackageName("packageName");
        Assert.assertEquals(cut.getPackageName(), "packageName");

        cut.setPackageName(null);
        Assert.assertEquals(cut.getPackageName(), null);
    }

    @Test
    public void setPackageName() throws Exception {
        ForegroundBackgroundEventsProbe cut = new ForegroundBackgroundEventsProbe();

        cut.setPackageName("packageName");
        Assert.assertEquals(cut.getPackageName(), "packageName");

        cut.setPackageName(null);
        Assert.assertEquals(cut.getPackageName(), null);
    }

    @Test
    public void getClassName() throws Exception {
        ForegroundBackgroundEventsProbe cut = new ForegroundBackgroundEventsProbe();

        cut.setClassName("className");
        Assert.assertEquals(cut.getClassName(), "className");

        cut.setClassName(null);
        Assert.assertEquals(cut.getClassName(), null);
    }

    @Test
    public void setClassName() throws Exception {
        ForegroundBackgroundEventsProbe cut = new ForegroundBackgroundEventsProbe();

        cut.setClassName("className");
        Assert.assertEquals(cut.getClassName(), "className");

        cut.setClassName(null);
        Assert.assertEquals(cut.getClassName(), null);
    }

    @Test
    public void getAccuracy() throws Exception {
        ForegroundBackgroundEventsProbe cut = new ForegroundBackgroundEventsProbe();

        cut.setAccuracy(1d);
        Assert.assertEquals(cut.getAccuracy(), 1d);
    }

    @Test
    public void setAccuracy() throws Exception {
        ForegroundBackgroundEventsProbe cut = new ForegroundBackgroundEventsProbe();

        cut.setAccuracy(1d);
        Assert.assertEquals(cut.getAccuracy(), 1d);
    }

    @Test
    public void getType() throws Exception {
        ForegroundBackgroundEventsProbe cut = new ForegroundBackgroundEventsProbe();

        Assert.assertEquals(cut.getType(), "ForegroundBackgroundEvent");
    }

    @Test
    public void testToString() throws Exception {
        ForegroundBackgroundEventsProbe cut = new ForegroundBackgroundEventsProbe();

        cut.setEventType(1);
        cut.setPackageName("PackageName");
        cut.setClassName("ClassName");
        cut.setAccuracy(1d);

        Assert.assertEquals(cut.toString(), "{\"eventType\": " + 1 +
                ", \"packageName\": " + "PackageName" +
                ", \"className\": " + "ClassName" +
                ", \"accuracy\": " + 1d +
                '}' );

        cut.setClassName(null);
        Assert.assertEquals(cut.toString(), "{\"eventType\": " + 1 +
                ", \"packageName\": " + "PackageName" +
                ", \"className\": " + "-" +
                ", \"accuracy\": " + 1d +
                '}' );
    }

}