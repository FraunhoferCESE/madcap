package org.fraunhofer.cese.funf_sensor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Switch;


/**
 *
 */
public class AutoStart extends BroadcastReceiver{



    @Override
    public void onReceive(final Context context, final Intent intent) {

        Switch switchAutoStart = new MainActivity().getSwitchAutoStart();

        if(switchAutoStart.isChecked()) {
                Intent startIntent = new Intent(context, MainActivity.class);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startService(startIntent);
                Log.i("AutoStart: ", "launched!!");
        }
    }
}
