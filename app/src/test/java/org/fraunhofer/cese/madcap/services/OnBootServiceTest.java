package org.fraunhofer.cese.madcap.services;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static com.pathsense.locationengine.lib.detectionLogic.b.s;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInResult;

/**
 * Created by MMueller on 10/11/2016.
 */
public class OnBootServiceTest {
    OnBootService cut;

    @Before
    public void setUp() throws Exception {
        cut = new OnBootService();
        Context mockContext = spy(Context.class);
        cut.setContext(mockContext);
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
        //cut.onDestroy();
    }

    @Test
    public void onCreate() throws Exception {
        //cut.onCreate();
    }

    @Test
    public void onStartCommand() throws Exception {
        Intent mockIntent = spy(Intent.class);
        int mockFlag = 1;
        int startId = 1;
        //Assert.assertEquals(cut.onStartCommand(mockIntent, mockFlag, startId), 1);
    }

}