package org.fraunhofer.cese.madcap.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.fraunhofer.cese.madcap.MyApplication;

import org.fraunhofer.cese.madcap.services.OnBootService;

/**
 * Created by MMueller on 10/7/2016.
 * This Receiver listrens to the ON_BOOT_COMPLETE intent.
 */
public class OnBootCompleteBoardcastReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent arg1){
        Intent intent = new Intent(context, OnBootService.class);
        context.startService(intent);
        MyApplication.madcapLogger.i("Madcap", "BootCompletetBroadcastReceiver received on boot");
    }
}