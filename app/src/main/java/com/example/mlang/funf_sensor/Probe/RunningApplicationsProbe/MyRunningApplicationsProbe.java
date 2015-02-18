package com.example.mlang.funf_sensor.Probe.RunningApplicationsProbe;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;

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
            Intent intent = new Intent();
            for(ActivityManager.RunningAppProcessInfo info : runningAppProcessInfoList){
                intent.putExtra("pcs", info.processName);
                intent.putExtra("pkgs", info.pkgList);
                intent.putExtra("imp", info.importance);
                intent.putExtra("imprc", info.importanceReasonCode);
                if(info.importanceReasonComponent != null){
                    intent.putExtra("impcom", info.importanceReasonComponent);
                }
            }
            sendData(gson.toJsonTree(intent).getAsJsonObject());
        }
        stop();
    }
}