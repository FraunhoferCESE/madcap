package org.fraunhofer.cese.madcap.util;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import org.fraunhofer.cese.madcap.cache.Cache;

import edu.umd.fcmd.sensorlisteners.model.Probe;

/**
 * Created by MMueller on 12/13/2016.
 */

public class ManualProbeUploader {

    private ManualProbeUploadTask manualProbeUploadTask;
    private Context context;

    public void uploadManual(Probe probe, Application application, Cache cache){
        manualProbeUploadTask = new ManualProbeUploadTask(application, cache);
        manualProbeUploadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, probe);
    }
}
