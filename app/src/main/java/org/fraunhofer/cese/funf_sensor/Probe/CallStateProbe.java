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
    private BroadcastReceiver receiver;

    @Override
    protected void onEnable() {
        super.onStart();

        TelephonyManager telephony = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

        receiver = new CallStateReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        filter.addAction("android.intent.action.CALL");
        filter.addAction("android.intent.action.DIAL");
        filter.addAction("android.intent.action.ANSWER");

        getContext().registerReceiver(receiver, filter);

        Log.i("CallStateProbe: ", "CallStateProbe enabled");

    }

    @Override
    protected void onDisable() {
        TelephonyManager telephony = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(phoneListener, PhoneStateListener.LISTEN_NONE);

        super.onStop();
        getContext().unregisterReceiver(receiver);
    }

    public void sendData(Intent intent) {
        sendData(getGson().toJsonTree(intent).getAsJsonObject());
        Log.i("CallStateProbe: ", "CallStateProbe sent");
    }

    private class CallStateReceiver extends BroadcastReceiver {

        public CallStateProbe callback;

        public CallStateReceiver(CallStateProbe callback) {
            this.callback = callback;
        }


        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()) {
                case Intent.ACTION_CALL:
                    intent.putExtra("CallStateProbe: ", "called somebody");
                    break;
                case Intent.ACTION_ANSWER:
                    intent.putExtra("CallStateProbe: ", "incoming phonecall accepted");
                    break;
                case Intent.ACTION_DIAL:
                    intent.putExtra("CallStateProbe: ", "dialed a number");
                    break;
                case Intent.ACTION_NEW_OUTGOING_CALL:
                    intent.putExtra("CallStateProbe: ", "call about to be placed");
                    break;
                default:
                    intent.putExtra("CallStateProbe: ", "Something went wrong");
                    break;
            }

            callback.sendData(intent);
        }
    }

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
}