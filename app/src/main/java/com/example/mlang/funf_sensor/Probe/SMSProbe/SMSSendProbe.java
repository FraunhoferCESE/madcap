package com.example.mlang.funf_sensor.Probe.SMSProbe;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import edu.mit.media.funf.probe.Probe;

/**
 * Created by MLang on 10.02.2015.
 */
public class SMSSendProbe extends Probe.Base implements Probe.PassiveProbe{

    ContentResolver contentResolver;

    private static final String TAG = "SMSSendProbe";

    private ContentObserver smsOutgoingObserver = new ContentObserver(new Handler()) {

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onChange(boolean selfChange) {
            Intent i = new Intent();
            i.putExtra("Action", "SMS sent");
            Log.d(TAG, "SMS was sent with this device.");
            sendData(i);
        }

        @Override
        public boolean deliverSelfNotifications() {
            return false;
        }
    };

    private ContentObserver mmsOutgoingObserver = new ContentObserver(new Handler()) {

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onChange(boolean selfChange) {
            Intent i = new Intent();
            i.putExtra("Action", "MMS sent");
            Log.d(TAG, "MMS was sent with this device.");
            sendData(i);
        }

        @Override
        public boolean deliverSelfNotifications() {
            return false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        contentResolver = getContext().getContentResolver();
        contentResolver.registerContentObserver(Uri.parse("content://sms"), true, smsOutgoingObserver);
        contentResolver.registerContentObserver(Uri.parse("content://mms"), true, mmsOutgoingObserver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        contentResolver.unregisterContentObserver(smsOutgoingObserver);
        contentResolver.unregisterContentObserver(mmsOutgoingObserver);
    }

    public void sendData(Intent intent) {
        sendData(getGson().toJsonTree(intent).getAsJsonObject());
    }

}
