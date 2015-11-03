package org.fraunhofer.cese.funf_sensor;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import org.fraunhofer.cese.funf_sensor.appengine.GoogleAppEnginePipeline;
import org.fraunhofer.cese.funf_sensor.backend.models.probeDataSetApi.ProbeDataSetApi;
import org.fraunhofer.cese.funf_sensor.cache.Cache;
import org.fraunhofer.cese.funf_sensor.cache.CacheConfig;
import org.fraunhofer.cese.funf_sensor.cache.DatabaseAsyncTaskFactory;
import org.fraunhofer.cese.funf_sensor.cache.RemoteUploadAsyncTaskFactory;

import java.io.IOException;

/**
 * Created by Lucas on 10/6/2015.
 */
public class MyModule extends AbstractModule {

    @Override
    protected void configure() {
        binder.bind(Cache.class);
        binder.bind(GoogleAppEnginePipeline.class);
    }

    @Provides
    CacheConfig provideCacheConfig() {
        CacheConfig config = new CacheConfig();
        config.setMaxMemEntries(2);
        config.setMaxDbEntries(5);

        config.setMemForcedCleanupLimit(5000);
        config.setDbForcedCleanupLimit(30000); // Required to make sure upload doesn't exceed Google API limits for a single request

        config.setDbWriteInterval(1000); // 2 second minimum
        config.setUploadInterval(5000); // 15 minute minimum

        config.setUploadWifiOnly(true);
        config.setWriteToFile(false);

        return config;
    }

//    @Provides
//    CacheConfig provideCacheConfig() {
//        CacheConfig config = new CacheConfig();
//        config.setMaxMemEntries(100);
//        config.setMaxDbEntries(250);
//
//        config.setMemForcedCleanupLimit(5000);
//        config.setDbForcedCleanupLimit(30000); // Required to make sure upload doesn't exceed Google API limits for a single request
//
//        config.setDbWriteInterval(2000); // 2 second minimum
//        config.setUploadInterval(75000); // 15 minute minimum
//
//        config.setUploadWifiOnly(true);
//        config.setWriteToFile(true);
//
//        return config;
//    }

    @Provides
    DatabaseAsyncTaskFactory provideDatabaseWriteAsyncTaskFactory() {
        return new DatabaseAsyncTaskFactory();
    }

    @Provides
    RemoteUploadAsyncTaskFactory provideRemoteUploadAsyncTaskFactory() {
        return new RemoteUploadAsyncTaskFactory();
    }

    @Provides
    ProbeDataSetApi provideProbeDataSetApi() {
        ProbeDataSetApi.Builder builder = new ProbeDataSetApi.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null)
                .setApplicationName("funfSensor")
                .setRootUrl("http://192.168.0.100:8080/_ah/api/")
                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                    @Override
                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                        abstractGoogleClientRequest.setDisableGZipContent(true);
                    }
                });
        return builder.build();
    }

//    @Provides
//    ProbeDataSetApi provideProbeDataSetApi() {
//        ProbeDataSetApi.Builder builder = new ProbeDataSetApi.Builder(AndroidHttp.newCompatibleTransport(),
//                new AndroidJsonFactory(), null)
//                .setRootUrl("https://funfcese.appspot.com/_ah/api/");
////                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
////                    @Override
////                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
////                        abstractGoogleClientRequest.setDisableGZipContent(true);
////                    }
////                });
//        return builder.build();
//    }

}
