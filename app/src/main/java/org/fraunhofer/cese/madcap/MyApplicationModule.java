package org.fraunhofer.cese.madcap;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.SnapshotApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import org.fraunhofer.cese.madcap.backend.probeEndpoint.ProbeEndpoint;
import org.fraunhofer.cese.madcap.cache.CacheConfig;
import org.fraunhofer.cese.madcap.cache.RemoteUploadAsyncTaskFactory;
import org.fraunhofer.cese.madcap.factories.JsonObjectFactory;
import org.fraunhofer.cese.madcap.issuehandling.GoogleApiClientConnectionIssueManager;
import org.fraunhofer.cese.madcap.issuehandling.MadcapPermissionDeniedHandler;

import java.util.Calendar;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import edu.umd.fcmd.sensorlisteners.listener.applications.TimedApplicationTask;
import edu.umd.fcmd.sensorlisteners.listener.applications.TimedApplicationTaskFactory;
import edu.umd.fcmd.sensorlisteners.listener.location.LocationServiceStatusReceiverFactory;
import edu.umd.fcmd.sensorlisteners.listener.location.TimedLocationTaskFactory;

/**
 * This class defines the providers to use for dependency injection
 */
@Module
class MyApplicationModule {

    /**
     * Local instane of the Application. Needed to provide the {@link Context} to injected classes.
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
     * Needed by the {@link org.fraunhofer.cese.madcap.cache.Cache} and {@link org.fraunhofer.cese.madcap.appengine.GoogleAppEnginePipeline}
     *
     * @return a Context object (probably the Application) to be used
     */
    @Provides
    Context provideContext() {
        return application;
    }

//    @Provides
//    GoogleApiClient.Builder provideBuilder(){
//        return new GoogleApiClient.Builder(application);
//    }
//
//    @Provides
//    GoogleApiClientBuilderFactory provideGoogleApiClientBuilderFactory(){
//        return new GoogleApiClientBuilderFactory();
//    }

    @Provides @Named("AwarenessApi")
    GoogleApiClient provideGoogleApiClient(){
        return new GoogleApiClient.Builder(application)
                .addApi(Awareness.API)
                .build();
    }

    @Provides
    Calendar provideCalendar(){
        return Calendar.getInstance();
    }


    /**
     * Needed by the DataCollectionService.
     *
     * @return a statuc SnapshotApi
     */
    @Provides
    SnapshotApi provideSnapshotApi(){
        return Awareness.SnapshotApi;
    }

    /**
     * Needed by the DataCollectionService.
     *
     * @return a factory.
     */
    @Provides
    TimedLocationTaskFactory provideTimedLocationTaskFactory(){ return new TimedLocationTaskFactory();}

    /**
     * Needed by the DataCollectionService.
     *
     * @return a factory.
     */
    @Provides
    TimedApplicationTaskFactory provideTimedApplicationTask(){ return new TimedApplicationTaskFactory();}

    /**
     * Needed by the DataCollectionService.
     *
     * @return a factory.
     */
    @Provides
    LocationServiceStatusReceiverFactory provideLocationServiceStatusReceiverFactory(){ return new LocationServiceStatusReceiverFactory(); }

    /**
     * Needed by the DataCollectionService.
     *
     * @return an issuemanager.
     */
    @Provides
    GoogleApiClientConnectionIssueManager provideGoogleConnectionIssueManager(){return new GoogleApiClientConnectionIssueManager();}

    /**
     * Needed by the DataCollectionService.
     *
     * @return an MadcapPermissionDeniedHandler.
     */
    @Provides
    MadcapPermissionDeniedHandler provideMadcapPermissionDeniedHandler(){ return new MadcapPermissionDeniedHandler(application);}

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
        config.setMaxMemEntries(R.integer.MaxMemEntries);
        config.setMaxDbEntries(R.integer.MaxDbEntries);

        config.setMemForcedCleanupLimit(R.integer.MemForcedCleanupLimit);
        config.setDbForcedCleanupLimit(R.integer.DbForcedCleanupLimit); // value must ensure that we do not exceed Google API limits for a single request

        config.setDbWriteInterval((long) R.integer.DbWriteInterval);
        config.setUploadInterval((long) R.integer.UploadInterval);

        Resources res = application.getResources();

        config.setUploadWifiOnly(res.getBoolean(R.bool.UploadWifiOnly));
        config.setWriteToFile(res.getBoolean(R.bool.WriteToFile));

        return config;
    }

//    @Provides
//    ProbeDataSetApi provideProbeDataSetApi() {
//        ProbeDataSetApi.Builder builder = new ProbeDataSetApi.Builder(AndroidHttp.newCompatibleTransport(),
//                new AndroidJsonFactory(), null)
//                .setApplicationName("funfSensor")
//                .setRootUrl("http://192.168.0.100:8080/_ah/api/")
//                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
//                    @Override
//                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
//                        abstractGoogleClientRequest.setDisableGZipContent(true);
//                    }
//                });
//        return builder.build();
//    }

    /**
     * Needed by the {@link org.fraunhofer.cese.madcap.cache.Cache} and the {@link RemoteUploadAsyncTaskFactory}
     *
     * @return the ProbeEndpoint to use
     */
    @Provides
    static ProbeEndpoint provideProbeDataSetApi() {
        ProbeEndpoint.Builder builder = new ProbeEndpoint.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null)
                .setApplicationName("Fraunhofer MADCAP")
                .setRootUrl("https://madcap-dev1.appspot.com/_ah/api/");
//                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
//                    @Override
//                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
//                        abstractGoogleClientRequest.setDisableGZipContent(true);
//                    }
//                });
        return builder.build();
    }

    @Provides
    BluetoothAdapter provideBluetoothAdapter(){
        return BluetoothAdapter.getDefaultAdapter();
    }

    @Provides
    JsonObjectFactory provideJsonObjectFactory(){
        return null;
    }


}
