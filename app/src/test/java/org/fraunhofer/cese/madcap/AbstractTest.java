package org.fraunhofer.cese.madcap;

import android.content.Context;
import android.content.ContextWrapper;

import com.google.inject.AbstractModule;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import roboguice.RoboGuice;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, manifest = "src/main/AndroidManifest.xml", sdk = 21)
public abstract class AbstractTest {
    public final String TAG = getClass().getName();
    public Context appContext;

    @Before
    public void setUp() throws Exception {
        appContext = Mockito.spy(
                new ContextWrapper(RuntimeEnvironment.application.getApplicationContext()));
        MockitoAnnotations.initMocks(this);
        RoboGuice.overrideApplicationInjector(RuntimeEnvironment.application, new MyTestModule());
        RoboGuice.getInjector(appContext).injectMembers(this);
    }

    public void setUp(AbstractModule myTestModule) throws Exception {
        appContext = Mockito.spy(
                new ContextWrapper(RuntimeEnvironment.application.getApplicationContext()));
        MockitoAnnotations.initMocks(this);
        RoboGuice.overrideApplicationInjector(RuntimeEnvironment.application, myTestModule);
        RoboGuice.getInjector(appContext).injectMembers(this);
    }

    @After
    public void tearDown() {
        // Important call to remove old databases
        OpenHelperManager.releaseHelper();
        OpenHelperManager.setHelper(null);
        RoboGuice.Util.reset();
    }

    public class MyTestModule extends AbstractModule {
        @Override
        protected void configure() {
            // Necessary global mocks
        }
    }
}
