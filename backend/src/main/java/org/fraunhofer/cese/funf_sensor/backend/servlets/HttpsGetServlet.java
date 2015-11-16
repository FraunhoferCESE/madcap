package org.fraunhofer.cese.funf_sensor.backend.servlets;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.opencsv.CSVWriter;

import org.fraunhofer.cese.funf_sensor.backend.models.ProbeEntry;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.Channels;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.fraunhofer.cese.funf_sensor.backend.OfyService.ofy;


/**
 *
 */
public class HttpsGetServlet extends HttpServlet {

    private static final String BUCKETNAME = "c3s3d4t4dump";
    private static final String[] FILE_HEADER = {"id", "userID", "timestamp", "probeType", "sensorData"};
    private static final Logger logger = Logger.getLogger(HttpsGetServlet.class.getName());

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        logger.warning("Something reached the Servlet. Fly, you fools!");

        if (request.getParameter("password") != null && request.getParameter("password").equals("swordfish")) {

            //Getting the timestamps out of the request
            String fromTimestampString = request.getParameter("fromTimestamp");
            String toTimestampString = request.getParameter("toTimestamp");

            long fromTimestamp = Long.parseLong(fromTimestampString);
            long toTimestamp = Long.parseLong(toTimestampString);

            //Google Cloud Storage has a limit of only one upload a second. Just to ensure we don't bust that.
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //Getting the required entries using Objectify. CURRENTLY NOT WORKING
//            List<ProbeEntry> entries = ofy().load().type(ProbeEntry.class).filter("timestamp >=", fromTimestamp).list();
//            entries.removeAll(ofy().load().type(ProbeEntry.class).filter("timestamp >", toTimestamp).list());
//            Collections.sort(entries);

            List<ProbeEntry> entries = ofy().load().type(ProbeEntry.class)
                    .filter("timestamp >=", fromTimestamp)
                    .filter("timestamp <", toTimestamp)
                    .list();

            //using dummy entries
//        entries.addAll(getDummyEntries());

            response.getWriter().println("Trying to write " + entries.size() + " entries to csv-files.");


            //opening streams/channels
            String filenameOnly = "From" + fromTimestamp + "To" + toTimestamp + ".zip";
            GcsFilename filename = new GcsFilename(BUCKETNAME, filenameOnly);
            GcsService gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());
            GcsOutputChannel outputChannel = gcsService.createOrReplace(filename, GcsFileOptions.getDefaultInstance());
            ZipOutputStream zipOutputStream = new ZipOutputStream(Channels.newOutputStream(outputChannel));
            zipOutputStream.putNextEntry(new ZipEntry("From" + fromTimestamp + "To" + toTimestamp + ".csv"));
//            PrintWriter writer = new PrintWriter(zipOutputStream);
//        PrintWriter writer = new PrintWriter(Channels.newOutputStream(outputChannel));


            CSVWriter writer = new CSVWriter(new OutputStreamWriter(zipOutputStream));
            writer.writeNext(FILE_HEADER);
            for (ProbeEntry entry : entries) {
                writer.writeNext(new String[]{entry.getId(), entry.getUserID(), entry.getTimestamp().toString(), entry.getProbeType(), entry.getSensorData()});
            }

            //closing stuff. data is send when stream/channel is closed
            writer.flush();
            writer.close();
            zipOutputStream.close();
            outputChannel.close();

            response.getWriter().println("Seems like it worked");
        } else {
            response.getWriter().println("Request has not been executed. Guess why!");
        }
    }
}

