package org.fraunhofer.cese.madcap.util.daggerComponents;

import org.fraunhofer.cese.madcap.backend.probeEndpoint.ProbeEndpoint;
import org.fraunhofer.cese.madcap.cache.CacheConfig;
import org.fraunhofer.cese.madcap.cache.DatabaseAsyncTaskFactory;
import org.fraunhofer.cese.madcap.cache.RemoteUploadAsyncTaskFactory;
import org.fraunhofer.cese.madcap.util.daggerModules.GoogleAppEnginePipelineModule;

import dagger.Component;

/**
 * Created by MMueller on 9/22/2016.
 */

@Component( modules = {GoogleAppEnginePipelineModule.class})
public interface GoogleAppEnginePipelineComponent {
    CacheConfig provideCacheConfig();
    DatabaseAsyncTaskFactory provideDatabaseWriteAsyncTaskFactory();
    RemoteUploadAsyncTaskFactory provideRemoteUploadAsyncTaskFactory();
    ProbeEndpoint provideProbeDataSetApi();
}
