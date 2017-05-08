package org.fraunhofer.cese.madcap;

import android.app.Application;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.FenceApi;
import com.google.android.gms.awareness.SnapshotApi;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import org.fraunhofer.cese.madcap.cache.CacheFactory;
import org.fraunhofer.cese.madcap.issuehandling.MadcapPermissionsManager;
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

import static android.content.Context.TELEPHONY_SERVICE;

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
    @Singleton
    @Named("HeartbeatHandler")
    Handler provideHeartBeatRunnerHandler() {
        return new Handler();
    }

    @Provides
    @Singleton
    @Named("RemoteConfigUpdateHandler")
    Handler provideRemoteConfigUpdateHandler() {
        return new Handler();
    }

    @Provides
    FirebaseRemoteConfig provideFirebaseRemoteConfig() {
        return FirebaseRemoteConfig.getInstance();
    }

    @Provides
    SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    /**
     * Needed by the DataCollectionService.
     *
     * @return a factory.
     */
    @Provides
    TimedApplicationTaskFactory provideTimedApplicationTask() {
        return new TimedApplicationTaskFactory();
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

    @Provides
    NotificationManager provideNotificationManager() {
        return (NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Provides
    TelephonyManager provideTelephonyManager() {
        return (TelephonyManager) application.getSystemService(TELEPHONY_SERVICE);
    }

    @Provides
    WifiManager provideWifiManager() {
        return (WifiManager) application.getSystemService(Context.WIFI_SERVICE);
    }

    @Provides
    NfcAdapter provideNfcAdapter() {
        return NfcAdapter.getDefaultAdapter(application);
    }

}

