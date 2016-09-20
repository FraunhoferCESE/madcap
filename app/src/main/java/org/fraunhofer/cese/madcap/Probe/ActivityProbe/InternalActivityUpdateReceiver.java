package org.fraunhofer.cese.madcap.Probe.ActivityProbe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Message;

import com.pathsense.android.sdk.location.PathsenseDetectedActivities;

import org.fraunhofer.cese.madcap.MainActivity;

/**
 * Created by MMueller on 9/20/2016.
 * @deprecated
 */
public class InternalActivityUpdateReceiver extends BroadcastReceiver {
    ActivityProbe activityProbe;
    //
    InternalActivityUpdateReceiver(ActivityProbe activityProbe)
    {
        this.activityProbe = activityProbe;
    }
    @Override
    public void onReceive(Context context, Intent intent)
    {
        final ActivityProbe activity = activityProbe;
        final InternalHandler handler = activity != null ? activity.getmHandler() : null;
        //
        if (activity != null && handler != null)
        {
            PathsenseDetectedActivities detectedActivities = (PathsenseDetectedActivities) intent.getSerializableExtra("detectedActivities");
            Message msg = Message.obtain();
            msg.what = ActivityProbe.MESSAGE_ON_ACTIVITY_UPDATE;
            msg.obj = detectedActivities;
            handler.sendMessage(msg);
        }
    }
}
