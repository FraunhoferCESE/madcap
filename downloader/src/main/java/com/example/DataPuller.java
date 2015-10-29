package com.example;

/**
 *
 */


import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.ListOptions;
import com.google.appengine.tools.cloudstorage.RetryParams;

import org.fraunhofer.cese.funf_sensor.backend.models.responseDataSetApi.ResponseDataSetApi;
import org.fraunhofer.cese.funf_sensor.backend.models.responseDataSetApi.model.ResponseDataSet;
import org.fraunhofer.cese.funf_sensor.backend.models.responseDataSetApi.model.ProbeEntry;

import java.io.PrintWriter;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.List;

import java.io.IOException;


public class DataPuller {

    private static final String BUCKETNAME = "c3s3d4t4dump";
    private static final String FILENAME = "testfile";
    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";
    private static final String FILE_HEADER = "id,timestamp,probeType,sensorData,userID";
    private final GcsService gcsService;

    public DataPuller(GcsService service) {
        this.gcsService = service;
    }

    public ResponseDataSetApi responseDataSetApi;

    public void pullDataFromAppengine() throws IOException {
        System.out.println("Howdy. I'm getting started.");

        GcsFilename filename = new GcsFilename(BUCKETNAME, FILENAME);

        System.out.println(gcsService.toString());

        System.out.println(filename.toString());

        System.out.println(GcsFileOptions.getDefaultInstance());
        gcsService.list(BUCKETNAME, ListOptions.DEFAULT);

//        GcsOutputChannel outputChannel = gcsService.createOrReplace(filename, GcsFileOptions.getDefaultInstance());

    }

}
