package org.fraunhofer.cese.funf_sensor;

import com.google.inject.AbstractModule;

import org.fraunhofer.cese.funf_sensor.appengine.GoogleAppEnginePipeline;
import org.fraunhofer.cese.funf_sensor.cache.Cache;
import org.fraunhofer.cese.funf_sensor.cache.DatabaseWriteAsyncTask;

/**
 * Created by Lucas on 10/6/2015.
 */
public class MyModule extends AbstractModule {

    @Override
    protected void configure() {
//        binder.bind(RemoteUploadAsyncTask.class).toProvider(RemoteUploadAsyncTaskProvider.class);
//        binder.bind(DatabaseWriteAsyncTask.class).toProvider(DatabaseWriteAsyncTaskProvider.class);
        binder.bind(Cache.class);
        binder.bind(GoogleAppEnginePipeline.class);
    }
}
