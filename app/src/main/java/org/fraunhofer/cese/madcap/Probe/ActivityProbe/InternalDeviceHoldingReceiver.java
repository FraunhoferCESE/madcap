package org.fraunhofer.cese.madcap.Probe.ActivityProbe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Message;

import com.pathsense.android.sdk.location.PathsenseDeviceHolding;

import org.fraunhofer.cese.madcap.MainActivity;

/**
 * Created by MMueller on 9/20/2016.
 * @deprecated
 */
public class InternalDeviceHoldingReceiver extends BroadcastReceiver {
    ActivityProbe activityProbe;
    //
    InternalDeviceHoldingReceiver(ActivityProbe activity)
    {
        activityProbe = activity;
    }
    @Override
    public void onReceive(Context context, Intent intent)
    {
        final ActivityProbe activity = activityProbe;
        final InternalHandler handler = activity != null ? activity.getmHandler() : null;
        //
        if (activity != null && handler != null)
        {
            PathsenseDeviceHolding deviceHolding = (PathsenseDeviceHolding) intent.getSerializableExtra("deviceHolding");
            Message msg = Message.obtain();
            msg.what = ActivityProbe.MESSAGE_ON_DEVICE_HOLDING;
            msg.obj = deviceHolding;
            handler.sendMessage(msg);
        }
    }
}
