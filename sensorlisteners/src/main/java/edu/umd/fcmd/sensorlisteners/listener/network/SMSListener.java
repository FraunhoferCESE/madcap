package edu.umd.fcmd.sensorlisteners.listener.network;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.provider.Telephony;
import android.support.v4.content.ContextCompat;

import javax.inject.Inject;

import edu.umd.fcmd.sensorlisteners.listener.Listener;
import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.model.network.NetworkProbeFactory;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;
import timber.log.Timber;

/**
 * Listener for SMS and MMS receive events. It is not possible to listen for SMS/MMS send events unless the application is the default SMS provider.
 */
public class SMSListener extends BroadcastReceiver implements Listener {

    private boolean isRunning;

    private final Context mContext;
    private final ProbeManager<Probe> probeManager;
    private final NetworkProbeFactory factory;

    @Inject
    SMSListener(Context context, ProbeManager<Probe> probeManager, NetworkProbeFactory factory) {
        mContext = context;
        this.probeManager = probeManager;
        this.factory = factory;
    }

    @Override
    public void onUpdate(Probe state) {
        probeManager.save(state);
    }

    @Override
    public void startListening() {
        if (!isRunning && isPermittedByUser()) {
            Timber.d("startListening");
            IntentFilter filter = new IntentFilter();
            filter.addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
            filter.addAction(Telephony.Sms.Intents.DATA_SMS_RECEIVED_ACTION);
            filter.addAction(Telephony.Sms.Intents.SMS_CB_RECEIVED_ACTION);
            filter.addAction(Telephony.Sms.Intents.SMS_REJECTED_ACTION);
            filter.addAction(Telephony.Sms.Intents.WAP_PUSH_RECEIVED_ACTION);
            filter.addAction(Telephony.Mms.Intents.CONTENT_CHANGED_ACTION);
            mContext.registerReceiver(this, filter);

            isRunning = true;
        }
    }

    @Override
    public void stopListening() {
        if (isRunning) {
            mContext.unregisterReceiver(this);
            isRunning = false;
        }
    }

    @Override
    public boolean isPermittedByUser() {
        return ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        onUpdate(factory.createSmsProbe(intent));
    }
}
