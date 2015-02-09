package com.example.mlang.funf_sensor.Probe.CallStateProbe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * Created by MLang on 09.02.2015.
 */
/*public class MyPhoneStateReceiver extends BroadcastReceiver {
    TelephonyManager telephony;
    private MyPhoneStateListener phoneListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (phoneListener == null) {
            phoneListener = new MyPhoneStateListener();
        }
        telephony = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
    }
}
*/