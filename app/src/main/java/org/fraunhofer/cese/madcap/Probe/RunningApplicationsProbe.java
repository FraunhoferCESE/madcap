package org.fraunhofer.cese.madcap.Probe;

import android.app.ActivityManager;
import android.content.Context;
import org.fraunhofer.cese.madcap.MyApplication;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;

import edu.mit.media.funf.probe.Probe;

/**
 *
 */
public class RunningApplicationsProbe extends Probe.Base implements Probe.ContinuousProbe {

    private static final String TAG = "Fraunhofer." + RunningApplicationsProbe.class.getSimpleName();
    private static Thread runningApplicationsDeliverer;


    @Override
    protected void onEnable() {

        MyApplication.madcapLogger.d(TAG, "RunningApplicationsProbe starting");
        onStart();
        Gson gson = getGson();
        ActivityManager am = (ActivityManager) getContext().getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);

        runningApplicationsDeliverer = createRunningApplicationsDeliverer(gson, am);
        runningApplicationsDeliverer.start();
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        runningApplicationsDeliverer.interrupt();
    }

    private Thread createRunningApplicationsDeliverer(final Gson gson, final ActivityManager am) {

        runningApplicationsDeliverer = new Thread(new Runnable() {
            @Override
            public void run() {

                boolean isInterrupted = false;

                while (!isInterrupted && !runningApplicationsDeliverer.isInterrupted()) {
                    List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList = am.getRunningAppProcesses();
                    JsonObject allApps = new JsonObject();
                    int i = 1;
                    for (ActivityManager.RunningAppProcessInfo info : runningAppProcessInfoList) {
                        JsonObject currentApp = new JsonObject();
                        currentApp.addProperty("pcs", info.processName);
                        int k = 1;
                        for (String pkg : info.pkgList) {
                            currentApp.addProperty("pkg" + k++, pkg);
                        }
                        currentApp.addProperty("imp", info.importance);
                        currentApp.addProperty("imprc", info.importanceReasonCode);
                        if (info.importanceReasonComponent != null) {
                            currentApp.addProperty("impcom" + i, info.importanceReasonComponent.toString());
                        }
                        allApps.add("app" + i++, currentApp);
                    }
                    sendData(gson.toJsonTree(allApps).getAsJsonObject());

                    try {
                        Thread.sleep(15000l);
                    } catch (InterruptedException e) {
                        isInterrupted = true;
                    }
                }
            }
        });

        return runningApplicationsDeliverer;
    }
}

