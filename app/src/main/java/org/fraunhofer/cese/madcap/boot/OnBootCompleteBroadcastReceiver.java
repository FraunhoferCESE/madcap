package org.fraunhofer.cese.madcap.boot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.fraunhofer.cese.madcap.MyApplication;

import timber.log.Timber;

/**
 * Created by MMueller on 10/7/2016.
 * This Receiver listrens to the ON_BOOT_COMPLETE intent.
 */
public class OnBootCompleteBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "OnBootCompleteBroadcastReceiver";


    @Override
    public void onReceive(Context context, Intent intent){
        Timber.d("onReceive: Boot Completed message.");
        context.startService(new Intent(context, OnBootService.class));
    }
}