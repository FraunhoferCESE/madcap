package com.example.mlang.funf_sensor.Probe.RunningApplicationsProbe;

import android.app.ActivityManager;
import android.content.Context;
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
            List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfo = am.getRunningAppProcesses();
            Log.i("MyRunningApplicationsProbe.class", "MyRunningApplicationsProbe started.");
            sendData(gson.toJsonTree(runningAppProcessInfo).getAsJsonObject());
        }
        stop();
    }
}