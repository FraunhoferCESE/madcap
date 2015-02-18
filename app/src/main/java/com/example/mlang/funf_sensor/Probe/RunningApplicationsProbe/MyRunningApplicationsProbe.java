package com.example.mlang.funf_sensor.Probe.RunningApplicationsProbe;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;

import edu.mit.media.funf.probe.Probe;

/**
 * Created by MLang on 18.02.2015.
 */
public class MyRunningApplicationsProbe extends Probe.Base {
    @Override
    protected void onStart() {
        super.onStart();
        Gson gson = getGson();
        ActivityManager am = (ActivityManager) getContext().getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        if(!am.isUserAMonkey()) {
            List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList = am.getRunningAppProcesses();
            Log.i("MyRunningApplicationsProbe.class", "MyRunningApplicationsProbe started.");
            JsonObject allApps = new JsonObject();
            int i = 1;
            for(ActivityManager.RunningAppProcessInfo info : runningAppProcessInfoList){
                JsonObject currentApp = new JsonObject();
                currentApp.addProperty("pcs", info.processName);
                int k = 1;
                for(String pkg : info.pkgList){
                    currentApp.addProperty("pkg" + k++, pkg);
                }
                currentApp.addProperty("imp", info.importance);
                currentApp.addProperty("imprc", info.importanceReasonCode);
                if(info.importanceReasonComponent != null){
                    currentApp.addProperty("impcom" + i, info.importanceReasonComponent.toString());
                }
                allApps.add("app"+ i++,currentApp);
            }
            sendData(gson.toJsonTree(allApps).getAsJsonObject());
        }
        stop();
    }
}