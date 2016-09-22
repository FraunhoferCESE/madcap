package org.fraunhofer.cese.madcap.util.daggerComponents;

import android.content.Context;
import android.net.ConnectivityManager;

import org.fraunhofer.cese.madcap.backend.probeEndpoint.ProbeEndpoint;
import org.fraunhofer.cese.madcap.cache.Cache;
import org.fraunhofer.cese.madcap.cache.CacheConfig;
import org.fraunhofer.cese.madcap.cache.DatabaseAsyncTaskFactory;
import org.fraunhofer.cese.madcap.cache.RemoteUploadAsyncTaskFactory;
import org.fraunhofer.cese.madcap.util.daggerModules.CacheModule;

import dagger.Component;

/**
 * Created by MMueller on 9/22/2016.
 */

@Component(modules = {CacheModule.class})
public interface CacheComponent {
    Cache provideCache(Context context,
                       ConnectivityManager connManager,
                       CacheConfig config,
                       DatabaseAsyncTaskFactory dbWriteTaskFactory,
                       RemoteUploadAsyncTaskFactory uploadTaskFactory,
                       ProbeEndpoint appEngineApi);
    CacheConfig provideCacheConfig();
    DatabaseAsyncTaskFactory provideDatabaseWriteAsyncTaskFactory();
    RemoteUploadAsyncTaskFactory provideRemoteUploadAsyncTaskFactory();
    ProbeEndpoint provideProbeDataSetApi();

}
