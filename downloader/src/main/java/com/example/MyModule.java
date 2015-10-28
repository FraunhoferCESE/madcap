package com.example;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;


import org.fraunhofer.cese.funf_sensor.backend.models.probeDataSetApi.ProbeDataSetApi;
import org.fraunhofer.cese.funf_sensor.backend.models.responseDataSetApi.ResponseDataSetApi;

import java.io.IOException;

/**
 *
 */
public class MyModule extends AbstractModule {


    @Override
    protected void configure() {
//        bind(ProbeDataSetApi.class);
//        bind(ResponseDataSetApi.class);
    }


    @Provides
    ResponseDataSetApi provideResponseDataSetApi() {
        System.out.println("Called");
        ResponseDataSetApi.Builder builder = new ResponseDataSetApi.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null)
                .setApplicationName("funfSensor")
                .setRootUrl("https://funfcese.appspot.com/_ah/api/");
//                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
//                    @Override
//                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
//                        abstractGoogleClientRequest.setDisableGZipContent(true);
//                    }
//                });
        System.out.println("Gonna return something");
        return builder.build();
    }

}
