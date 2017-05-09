package edu.umd.fcmd.sensorlisteners.listener.network;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;

import javax.inject.Inject;

import edu.umd.fcmd.sensorlisteners.listener.Listener;
import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.model.network.NetworkProbeFactory;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;
import timber.log.Timber;

/**
 * Created by MMueller on 12/22/2016.
 * <p>
 * Listening to changes in the telephone manager of any kind.
 */
public class TelephonyListener extends PhoneStateListener implements Listener {

    private boolean isRunning;

    private final Context context;
    private final TelephonyManager telephonyManager;
    private final ProbeManager<Probe> probeManager;
    private final NetworkProbeFactory factory;

    @Inject
    TelephonyListener(Context context, ProbeManager<Probe> probeManager, TelephonyManager telephonyManager, NetworkProbeFactory factory) {
        this.context = context;
        this.probeManager = probeManager;
        this.telephonyManager = telephonyManager;
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

            telephonyManager.listen(this,
                    PhoneStateListener.LISTEN_CALL_STATE
                            | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE // Listen for changes to the data connection state (cellular).
                            | PhoneStateListener.LISTEN_SERVICE_STATE // Listen for changes to the network service state (cellular).
            );


            // Send initial probes
            onUpdate(factory.createCellProbe(telephonyManager.getDataState()));
            onUpdate(factory.createCallStateProbe(telephonyManager.getCallState()));
            //TODO: For Android O, we can get the ServiceState directly on startup. Not available before that version.

            // Indicate that this listener is listening.
            isRunning = true;
        }

    }

    @Override
    public void stopListening() {
        if (isRunning) {
            Timber.d("stopListening");
            telephonyManager.listen(this, PhoneStateListener.LISTEN_NONE);
            isRunning = false;
        }
    }

    @Override
    public boolean isPermittedByUser() {
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        onUpdate(factory.createCallStateProbe(state));
    }

    @Override
    public void onDataConnectionStateChanged(int state, int networkType) {
        super.onDataConnectionStateChanged(state, networkType);
        onUpdate(factory.createCellProbe(state));
    }

    @Override
    public void onServiceStateChanged(ServiceState serviceState) {
        super.onServiceStateChanged(serviceState);
        onUpdate(factory.createServiceStateProbe(serviceState));
    }
}

