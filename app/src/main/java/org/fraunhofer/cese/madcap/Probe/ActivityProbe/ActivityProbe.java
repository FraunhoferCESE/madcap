package org.fraunhofer.cese.madcap.Probe.ActivityProbe;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.pathsense.android.sdk.location.PathsenseLocationProviderApi;

import org.fraunhofer.cese.madcap.MainActivity;

import edu.mit.media.funf.probe.Probe;

/**
 * Created by MMueller on 9/20/2016.
 */
public class ActivityProbe extends Probe.Base implements Probe.PassiveProbe {
    MainActivity mainActivity;
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
    private InternalHandler mHandler;

    public PathsenseLocationProviderApi getmApi() {
        return mApi;
    }

    public void setmApi(PathsenseLocationProviderApi mApi) {
        this.mApi = mApi;
    }

    public PathsenseActivityChangeBroadcastReceiver getpActivityChangeReceiver() {
        return pActivityChangeReceiver;
    }

    public void setpActivityChangeReceiver(PathsenseActivityChangeBroadcastReceiver pActivityChangeReceiver) {
        this.pActivityChangeReceiver = pActivityChangeReceiver;
    }

    public PathsenseActivityUpdateBroadcastReceiver getpActivityUpdateReceiver() {
        return pActivityUpdateReceiver;
    }

    public void setpActivityUpdateReceiver(PathsenseActivityUpdateBroadcastReceiver pActivityUpdateReceiver) {
        this.pActivityUpdateReceiver = pActivityUpdateReceiver;
    }

    public PathsenseDeviceHoldingBroadcastReceiver getpDeviceHoldingReceiver() {
        return pDeviceHoldingReceiver;
    }

    public void setpDeviceHoldingReceiver(PathsenseDeviceHoldingBroadcastReceiver pDeviceHoldingReceiver) {
        this.pDeviceHoldingReceiver = pDeviceHoldingReceiver;
    }

    public InternalHandler getmHandler() {
        return mHandler;
    }

    public void setmHandler(InternalHandler mHandler) {
        this.mHandler = mHandler;
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    PathsenseLocationProviderApi mApi;

    public ActivityProbe(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

    @Override
    public void onEnable(){
        super.onStart();
        //mHandler = new InternalHandler(this);

        // location api
        mApi = PathsenseLocationProviderApi.getInstance(mainActivity);
        localBroadcastManager = LocalBroadcastManager.getInstance(mainActivity);

        pActivityChangeReceiver = new PathsenseActivityChangeBroadcastReceiver(this, localBroadcastManager);
        localBroadcastManager.registerReceiver(pActivityChangeReceiver, new IntentFilter("activityChange"));

        pActivityUpdateReceiver = new PathsenseActivityUpdateBroadcastReceiver(this, localBroadcastManager);
        localBroadcastManager.registerReceiver(pActivityUpdateReceiver, new IntentFilter("activityUpdate"));

        pDeviceHoldingReceiver = new PathsenseDeviceHoldingBroadcastReceiver(this, localBroadcastManager);
        localBroadcastManager.registerReceiver(pDeviceHoldingReceiver, new IntentFilter("deviceHolding"));

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

    private void sendData(Intent intent) {
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
