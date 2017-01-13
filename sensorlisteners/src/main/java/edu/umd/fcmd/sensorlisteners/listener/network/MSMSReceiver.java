package edu.umd.fcmd.sensorlisteners.listener.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import edu.umd.fcmd.sensorlisteners.model.network.MSMSProbe;

import static android.os.Build.VERSION_CODES.M;

/**
 * Created by MMueller on 12/28/2016.
 *
 * Receiver for SMS and MMS events
 */
public class MSMSReceiver extends BroadcastReceiver{
    private final String TAG = getClass().getSimpleName();

    private final NetworkListener networkListener;

    public MSMSReceiver(NetworkListener networkListener){
        this.networkListener = networkListener;
    }


    /**
     * This method is called when the BroadcastReceiver is receiving an Intent
     * broadcast.  During this time you can use the other methods on
     * BroadcastReceiver to view/modify the current result values.  This method
     * is always called within the main thread of its process, unless you
     * explicitly asked for it to be scheduled on a different thread using
     * {@link Context registerReceiver(BroadcastReceiver,
     * IntentFilter, String, Handler)}. When it runs on the main
     * thread you should
     * never perform long-running operations in it (there is a timeout of
     * 10 seconds that the system allows before considering the receiver to
     * be blocked and a candidate to be killed). You cannot launch a popup dialog
     * in your implementation of onReceive().
     * <p>
     * <p><b>If this BroadcastReceiver was launched through a &lt;receiver&gt; tag,
     * then the object is no longer alive after returning from this
     * function.</b>  This means you should not perform any operations that
     * return a result to you asynchronously -- in particular, for interacting
     * with services, you should use
     * {@link Context#startService(Intent)} instead of
     * {@link Context bindService(Intent, ServiceConnection, int)}.  If you wish
     * to interact with a service that is already running, you can use
     * {@link #peekService}.
     * <p>
     * <p>The Intent filters used in {@link Context#registerReceiver}
     * and in application manifests are <em>not</em> guaranteed to be exclusive. They
     * are hints to the operating system about how to find suitable recipients. It is
     * possible for senders to force delivery to specific recipients, bypassing filter
     * resolution.  For this reason, {#onReceive(Context, Intent) onReceive()}
     * implementations should respond only to known actions, ignoring any unexpected
     * Intents that they may receive.
     *
     * @param context The Context in which the receiver is running.
     * @param intent  The Intent being received.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "RECEIVED");
//        SmsMessage[] messages = null;
//
//        if(intent != null){
//            messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
//        }
//
//        long timestamp = 0L;
//
//        if(messages != null){
//            for (SmsMessage message : messages) {
//                if (timestamp < message.getTimestampMillis()) {
//                    timestamp = message.getTimestampMillis();
//                }
//            }
//        }else{
//            timestamp = System.currentTimeMillis();
//        }


        MSMSProbe msmsProbe = new MSMSProbe();
        msmsProbe.setDate(System.currentTimeMillis());

        switch(intent.getAction()){
            case Telephony.Sms.Intents.SMS_RECEIVED_ACTION:
                // Text based sms received
                msmsProbe.setAction("SMS_RECEIVED_TEXT_BASED");
                break;
            case Telephony.Sms.Intents.DATA_SMS_RECEIVED_ACTION:
                // Data based sms received
                msmsProbe.setAction("SMS_RECEIVED_DATA_BASED");
                break;
            case Telephony.Sms.Intents.SMS_CB_RECEIVED_ACTION:
                // For public broadcasted messages through the cell network
                msmsProbe.setAction("CELL_BROADCAST_RECEIVED");
                break;
            case Telephony.Sms.Intents.SMS_REJECTED_ACTION:
                // When a sms has been rejected for any reason.
                msmsProbe.setAction("SMS_REJECTED");
                break;
            case Telephony.Sms.Intents.WAP_PUSH_RECEIVED_ACTION:
                // When a MMS is pushed to the phone
                msmsProbe.setAction("MMS_RECEIVED");
                break;
            case "android.provider.Telephony.SMS_SENT":
                // For outgoing sms in addition to the content observers.
                msmsProbe.setAction("SMS_SENT");
                break;
            case Telephony.Mms.Intents.CONTENT_CHANGED_ACTION:
                // When the content of a mms changes.
                msmsProbe.setAction("MMS_CONTENT_CHANGED");
                break;
            default:
                msmsProbe.setAction("UNKNOWN");
                break;
        }

        networkListener.onUpdate(msmsProbe);
    }
}
