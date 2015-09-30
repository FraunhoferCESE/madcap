package org.fraunhofer.cese.funf_sensor.Probe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import edu.mit.media.funf.probe.Probe;

/**
 * Created by MLang on 09.02.2015.
 */
public class CallStateProbe extends Probe.Base implements Probe.PassiveProbe {

    private static final String TAG = "CallStateProbe";

    PhoneStateListener phoneListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            Intent intent = new Intent();
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    intent.putExtra("CALL STATE", "IDLE");
                    sendData(intent);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    intent.putExtra("CALL STATE", "OFFHOOK");
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    intent.putExtra("CALL STATE", "RINGING");
                    break;
            }
            sendData(intent);
        }
    };

    @Override
    protected void onEnable() {
        super.onStart();

        TelephonyManager telephony = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    protected void onStop() {
        TelephonyManager telephony = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(phoneListener, PhoneStateListener.LISTEN_NONE);

        super.onStop();
    }

    public void sendData(Intent intent) {
        sendData(getGson().toJsonTree(intent).getAsJsonObject());
    }
}