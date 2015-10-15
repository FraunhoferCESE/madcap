package org.fraunhofer.cese.funf_sensor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Switch;


/**
 *
 */
public class AutoStart extends BroadcastReceiver {


    @Override
    public void onReceive(final Context context, final Intent intent) {

        Log.i("AutoStart: ", "started!!");
        Intent startIntent = new Intent(context, StartService.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(startIntent);
        Log.i("AutoStart: ", "finished!!");
        
    }
}
