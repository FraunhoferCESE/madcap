package com.example.mlang.funf_sensor.Probe.SMSProbe;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;

import edu.mit.media.funf.probe.Probe;

/**
 * Created by MLang on 10.02.2015.
 */
public class SMSSendProbe extends Probe.Base implements Probe.PassiveProbe{

    private ContentObserver myObserver = new ContentObserver(new Handler()) {

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onChange(boolean selfChange) {
            this.onChange(selfChange, null);
            Intent i = new Intent();
            i.putExtra("Action", "SMS sent");
            sendData(i);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        ContentResolver contentResolver = getContext().getContentResolver();
        contentResolver.registerContentObserver(Uri.parse("content://sms"), true, myObserver);
    }

    @Override
    protected void onStop() {
        super.onStart();
        ContentResolver contentResolver = getContext().getContentResolver();
        contentResolver.unregisterContentObserver(myObserver);
    }

    public void sendData(Intent intent) {
        sendData(getGson().toJsonTree(intent).getAsJsonObject());
    }

}
