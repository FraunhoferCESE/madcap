package org.fraunhofer.cese.madcap;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Handler;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.FenceApi;
import com.google.android.gms.awareness.SnapshotApi;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationServices;

import org.fraunhofer.cese.madcap.cache.CacheConfig;
import org.fraunhofer.cese.madcap.cache.CacheFactory;
import org.fraunhofer.cese.madcap.issuehandling.MadcapPermissionsManager;
import org.fraunhofer.cese.madcap.issuehandling.MadcapSensorNoAnswerReceivedHandler;
import org.fraunhofer.cese.madcap.util.MadcapBuildVersionProvider;

import java.util.Calendar;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import edu.umd.fcmd.sensorlisteners.issuehandling.PermissionsManager;
import edu.umd.fcmd.sensorlisteners.listener.applications.TimedApplicationTaskFactory;
import edu.umd.fcmd.sensorlisteners.listener.location.LocationConnectionCallbacks;
import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.model.system.BuildVersionProvider;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;

/**
 * This class defines the providers to use for dependency injection
 */
@SuppressWarnings({"SameReturnValue", "InstanceMethodNamingConvention", "MethodMayBeStatic", "TypeMayBeWeakened"})
@Module
class MyApplicationModule {


    /**
     * Local instance of the Application. Needed to provide the {@link Context} to injected classes.
     */
    private final Application application;

    /**
     * Constructor used to initiate the module with application-specific context
     *
     * @param application the calling application
     */
    MyApplicationModule(Application application) {
        this.application = application;
    }

    /**
     * Needed by the {@link org.fraunhofer.cese.madcap.cache.Cache}
     *
     * @return a Context object (probably the Application) to be used
     */
    @Provides
    Context provideContext() {
        return application;
    }

    @Provides
    @Named("FusedLocationProviderApi")
    GoogleApiClient provideFusedLocationProviderApiClient() {
        return new GoogleApiClient.Builder(application)
                .addApi(LocationServices.API)
                .build();
    }

    @Provides
    FusedLocationProviderApi provideFusedLocationProviderApi() {
        return LocationServices.FusedLocationApi;
    }

    @Provides
    @Named("AwarenessApi")
    GoogleApiClient provideAwarenessApiClient() {
        return new GoogleApiClient.Builder(application)
                .addApi(Awareness.API)
                .build();
    }

    @Provides
    Calendar provideCalendar() {
        return Calendar.getInstance();
    }


    /**
     * Needed by the DataCollectionService.
     *
     * @return a static FenceApi
     */
    @Provides
    FenceApi provideFenceApi() {
        return Awareness.FenceApi;
    }

    /**
     * Needed by the DataCollectionService.
     *
     * @return a statuc SnapshotApi
     */
    @Provides
    SnapshotApi provideSnapshotApi() {
        return Awareness.SnapshotApi;
    }

    @Provides
    Handler provideHeartBeatRunnerHandler() { return new Handler(); }

    /**
     * Needed by the DataCollectionService.
     *
     * @return a factory.
     */
    @Provides
    TimedApplicationTaskFactory provideTimedApplicationTask() {
        return new TimedApplicationTaskFactory();
    }

    @Provides
    MadcapSensorNoAnswerReceivedHandler provideSensorNoAnswerReceivedHandler() {
        return new MadcapSensorNoAnswerReceivedHandler();
    }

    @SuppressWarnings("unchecked")
    @Provides
    ProbeManager<Probe> provideProbeManager(CacheFactory cacheFactory) {
        return cacheFactory;
    }

    @Provides
    PermissionsManager providePermissionDeniedHandler(MadcapPermissionsManager madcapPermissionDeniedHandler) {
        return madcapPermissionDeniedHandler;
    }

    @Provides
    BuildVersionProvider provideBuildVersionProvider(MadcapBuildVersionProvider madcapBuildVersionProvider) {
        return madcapBuildVersionProvider;
    }

    @Provides
    GoogleApiClient.ConnectionCallbacks providesGoogleApiClientConnectionCallbacks() {
        return new LocationConnectionCallbacks();
    }

    @Provides
    GoogleApiClient.OnConnectionFailedListener providesGoogleApiClientConnectionFailedListener() {
        return new LocationConnectionCallbacks();
    }

    /**
     * Needed by the {@link org.fraunhofer.cese.madcap.cache.Cache}
     *
     * @return the ConnectivityManager to use
     */
    @Provides
    @Singleton
    final ConnectivityManager provideConnectivityManager() {
        return (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
    }


    /**
     * Needed by the {@link org.fraunhofer.cese.madcap.cache.Cache}
     *
     * @return the CacheConfig to use
     */
    @Provides
    CacheConfig provideCacheConfig() {
        CacheConfig config = new CacheConfig();
        config.setMaxMemEntries(40);
        config.setMaxDbEntries(1000);

        config.setMemForcedCleanupLimit(5000);
        config.setDbForcedCleanupLimit(30000); // value must ensure that we do not exceed Google API limits for a single request

        config.setDbWriteInterval(2000);
        config.setUploadInterval(900000);

        config.setUploadWifiOnly(true);

        return config;
    }

    @Provides
    BluetoothAdapter provideBluetoothAdapter() {
        return BluetoothAdapter.getDefaultAdapter();
    }

    @Provides
    @Named("SigninApi")
    GoogleApiClient providesGoogleSigninApiClient() {
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        return new GoogleApiClient.Builder(application)
                //.enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();
    }

    @Provides
    GoogleApiAvailability providesGoogleApiAvailability() {
        return GoogleApiAvailability.getInstance();
    }
}

