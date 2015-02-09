package com.example.mlang.funf_sensor.Probe.ForegroundProbe;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.gson.Gson;

import edu.mit.media.funf.probe.Probe;

/**
 * Created by MLang on 04.02.2015.
 */
public class ForegroundProbe extends Probe.Base {
    @Override
    protected void onStart() {
        super.onStart();
        Gson gson = getGson();
        ActivityManager am = (ActivityManager) getContext().getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
        String foregroundTaskPackageName = foregroundTaskInfo .topActivity.getPackageName();
        Log.i("ForegroundProbe.class","ForegroundProbe started.");
        Log.i("ForegroundProbe",foregroundTaskPackageName);
        //Log.wtf("ForegroundProbe","Package name" + foregroundTaskPackageName);
        sendData(gson.toJsonTree(foregroundTaskInfo).getAsJsonObject());
        stop();
    }
}
