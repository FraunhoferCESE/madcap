package org.fraunhofer.cese.madcap.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.fraunhofer.cese.madcap.services.MadcapOnBootService;

/**
 * Created by MMueller on 10/7/2016.
 * This Receiver listrens to the ON_BOOT_COMPLETE intent.
 */
public class MadcaponBootCompleteBoardcastReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent arg1){
        Intent intent = new Intent(context, MadcapOnBootService.class);
        context.startService(intent);
        Log.i("Madcap", "BootCompletetBroadcastReceiver received on boot");
    }
}