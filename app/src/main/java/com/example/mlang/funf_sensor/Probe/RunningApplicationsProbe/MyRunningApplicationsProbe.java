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
            int i = 1;
            for(ActivityManager.RunningAppProcessInfo info : runningAppProcessInfoList){
                intent.putExtra("pcs"+i, info.processName);
                intent.putExtra("pkgs"+i, info.pkgList);
                intent.putExtra("imp"+i, info.importance);
                intent.putExtra("imprc+i", info.importanceReasonCode);
                if(info.importanceReasonComponent != null){
                    intent.putExtra("impcom"+i, info.importanceReasonComponent);
                }
                i++;
            }
            sendData(gson.toJsonTree(intent).getAsJsonObject());
        }
        stop();
    }
}