package com.example.mlang.funf_sensor.Probe.SMSProbe;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
import android.util.Log;

import edu.mit.media.funf.probe.Probe;

/**
 * Created by MLang on 09.02.2015.
 */
public class SMSProbe extends Probe.Base implements Probe.PassiveProbe {

    private static final String TAG = "SMSProbe";

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Telephony.Sms.Intents.SMS_DELIVER_ACTION.equals(intent.getAction())) {
                for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    String messageBody = smsMessage.getMessageBody();
                    Log.d(TAG, "SMS delivered.");
                    intent.putExtra("SMS Action", "SMS delivered");
                }
            }
            else if (Telephony.Sms.Intents.SMS_EMERGENCY_CB_RECEIVED_ACTION.equals(intent.getAction())) {
                for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    String messageBody = smsMessage.getMessageBody();
                    Log.d(TAG, "SMS emergency received.");
                    intent.putExtra("SMS Action", "SMS emergencey received");
                }
            }
            else if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
                for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    String messageBody = smsMessage.getMessageBody();
                    Log.d(TAG, "SMS received.");
                    intent.putExtra("SMS Action", "SMS received");
                }
            }
            else if(Telephony.Mms.Intents.CONTENT_CHANGED_ACTION.equals(intent.getAction())) {
                Log.d(TAG, "MMS content changed.");
                intent.putExtra("MMS Action", "MMS content changed.");
            }
            sendData(intent);
        }
    };

    public void sendData(Intent intent) {
        sendData(getGson().toJsonTree(intent).getAsJsonObject());
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.addAction("android.provider.Telephony.SMS_DELIVER");
        filter.addAction("android.provider.Telephony.SMS_EMERGENCY_CB_RECEIVED");
        filter.addAction("android.provider.Telephony.WAP_PUSH_RECEIVED");
        getContext().registerReceiver(receiver, filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getContext().unregisterReceiver(receiver);
    }

}
