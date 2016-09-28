package org.fraunhofer.cese.madcap.Probe.ActivityProbe;

import android.support.v4.content.LocalBroadcastManager;

import com.pathsense.android.sdk.location.PathsenseLocationProviderApi;

import org.fraunhofer.cese.madcap.factories.IntentFilterFactory;
import org.junit.After;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Created by MMueller on 9/21/2016.
 */

public class ActivityProbeTest{
    ActivityProbe cut;
    IntentFilterFactory mockIntentFilterFactory;
    PathsenseLocationProviderApi mockPApi;

    public final void setUp() throws Exception {
        //super.setUp();


        MockitoAnnotations.initMocks(this);
    }


    @After
    public void tearDown() {

    }

    @Test
    public void testOnEnable(){

        System.out.println(mockIntentFilterFactory);

        //cut = new ActivityProbe(mockIntentFilterFactory, mockPApi);
        cut.onEnable();

        verify(mockPApi, times(1)).requestActivityChanges(PathsenseActivityChangeBroadcastReceiver.class);

    }
}

