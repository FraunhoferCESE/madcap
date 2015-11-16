package org.fraunhofer.cese.funf_sensor.backend.servlets;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.googlecode.objectify.cmd.Query;
import com.opencsv.CSVWriter;

import org.fraunhofer.cese.funf_sensor.backend.models.ProbeEntry;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.Channels;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.fraunhofer.cese.funf_sensor.backend.OfyService.ofy;

public class WriteQueue extends HttpServlet {

    private static final String BUCKETNAME = "c3s3d4t4dump";
    private static final String[] FILE_HEADER = {"id", "userID", "timestamp", "probeType", "sensorData"};

    private static final int BUFFER_SIZE = 8000;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Query<ProbeEntry> query = ofy().load().type(ProbeEntry.class).limit(BUFFER_SIZE);

        String cursorStr = req.getParameter("cursor");
        if (cursorStr != null)
            query = query.startAt(Cursor.fromWebSafeString(cursorStr));

        int fileNum = 1;
        if (req.getParameter("fileNum") != null)
            fileNum = Integer.parseInt(req.getParameter("fileNum")) + 1;

        String path = req.getParameter("path");
        if (path == null)
            path = "dump_" + System.currentTimeMillis();

        String filename = String.format(path + File.separator + "chunk_%03d.zip", fileNum);
        GcsFilename fullpath = new GcsFilename(BUCKETNAME, filename);

        GcsService gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());
        GcsOutputChannel outputChannel = gcsService.createOrReplace(fullpath, GcsFileOptions.getDefaultInstance());
        ZipOutputStream zipOutputStream = new ZipOutputStream(Channels.newOutputStream(outputChannel));
        zipOutputStream.putNextEntry(new ZipEntry(String.format("chunk_%03d.csv", fileNum)));
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(zipOutputStream));
        writer.writeNext(FILE_HEADER);

        boolean continu = false;
        QueryResultIterator<ProbeEntry> iterator = query.iterator();
        while (iterator.hasNext()) {
            ProbeEntry entry = iterator.next();
            writer.writeNext(new String[]{entry.getId(), entry.getUserID(), entry.getTimestamp().toString(), entry.getProbeType(), entry.getSensorData()});
            continu = true;
        }
        //closing stuff. data is send when stream/channel is closed
        writer.flush();
        writer.close();
        zipOutputStream.close();
        outputChannel.close();

        if (continu) {

            Cursor cursor = iterator.getCursor();
            Queue queue = QueueFactory.getDefaultQueue();
            queue.add(TaskOptions.Builder.withUrl("/enqueue")
                            .param("cursor", cursor.toWebSafeString())
                            .param("path", path)
                            .param("fileNum", Integer.toString(fileNum))
                            .method(TaskOptions.Method.GET)
            );
        }
    }
}
