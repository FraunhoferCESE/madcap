package com.example.mlang.funf_sensor.Probe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * Created by MLang on 09.02.2015.
 */
public class MyPhoneStateReceiver extends BroadcastReceiver {
    TelephonyManager telephony;
    MyPhoneStateListener phoneListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        phoneListener = new MyPhoneStateListener();
        telephony = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    public void onDestroy() {
        telephony.listen(phoneListener, PhoneStateListener.LISTEN_NONE);
    }
}
