/*
 * Copyright (c) 2015 PathSense, Inc.
 */

package org.fraunhofer.cese.madcap.Probe.ActivityProbe;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import org.fraunhofer.cese.madcap.MyApplication;

import com.pathsense.android.sdk.location.PathsenseActivityRecognitionReceiver;
import com.pathsense.android.sdk.location.PathsenseDetectedActivities;

public class PathsenseActivityChangeBroadcastReceiver extends PathsenseActivityRecognitionReceiver {

    private ActivityProbe callback;

    static final String TAG = PathsenseActivityChangeBroadcastReceiver.class.getName();

    @Override
    protected void onDetectedActivities(Context context, PathsenseDetectedActivities detectedActivities) {
        // broadcast detected activities
        Intent detectedActivitiesIntent = new Intent("activityChange");
        detectedActivitiesIntent.putExtra("detectedActivities", detectedActivities);
        LocalBroadcastManager.getInstance(context).sendBroadcast(detectedActivitiesIntent);
    }
}
