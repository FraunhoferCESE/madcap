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
public class InternalActivityChangeReceiver extends BroadcastReceiver {

    ActivityProbe activityProbe;
    //
    InternalActivityChangeReceiver(ActivityProbe activityProbe)
    {
        this.activityProbe = activityProbe;
    }
    @Override
    public void onReceive(Context context, Intent intent)
    {
        final ActivityProbe activityProbe = this.activityProbe;
        final InternalHandler handler = activityProbe != null ? activityProbe.getmHandler() : null;
        //
        if (activityProbe != null && handler != null)
        {
            PathsenseDetectedActivities detectedActivities = (PathsenseDetectedActivities) intent.getSerializableExtra("detectedActivities");
            Message msg = Message.obtain();
            msg.what = ActivityProbe.MESSAGE_ON_ACTIVITY_CHANGE;
            msg.obj = detectedActivities;
            handler.sendMessage(msg);
        }
    }
}
