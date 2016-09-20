package org.fraunhofer.cese.madcap.Probe.ActivityProbe;

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
    static final int MESSAGE_ON_DEVICE_HOLDING = 3;

    // Registration of internal receivers
    private PathsenseActivityChangeBroadcastReceiver pActivityChangeReceiver;
    private PathsenseActivityUpdateBroadcastReceiver pActivityUpdateReceiver;
    private PathsenseDeviceHoldingBroadcastReceiver pDeviceHoldingReceiver;

    PathsenseLocationProviderApi mApi;

    @Override
    public void onEnable(){
        super.onStart();
        Log.d("ActivityPRobe", "onEnable()");
        // location api
        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());

        pActivityChangeReceiver = new PathsenseActivityChangeBroadcastReceiver(this);
        localBroadcastManager.registerReceiver(pActivityChangeReceiver, new IntentFilter("activityChange"));

        pActivityUpdateReceiver = new PathsenseActivityUpdateBroadcastReceiver(this);
        localBroadcastManager.registerReceiver(pActivityUpdateReceiver, new IntentFilter("activityUpdate"));

        pDeviceHoldingReceiver = new PathsenseDeviceHoldingBroadcastReceiver(this);
        localBroadcastManager.registerReceiver(pDeviceHoldingReceiver, new IntentFilter("deviceHolding"));

        mApi = PathsenseLocationProviderApi.getInstance(getContext());
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
        Log.d("ActivityProbe", "sendData()");
        sendData(getGson().toJsonTree(intent).getAsJsonObject());
    }

    @Override
    protected void onDisable() {
        super.onStop();
        //getContext().unregisterReceiver(receiver);
    }

    protected void sendInitialProbe(){

    }

}
