package com.example;


import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;

import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.google.inject.Guice;
import com.google.inject.Injector;


import org.fraunhofer.cese.funf_sensor.backend.models.probeDataSetApi.model.ProbeEntry;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class MainToExecute {

    public static void main(String[] args) throws IOException {
        Injector injector = Guice.createInjector(new MyModule());
        DataPuller dp = new DataPuller(injector.getInstance(GcsService.class));
        dp.pullDataFromAppengine();
    }

//    private static List<ProbeEntry> getSomeDummyEntries() {
//        ProbeEntry entry = new ProbeEntry();
//        entry.setTimestamp(123l);
//        entry.setUserID("mario");
//        entry.setId("f563ved8");
//        entry.setProbeType("fooProbe");
//        entry.setSensorData("rather little foo");
//
//        ProbeEntry entry1 = new ProbeEntry();
//        entry1.setTimestamp(523l);
//        entry1.setUserID("luigi");
//        entry1.setId("cu29d8yv8");
//        entry1.setProbeType("anotherProbe");
//        entry1.setSensorData("oooh, that don't look good.");
//
//        ProbeEntry entry2 = new ProbeEntry();
//        entry2.setTimestamp(333l);
//        entry2.setUserID("daisy");
//        entry2.setId("c0q0bnv90");
//        entry2.setProbeType("temperatureProbe");
//        entry2.setSensorData("It's getting hot in here.");
//
//        ProbeEntry entry3 = new ProbeEntry();
//        entry3.setTimestamp(341l);
//        entry3.setUserID("yeti");
//        entry3.setId("b8430s03f");
//        entry3.setProbeType("probetyProbeProbe");
//        entry3.setSensorData("nice Probe, dude.");
//
//        ArrayList<ProbeEntry> result = new ArrayList<>();
//        result.add(entry);
//        result.add(entry1);
//        result.add(entry2);
//        result.add(entry3);
//
//        return result;
//    }
}
