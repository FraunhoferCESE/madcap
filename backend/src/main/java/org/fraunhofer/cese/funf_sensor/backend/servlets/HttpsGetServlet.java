package org.fraunhofer.cese.funf_sensor.backend.servlets;

import org.fraunhofer.cese.funf_sensor.backend.models.ProbeEntry;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.fraunhofer.cese.funf_sensor.backend.OfyService.ofy;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.RetryParams;

import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;


/**
 *
 */
public class HttpsGetServlet extends HttpServlet {

    private static final String BUCKETNAME = "c3s3d4t4dump";
    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";
    private static final String FILE_HEADER = "id,timestamp,probeType,sensorData,userID";
    private static final Logger logger = Logger.getLogger(HttpsGetServlet.class.getName());

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        logger.warning("Something reached the Servlet. Fly, you fools!");

        if(request.getParameter("password").equals("swordfish")) {

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
            List<ProbeEntry> entries = ofy().load().type(ProbeEntry.class).filter("timestamp >=", fromTimestamp).list();
            entries.removeAll(ofy().load().type(ProbeEntry.class).filter("timestamp >", toTimestamp).list());
            Collections.sort(entries);

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
            PrintWriter writer = new PrintWriter(zipOutputStream);
//        PrintWriter writer = new PrintWriter(Channels.newOutputStream(outputChannel));

            //writing the file
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

            //closing stuff. data is send when stream/channel is closed
            writer.close();
            zipOutputStream.close();
            outputChannel.close();


            response.getWriter().println("Seems like it worked");
        }else{
            response.getWriter().println("Request has not been executed. Guess why!");
        }
    }

    private List<ProbeEntry> getDummyEntries() {
        ProbeEntry entry1 = new ProbeEntry();
        ProbeEntry entry2 = new ProbeEntry();
        ProbeEntry entry3 = new ProbeEntry();

        entry1.setId("4fc6dc23-7359-4b23-b2e8-71d605211a8d");
        entry1.setTimestamp(1446218918l);
        entry1.setProbeType("org.fraunhofer.cese.funf_sensor.Probe.AudioProbe");
        entry1.setSensorData("{\"mExtras\":{\"CALL STATE\":\"IDLE\"},\"mFlags\":0,\"mWindowMode\":0,\"timestamp\":1446218918.523}");
        entry1.setUserID("He");

        entry2.setId("3b54adc4-7b39-42b1-8867-8e96fea64827");
        entry2.setTimestamp(1446218929l);
        entry2.setProbeType("org.fraunhofer.cese.funf_sensor.Probe.PowerProbe");
        entry2.setSensorData("{\"members\":{\"app1\":{\"imp\":100,\"imprc\":0,\"pcs\":\"org.fraunhofer.cese.funf_sensor\",\"pkg1\":\"org.fraunhofer.cese.funf_sensor\"},\"app10\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.nuance.swype.input\",\"pkg1\":\"com.nuance.swype.input\"},\"app11\":{\"imp\":300,\"imprc\":0,\"pcs\":\"com.google.android.gms\",\"pkg1\":\"com.google.android.gms\"},\"app12\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.samsung.android.app.watchmanagerstub\",\"pkg1\":\"com.samsung.android.app.watchmanagerstub\"},\"app13\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.sec.android.app.controlpanel\",\"pkg1\":\"com.sec.android.app.controlpanel\"},\"app14\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.samsung.helphub\",\"pkg1\":\"com.samsung.helphub\"},\"app15\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.sec.android.service.cm\",\"pkg1\":\"com.sec.android.service.cm\"},\"app16\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.samsung.android.app.assistantmenu\",\"pkg1\":\"com.samsung.android.app.assistantmenu\"},\"app17\":{\"imp\":300,\"impcom17\":\"ComponentInfo{com.google.android.gms/com.google.android.gms.droidguard.DroidGuardService}\",\"imprc\":2,\"pcs\":\"com.google.android.gms.unstable\",\"pkg1\":\"com.google.android.gms\"},\"app18\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.google.android.partnersetup\",\"pkg1\":\"com.google.android.partnersetup\"},\"app19\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.sec.android.SimpleWidget\",\"pkg1\":\"com.sec.android.SimpleWidget\"},\"app2\":{\"imp\":300,\"impcom2\":\"ComponentInfo{com.google.android.gms/com.google.android.gms.clearcut.service.ClearcutLoggerService}\",\"imprc\":2,\"pcs\":\"com.google.process.gapps\",\"pkg1\":\"com.google.android.syncadapters.contacts\",\"pkg2\":\"com.google.android.gms\",\"pkg3\":\"com.google.android.gsf\",\"pkg4\":\"com.google.android.gsf.login\"},\"app20\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.sec.android.provider.badge\",\"pkg1\":\"com.sec.android.provider.badge\"},\"app21\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.osp.app.signin\",\"pkg1\":\"com.osp.app.signin\"},\"app22\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.android.settings\",\"pkg1\":\"com.android.settings\"},\"app23\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.android.musicfx\",\"pkg1\":\"com.android.musicfx\"},\"app24\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.android.contacts\",\"pkg1\":\"com.android.contacts\"},\"app25\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.sec.android.gallery3d\",\"pkg1\":\"com.sec.android.gallery3d\"},\"app26\":{\"imp\":300,\"imprc\":0,\"pcs\":\"com.amazon.mShop.android\",\"pkg1\":\"com.amazon.mShop.android\"},\"app27\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.sec.android.inputmethod\",\"pkg1\":\"com.sec.android.inputmethod\"},\"app28\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.android.defcontainer\",\"pkg1\":\"com.android.defcontainer\"},\"app29\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.android.vending\",\"pkg1\":\"com.android.vending\"},\"app3\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.sec.android.app.launcher\",\"pkg1\":\"com.sec.android.app.launcher\"},\"app30\":{\"imp\":300,\"imprc\":0,\"pcs\":\"com.android.exchange\",\"pkg1\":\"com.android.exchange\"},\"app31\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.sec.android.widgetapp.activeapplicationwidget\",\"pkg1\":\"com.sec.android.widgetapp.activeapplicationwidget\"},\"app32\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.android.mms\",\"pkg1\":\"com.android.mms\"},\"app33\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.sec.android.widgetapp.ap.hero.accuweather\",\"pkg1\":\"com.sec.android.widgetapp.ap.hero.accuweather\"},\"app34\":{\"imp\":300,\"imprc\":0,\"pcs\":\"com.accuweather.android\",\"pkg1\":\"com.accuweather.android\"},\"app35\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.textra\",\"pkg1\":\"com.textra\"},\"app36\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.google.android.apps.plus\",\"pkg1\":\"com.google.android.apps.plus\"},\"app37\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.google.android.googlequicksearchbox:search\",\"pkg1\":\"com.google.android.googlequicksearchbox\"},\"app38\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.sec.knox.bridge\",\"pkg1\":\"com.sec.knox.bridge\"},\"app39\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.sec.android.app.FileShareServer\",\"pkg1\":\"com.sec.android.app.FileShareServer\"},\"app4\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.estrongs.android.pop\",\"pkg1\":\"com.estrongs.android.pop\"},\"app40\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.android.externalstorage\",\"pkg1\":\"com.android.externalstorage\"},\"app41\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.google.android.talk\",\"pkg1\":\"com.google.android.talk\"},\"app42\":{\"imp\":400,\"imprc\":0,\"pcs\":\"android.process.media\",\"pkg1\":\"com.android.providers.media\",\"pkg2\":\"com.android.providers.downloads\",\"pkg3\":\"com.android.providers.downloads.ui\"},\"app43\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.android.documentsui\",\"pkg1\":\"com.android.documentsui\"},\"app44\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.groupon\",\"pkg1\":\"com.groupon\"},\"app45\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.microsoft.office.word\",\"pkg1\":\"com.microsoft.office.word\"},\"app46\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.microsoft.office.powerpoint\",\"pkg1\":\"com.microsoft.office.powerpoint\"},\"app47\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.microsoft.office.excel\",\"pkg1\":\"com.microsoft.office.excel\"},\"app48\":{\"imp\":300,\"imprc\":0,\"pcs\":\"com.pushbullet.android:background\",\"pkg1\":\"com.pushbullet.android\"},\"app49\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.pushbullet.android\",\"pkg1\":\"com.pushbullet.android\"},\"app5\":{\"imp\":200,\"impcom5\":\"ComponentInfo{com.google.android.gms/com.google.android.location.geofencer.service.GeofenceProviderService}\",\"imprc\":2,\"pcs\":\"com.google.android.gms.persistent\",\"pkg1\":\"com.google.android.gms\"},\"app50\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.lookout\",\"pkg1\":\"com.lookout\"},\"app51\":{\"imp\":300,\"imprc\":0,\"pcs\":\"com.sec.spp.push\",\"pkg1\":\"com.sec.spp.push\"},\"app52\":{\"imp\":130,\"impcom52\":\"ComponentInfo{com.touchtype.swiftkey/com.touchtype.KeyboardService}\",\"imprc\":2,\"pcs\":\"com.touchtype.swiftkey\",\"pkg1\":\"com.touchtype.swiftkey\"},\"app53\":{\"imp\":300,\"imprc\":0,\"pcs\":\"com.sec.android.app.keyguard\",\"pkg1\":\"com.sec.android.app.keyguard\"},\"app54\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.vlingo.midas\",\"pkg1\":\"com.vlingo.midas\"},\"app55\":{\"imp\":400,\"impcom55\":\"ComponentInfo{com.samsung.SMT/com.samsung.SMT.SamsungTTSService}\",\"imprc\":2,\"pcs\":\"com.samsung.SMT\",\"pkg1\":\"com.samsung.SMT\"},\"app56\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.sec.android.daemonapp\",\"pkg1\":\"com.sec.android.daemonapp\"},\"app57\":{\"imp\":400,\"imprc\":0,\"pcs\":\"ccc71.bmw\",\"pkg1\":\"ccc71.bmw\"},\"app58\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.starbucks.mobilecard\",\"pkg1\":\"com.starbucks.mobilecard\"},\"app59\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.whatsapp\",\"pkg1\":\"com.whatsapp\"},\"app6\":{\"imp\":400,\"imprc\":0,\"pcs\":\"org.kman.AquaMail\",\"pkg1\":\"org.kman.AquaMail\"},\"app60\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.google.android.apps.googlevoice\",\"pkg1\":\"com.google.android.apps.googlevoice\"},\"app61\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.wssyncmldm\",\"pkg1\":\"com.wssyncmldm\"},\"app62\":{\"imp\":130,\"imprc\":0,\"pcs\":\"com.samsung.android.MtpApplication\",\"pkg1\":\"com.samsung.android.MtpApplication\"},\"app63\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.sec.android.pagebuddynotisvc\",\"pkg1\":\"com.sec.android.pagebuddynotisvc\"},\"app64\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.samsung.android.providers.context\",\"pkg1\":\"com.samsung.android.providers.context\"},\"app65\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.smlds\",\"pkg1\":\"com.smlds\"},\"app66\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.sec.android.app.parser\",\"pkg1\":\"com.sec.android.app.parser\"},\"app67\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.sec.android.service.sm\",\"pkg1\":\"com.sec.android.service.sm\"},\"app68\":{\"imp\":200,\"impcom68\":\"ComponentInfo{com.google.android.backuptransport/com.google.android.backup.BackupTransportService}\",\"imprc\":2,\"pcs\":\"com.google.process.location\",\"pkg1\":\"com.google.android.backuptransport\"},\"app69\":{\"imp\":200,\"impcom69\":\"ComponentInfo{com.sec.android.sviewcover/com.sec.android.sviewcover.SViewCoverBaseService}\",\"imprc\":2,\"pcs\":\"com.sec.android.sviewcover\",\"pkg1\":\"com.sec.android.sviewcover\"},\"app7\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.surpax.ledflashlight.panel\",\"pkg1\":\"com.surpax.ledflashlight.panel\"},\"app70\":{\"imp\":100,\"imprc\":0,\"pcs\":\"android.process.acore\",\"pkg1\":\"com.android.providers.userdictionary\",\"pkg2\":\"com.android.providers.contacts\"},\"app71\":{\"imp\":100,\"imprc\":0,\"pcs\":\"com.sec.android.provider.logsprovider\",\"pkg1\":\"com.sec.android.provider.logsprovider\"},\"app72\":{\"imp\":200,\"impcom72\":\"ComponentInfo{org.simalliance.openmobileapi.service/org.simalliance.openmobileapi.service.SmartcardService}\",\"imprc\":2,\"pcs\":\"org.simalliance.openmobileapi.service:remote\",\"pkg1\":\"org.simalliance.openmobileapi.service\"},\"app73\":{\"imp\":100,\"imprc\":0,\"pcs\":\"com.android.nfc\",\"pkg1\":\"com.android.nfc\"},\"app74\":{\"imp\":100,\"imprc\":0,\"pcs\":\"com.android.phone\",\"pkg1\":\"com.android.providers.telephony\",\"pkg2\":\"com.android.stk\",\"pkg3\":\"com.sec.enterprise.mdm.services.simpin\",\"pkg4\":\"com.android.settings\",\"pkg5\":\"com.sec.android.app.bluetoothtest\",\"pkg6\":\"com.android.phone\",\"pkg7\":\"com.samsung.sec.android.application.csc\"},\"app75\":{\"imp\":100,\"imprc\":0,\"pcs\":\"com.android.systemui\",\"pkg1\":\"com.android.keyguard\",\"pkg2\":\"com.android.systemui\"},\"app76\":{\"imp\":100,\"imprc\":0,\"pcs\":\"system\",\"pkg1\":\"com.sec.android.providers.security\",\"pkg2\":\"android\",\"pkg3\":\"com.android.providers.settings\",\"pkg4\":\"com.dsi.ant.server\",\"pkg5\":\"com.qualcomm.location\"},\"app8\":{\"imp\":400,\"imprc\":0,\"pcs\":\"com.android.chrome\",\"pkg1\":\"com.android.chrome\"},\"app9\":{\"imp\":100,\"imprc\":0,\"pcs\":\"com.policydm\",\"pkg1\":\"com.policydm\"}},\"timestamp\":1446218918.383}");
        entry2.setUserID("She");

        entry3.setId("92eef5f1-4f08-4103-a641-e458a082f643");
        entry3.setTimestamp(1446218939l);
        entry3.setProbeType("org.fraunhofer.cese.funf_sensor.Probe.CallStateProbe");
        entry3.setSensorData("{\"mExtras\":{\"Initial AirplaneMode: \":false,\"Initial HeadsetState: \":false},\"mFlags\":0,\"mWindowMode\":0,\"timestamp\":1446218918.479}");
        entry3.setUserID("It");

        List<ProbeEntry> list = new ArrayList<ProbeEntry>();
        list.add(entry1);
        list.add(entry2);
        list.add(entry3);

        return list;
    }
}

