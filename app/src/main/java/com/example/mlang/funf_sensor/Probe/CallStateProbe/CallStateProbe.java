package com.example.mlang.funf_sensor.Probe.CallStateProbe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import edu.mit.media.funf.probe.Probe;

/**
 * Created by MLang on 09.02.2015.
 */
public class CallStateProbe extends Probe.Base implements Probe.PassiveProbe {

    private static final String TAG = "SMSProbe";
    private TelephonyManager telephony;
    private MyPhoneStateListener phoneListener;
    public static Boolean phoneRinging = false;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                    if (phoneListener == null) {
                        phoneListener = new MyPhoneStateListener();
                    }
                    telephony = (TelephonyManager) context
                            .getSystemService(Context.TELEPHONY_SERVICE);
                    telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
                    sendData(intent);
                }
        };

    @Override
    protected void onStart() {
        super.onStart();
        getContext().registerReceiver(receiver, new IntentFilter("android.intent.action.PHONE_STATE"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        getContext().unregisterReceiver(receiver);
    }

    public void sendData(Intent intent) {
        Log.i(TAG, "sendData() called. data = " + getGson().toJsonTree(intent).getAsJsonObject().toString());
        sendData(getGson().toJsonTree(intent).getAsJsonObject());
    }

    public class MyPhoneStateListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.d("PhoneStateListener", "Call state : IDLE");
                    phoneRinging = false;
                    Intent intent = new Intent();
                    intent.putExtra("CALL STATE", "IDLE");
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.d("PhoneStateListener", "Call state : OFFHOOK");
                    phoneRinging = false;
                    Intent intent2 = new Intent();
                    intent2.putExtra("CALL STATE", "OFFHOOK");
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.d("PhoneStateListener", "Call state : RINGING");
                    phoneRinging = true;
                    Intent intent3 = new Intent();
                    intent3.putExtra("CALL STATE", "RINGING");
                    break;
            }
        }
    }

}