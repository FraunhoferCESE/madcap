package org.fraunhofer.cese.funf_sensor.backend.servlets;

import org.fraunhofer.cese.funf_sensor.backend.models.ProbeEntry;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.fraunhofer.cese.funf_sensor.backend.OfyService.ofy;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.Channels;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;

import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.appengine.api.files.GSFileOptions.GSFileOptionsBuilder;


/**
 *
 */
public class HttpsGetServlet extends HttpServlet {

    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";
    private static final String FILE_HEADER = "id,timestamp,probeType,sensorData,userID";
    private static final Logger logger = Logger.getLogger(HttpsGetServlet.class.getName());

   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        logger.warning("Something reached the Servlet. Fly, you fools!");

        String fromTimestampString = request.getParameter("fromTimestamp");
        String toTimestampString = request.getParameter("toTimestamp");

        long fromTimestamp = Long.parseLong(fromTimestampString);
        long toTimestamp = Long.parseLong(toTimestampString);

        response.getWriter().print("Seems like it worked");


//        //Google Cloud Storage has a limit of only one upload a second. Just to ensure we don't bust that.
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//
//        List<ProbeEntry> entries = ofy().load().type(ProbeEntry.class).filter("timestamp >=", fromTimestamp).list();
//        entries.removeAll(ofy().load().type(ProbeEntry.class).filter("timestamp >", toTimestamp).list());
//        Collections.sort(entries);
//
//
//        GcsService gcsService = GcsServiceFactory.createGcsService();
//
//        FileService fileService = FileServiceFactory.getFileService();
//
//        GSFileOptionsBuilder optionsBuilder = new GSFileOptionsBuilder()
//                .setBucket("my_bucket") //bucket name not known yet
//                .setKey("DataFrom" + fromTimestamp + "To" + toTimestamp + "SavedAt"
//                        + System.currentTimeMillis() + ".csv")
//                .setContentDisposition("attachment; filename=DataFrom" + fromTimestamp
//                        + "To" + toTimestamp + "SavedAt" + System.currentTimeMillis() + ".csv");
//
//        AppEngineFile writableFile = null;
//
//        writableFile = fileService.createNewGSFile(optionsBuilder.build());
//
//
//        boolean lockForWrite = false;
//        FileWriteChannel writeChannel = null;
//
//        writeChannel = fileService.openWriteChannel(writableFile, lockForWrite);
//
//
//        PrintWriter writer = new PrintWriter(Channels.newWriter(writeChannel, "UTF-8"));
//
//        writer.append(FILE_HEADER);
//        writer.append(NEW_LINE_SEPARATOR);
//
//        for (ProbeEntry entry : entries) {
//            writer.append(entry.getId());
//            writer.append(COMMA_DELIMITER);
//            writer.append(entry.getTimestamp().toString());
//            writer.append(COMMA_DELIMITER);
//            writer.append(entry.getProbeType());
//            writer.append(COMMA_DELIMITER);
//            writer.append(entry.getSensorData());
//            writer.append(COMMA_DELIMITER);
//            writer.append(entry.getUserID());
//            writer.append(NEW_LINE_SEPARATOR);
//        }
//
//        writeChannel.closeFinally();
//
//        response.getWriter().println("I am done.");
//        response.getWriter().println("It might have worked.");

    }
}

