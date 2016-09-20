package org.fraunhofer.cese.madcap.Probe;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import edu.mit.media.funf.probe.Probe;

public class SMSProbe extends Probe.Base implements Probe.PassiveProbe {

    private static final String TAG = "Fraunhofer." + SMSProbe.class.getSimpleName();

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public void onReceive(Context context, Intent intent) {

            SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
            long timestamp = 0;
            for (SmsMessage message : messages) {
                if (timestamp < message.getTimestampMillis()) {
                    timestamp = message.getTimestampMillis();
                }
            }

            Intent myIntent = new Intent();
            myIntent.putExtra("SMSProbe Timestamp: ", timestamp);

            if (Telephony.Sms.Intents.SMS_CB_RECEIVED_ACTION.equals(intent.getAction())) {
                for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    myIntent.putExtra("SMS Action", "SMS cell broadcast received");
                }

            } else if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
                for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    myIntent.putExtra("SMS Action", "SMS received");
                }
            } else if (Telephony.Mms.Intents.CONTENT_CHANGED_ACTION.equals(intent.getAction())) {
                myIntent.putExtra("MMS Action", "MMS content changed.");
            }

            sendData(myIntent);
        }
    };


    private ContentObserver smsOutgoingObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);

            Intent i = new Intent();
            i.putExtra("SMS Action", "SMS/MMS activity detected");
            sendData(i);
//
//            Cursor cur = getContext().getContentResolver().query(Telephony.Sms.CONTENT_URI, null, null, null, null, null);
//            if(cur == null)
//                return;
//
//
//            // If it's Type = 1 and ends in RAW, it's iether an SMS or MMS. We don't know
//            // If it's Type = 1 and does not end in /raw, it's an SMS
//            // If it's Type = 2 and ends in /raw, it's an MMS
//            // If it's Type = 2 and ends in 1234, it's an SMS
//
//            cur.moveToNext();
//            String messageType = uri.toString().endsWith("/raw") ? "MMS" : "SMS";
//            String sentOrReceived = cur.getString(cur.getColumnIndex("type"));
//
//            if (!Strings.isNullOrEmpty(sentOrReceived)) {
//                int type = Integer.parseInt(cur.getString(cur.getColumnIndex("type")));
//                cur.close();
//                String action = null;
//                switch (type) {
//                    case Telephony.Sms.MESSAGE_TYPE_SENT:
//                        action = " sent";
//                        break;
//                    case Telephony.Sms.MESSAGE_TYPE_DRAFT:
//                        action = " draft";
//                        break;
//                    case Telephony.Sms.MESSAGE_TYPE_FAILED:
//                        action = " send failed";
//                        break;
//                    case Telephony.Sms.MESSAGE_TYPE_OUTBOX:
//                        action = " in outbox";
//                        break;
//                    case Telephony.Sms.MESSAGE_TYPE_QUEUED:
//                        action = " queued to send";
//                        break;
//                    default:
//                        break;
//                }
//                if (action != null) {
//                    Intent i = new Intent();
//                    i.putExtra("SMS/MMS Action", messageType + action);
//                    sendData(i);
//                }
//            }
        }

        @SuppressWarnings("MethodReturnAlwaysConstant")
        @Override
        public boolean deliverSelfNotifications() {
            return false;
        }
    };

    private ContentObserver mmsOutgoingObserver = new ContentObserver(new Handler()) {

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            Intent i = new Intent();
            i.putExtra("SMS Action", "MMS activity detected");
            sendData(i);

//            Cursor cur = getContext().getContentResolver().query(Telephony.Mms.CONTENT_URI, null, null, null, null, null);
//            cur.moveToNext();
//
//
//            String typeString = cur.getString(cur.getColumnIndex("type"));
//            if (!Strings.isNullOrEmpty(typeString)) {
//                int type = Integer.parseInt(cur.getString(cur.getColumnIndex("type")));
//                String action = null;
//                switch (type) {
//                    case Telephony.Mms.:
//                        action = "SMS sent";
//                        break;
//                    case Telephony.Sms.MESSAGE_TYPE_DRAFT:
//                        action = "SMS draft";
//                        break;
//                    case Telephony.Sms.MESSAGE_TYPE_FAILED:
//                        action = "SMS send failed";
//                        break;
//                    case Telephony.Sms.MESSAGE_TYPE_OUTBOX:
//                        action = "SMS in outbox";
//                        break;
//                    case Telephony.Sms.MESSAGE_TYPE_QUEUED:
//                        action = "SMS queued to send";
//                        break;
//                    default:
//                        break;
//                }
//                if (action != null) {
//                    Intent i = new Intent();
//                    i.putExtra("SMS Action", "SMS sent");
//                    sendData(i);
//                }
//            }
        }

        @SuppressWarnings("MethodReturnAlwaysConstant")
        @Override
        public boolean deliverSelfNotifications() {
            return false;
        }
    };


    public void sendData(Intent intent) {
        sendData(getGson().toJsonTree(intent).getAsJsonObject());
    }

    @Override
    protected void onEnable() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.addAction("android.provider.Telephony.SMS_EMERGENCY_CB_RECEIVED");
        filter.addAction("android.provider.Telephony.WAP_PUSH_RECEIVED");
        getContext().registerReceiver(receiver, filter);

        getContext().getContentResolver().registerContentObserver(Telephony.Sms.CONTENT_URI, true, smsOutgoingObserver);
        getContext().getContentResolver().registerContentObserver(Telephony.Mms.CONTENT_URI, true, mmsOutgoingObserver);
    }

    @Override
    protected void onDisable() {
        getContext().unregisterReceiver(receiver);

        getContext().getContentResolver().unregisterContentObserver(smsOutgoingObserver);
        getContext().getContentResolver().unregisterContentObserver(mmsOutgoingObserver);
        super.onStop();
    }


}
