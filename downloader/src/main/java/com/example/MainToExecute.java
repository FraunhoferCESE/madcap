package com.example;


import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;

import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;


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

    private static final String BUCKETNAME = "c3s3d4t4dump";
    private static final String FILENAME = "testfile";
    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";
    private static final String FILE_HEADER = "id,timestamp,probeType,sensorData,userID";
    private static final GcsService gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());


    public static void main(String[] args) throws IOException {

        System.out.println("Howdy. I'm getting started.");

        GcsFilename filename = new GcsFilename(BUCKETNAME, FILENAME);

        System.out.println(gcsService.toString());

        System.out.println(filename.toString());

        System.out.println(GcsFileOptions.getDefaultInstance());

        GcsOutputChannel outputChannel = gcsService.createOrReplace(filename, GcsFileOptions.getDefaultInstance());

        System.out.println("Wonder if i reach this.");

        PrintWriter writer = new PrintWriter(Channels.newWriter(outputChannel, "UTF-8"));

        List<ProbeEntry> entries = getSomeDummyEntries();

        writer.append(FILE_HEADER);
        writer.append(NEW_LINE_SEPARATOR);

        for (ProbeEntry entry : entries) {
            writer.append(entry.getId());
            writer.append(COMMA_DELIMITER);
            writer.append(entry.getTimestamp().toString());
            writer.append(COMMA_DELIMITER);
            writer.append(entry.getProbeType());
            writer.append(COMMA_DELIMITER);
            writer.append(entry.getSensorData());
            writer.append(COMMA_DELIMITER);
            writer.append(entry.getUserID());
            writer.append(NEW_LINE_SEPARATOR);
        }

        writer.close();

        System.out.println("Might have worked.");
    }

    private static void writeToFile(GcsFilename filename, List<ProbeEntry> entries) throws IOException {

        @SuppressWarnings("resource")
        GcsOutputChannel outputChannel = gcsService.createOrReplace(filename, GcsFileOptions.getDefaultInstance());
        @SuppressWarnings("resource")
        ObjectOutputStream outputStream = new ObjectOutputStream(Channels.newOutputStream(outputChannel));
        outputStream.writeObject(entries.get(0));
        outputStream.close();
    }

    private static List<ProbeEntry> getSomeDummyEntries() {
        ProbeEntry entry = new ProbeEntry();
        entry.setTimestamp(123l);
        entry.setUserID("mario");
        entry.setId("f563ved8");
        entry.setProbeType("fooProbe");
        entry.setSensorData("rather little foo");

        ProbeEntry entry1 = new ProbeEntry();
        entry1.setTimestamp(523l);
        entry1.setUserID("luigi");
        entry1.setId("cu29d8yv8");
        entry1.setProbeType("anotherProbe");
        entry1.setSensorData("oooh, that don't look good.");

        ProbeEntry entry2 = new ProbeEntry();
        entry2.setTimestamp(333l);
        entry2.setUserID("daisy");
        entry2.setId("c0q0bnv90");
        entry2.setProbeType("temperatureProbe");
        entry2.setSensorData("It's getting hot in here.");

        ProbeEntry entry3 = new ProbeEntry();
        entry3.setTimestamp(341l);
        entry3.setUserID("yeti");
        entry3.setId("b8430s03f");
        entry3.setProbeType("probetyProbeProbe");
        entry3.setSensorData("nice Probe, dude.");

        ArrayList<ProbeEntry> result = new ArrayList<>();
        result.add(entry);
        result.add(entry1);
        result.add(entry2);
        result.add(entry3);

        return result;
    }
}
