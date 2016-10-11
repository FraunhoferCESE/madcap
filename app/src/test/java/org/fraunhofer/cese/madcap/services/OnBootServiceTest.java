package org.fraunhofer.cese.madcap.services;

import android.content.Intent;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;

/**
 * Created by MMueller on 10/11/2016.
 */
public class OnBootServiceTest {
    OnBootService cut;

    @Before
    public void setUp() throws Exception {
        cut = new OnBootService();
    }

    @After
    public void tearDown() throws Exception {
        cut = new OnBootService();
    }

    @Test
    public void onBind() throws Exception {
        Intent mockIntent = spy(Intent.class);

        Assert.assertNull(cut.onBind(mockIntent));
    }

    @Test
    public void onDestroy() throws Exception {

    }

    @Test
    public void onCreate() throws Exception {

    }

    @Test
    public void onStartCommand() throws Exception {

    }

}