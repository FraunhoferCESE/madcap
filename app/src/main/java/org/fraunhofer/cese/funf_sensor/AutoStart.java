package org.fraunhofer.cese.funf_sensor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 *
 */
public class AutoStart extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent startIntent = new Intent(context, MainActivity.class);
        context.startService(startIntent);
        Log.i("AutoStart: ", "launched!!");

    }
}
