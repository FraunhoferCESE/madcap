package org.fraunhofer.cese.madcap.util.daggerModules;

import android.content.Context;
import android.net.ConnectivityManager;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import org.fraunhofer.cese.madcap.backend.probeEndpoint.ProbeEndpoint;
import org.fraunhofer.cese.madcap.cache.Cache;
import org.fraunhofer.cese.madcap.cache.CacheConfig;
import org.fraunhofer.cese.madcap.cache.DatabaseAsyncTaskFactory;
import org.fraunhofer.cese.madcap.cache.RemoteUploadAsyncTaskFactory;

import dagger.Module;
import dagger.Provides;

/**
 * Created by MMueller on 9/22/2016.
 */

@Module
public class CacheModule {

    @Provides
    Cache provideCache(Context context,
                       ConnectivityManager connManager,
                       CacheConfig config,
                       DatabaseAsyncTaskFactory dbWriteTaskFactory,
                       RemoteUploadAsyncTaskFactory uploadTaskFactory,
                       ProbeEndpoint appEngineApi){
        return new Cache(context, connManager, config, dbWriteTaskFactory, uploadTaskFactory, appEngineApi);
    }

    @Provides
    CacheConfig provideCacheConfig() {
        CacheConfig config = new CacheConfig();
        config.setMaxMemEntries(100);
        config.setMaxDbEntries(250);

        config.setMemForcedCleanupLimit(5000);
        config.setDbForcedCleanupLimit(30000); // Required to make sure upload doesn't exceed Google API limits for a single request

        config.setDbWriteInterval(2000); // 2 second minimum
        config.setUploadInterval(900000); // 15 minute minimum

        config.setUploadWifiOnly(false);
        config.setWriteToFile(false);

        return config;
    }

    @Provides
    DatabaseAsyncTaskFactory provideDatabaseWriteAsyncTaskFactory() {
        return new DatabaseAsyncTaskFactory();
    }

    @Provides
    RemoteUploadAsyncTaskFactory provideRemoteUploadAsyncTaskFactory() {
        return new RemoteUploadAsyncTaskFactory();
    }

    @Provides
    ProbeEndpoint provideProbeDataSetApi() {
        ProbeEndpoint.Builder builder = new ProbeEndpoint.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null)
                .setApplicationName("Fraunhofer MADCAP")
                .setRootUrl("https://madcap-dev1.appspot.com/_ah/api/");
        return builder.build();
    }
}
