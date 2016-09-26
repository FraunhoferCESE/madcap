package org.fraunhofer.cese.madcap.Probe.ActivityProbe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.pathsense.android.sdk.location.PathsenseLocationProviderApi;

import org.fraunhofer.cese.madcap.MainActivity;

import edu.mit.media.funf.probe.Probe;

/**
 * Created by MMueller on 9/20/2016.
 */
public class ActivityProbe extends Probe.Base implements Probe.PassiveProbe {
    LocalBroadcastManager localBroadcastManager;

    static final String TAG = ActivityProbe.class.getName();
    // Messages
    static final int MESSAGE_ON_ACTIVITY_CHANGE = 1;
    static final int MESSAGE_ON_ACTIVITY_UPDATE = 2;

    // Registration of internal receivers

    PathsenseLocationProviderApi mApi;

    private class LocalBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            sendData(intent);
        }
    }

    @Override
    public void onEnable(){
        onStart();
        // location api
        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());

        localBroadcastManager.registerReceiver(new LocalBroadcastReceiver(), new IntentFilter("activityChange"));

        localBroadcastManager.registerReceiver(new LocalBroadcastReceiver(), new IntentFilter("activityUpdate"));

        mApi = PathsenseLocationProviderApi.getInstance(getContext());
        mApi.requestActivityChanges(PathsenseActivityChangeBroadcastReceiver.class);
        mApi.requestActivityUpdates(PathsenseActivityUpdateBroadcastReceiver.class);
        /*
        // receivers old
        localBroadcastManager = LocalBroadcastManager.getInstance(mainActivity);
        pActivityChangeReceiver = new InternalActivityChangeReceiver(this);
        localBroadcastManager.registerReceiver(pActivityChangeReceiver, new IntentFilter("activityChange"));
        pActivityUpdateReceiver = new InternalActivityUpdateReceiver(this);
        localBroadcastManager.registerReceiver(pActivityUpdateReceiver, new IntentFilter("activityUpdate"));
        pDeviceHoldingReceiver = new InternalDeviceHoldingReceiver(this);
        localBroadcastManager.registerReceiver(pDeviceHoldingReceiver, new IntentFilter("deviceHolding"));
        */
    }

    protected void sendData(Intent intent) {
        sendData(getGson().toJsonTree(intent).getAsJsonObject());
    }

    @Override
    protected void onDisable() {
        mApi.removeActivityChanges();
        mApi.removeActivityUpdates();


        onStop();
        //getContext().unregisterReceiver(receiver);
    }

    protected void sendInitialProbe(){

    }

}
