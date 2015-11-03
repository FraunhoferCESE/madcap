package com.example;

import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 *
 */
public class MyModule extends AbstractModule {


    @Override
    protected void configure() {
    }


    @Provides
    GcsService provideGcsService() {
        return GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());
    }

}
