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
 *
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

        sendInitialState();
    }

    @Override
    protected void onDisable() {

        super.onStop();
        getContext().unregisterReceiver(receiver);
    }

    public void sendData(Intent intent) {
        sendData(getGson().toJsonTree(intent).getAsJsonObject());
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

    private void sendInitialState(){

        Intent intent = new Intent();

        TelephonyManager telephonyManager = (TelephonyManager)getContext().getSystemService(Context.TELEPHONY_SERVICE);

        intent.putExtra(TAG, "Initial Call State!");
        intent.putExtra("Call State: ", getCallState(telephonyManager));
        intent.putExtra("Data State: ", getDataState(telephonyManager));

        sendData(intent);

        Log.i(TAG, "initial Probe sent");
    }

    private String getDataState(TelephonyManager telephonyManager){
        String result;
        switch (telephonyManager.getDataState()){
            case TelephonyManager.DATA_CONNECTING:
                result = "connecting.";
                break;
            case TelephonyManager.DATA_SUSPENDED:
                result = "suspended.";
                break;
            case TelephonyManager.DATA_DISCONNECTED:
                result = "disconnected.";
                break;
            case TelephonyManager.DATA_CONNECTED:
                result = "connected.";
                break;
            default:
                result= "Something went wrong.";
                break;
        }
        return result;
    }

    private String getCallState(TelephonyManager telephonyManager){
        String result;
        switch(telephonyManager.getCallState()) {
            case TelephonyManager.CALL_STATE_RINGING:
                result = "ringing";
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                result = "idle";
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                result = "offhook";
                break;
            default:
                result = "Something went wrong.";
                break;
        }
        return result;
    }
}