package edu.umd.fcmd.sensorlisteners.listener.network;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

import javax.inject.Inject;

import edu.umd.fcmd.sensorlisteners.listener.Listener;
import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.model.network.CallStateProbe;
import edu.umd.fcmd.sensorlisteners.model.network.CellLocationProbe;
import edu.umd.fcmd.sensorlisteners.model.network.CellProbe;
import edu.umd.fcmd.sensorlisteners.model.network.TelecomServiceProbe;
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

    private TelephonyListener telephonyListener;

    @Inject
    TelephonyListener(Context context, ProbeManager<Probe> probeManager, TelephonyManager telephonyManager) {
        this.context = context;
        this.probeManager = probeManager;
        this.telephonyManager = telephonyManager;
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
                            | PhoneStateListener.LISTEN_CELL_INFO
                            | PhoneStateListener.LISTEN_CELL_LOCATION
                            | PhoneStateListener.LISTEN_DATA_ACTIVITY
                            | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE
                            | PhoneStateListener.LISTEN_SERVICE_STATE
                            | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                            | PhoneStateListener.LISTEN_CALL_FORWARDING_INDICATOR
                            | PhoneStateListener.LISTEN_MESSAGE_WAITING_INDICATOR);

            // Send initial probes
            onUpdate(createNewCellularProbe());
            onUpdate(createNewCallStateProbe(telephonyManager.getCallState(), ""));
            onUpdate(createCellLocationProbe(telephonyManager.getCellLocation()));

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
        return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * same as above, but with the network type.  Both called.
     *
     * @param state       the new state.
     * @param networkType the network type.
     */
    @Override
    public void onDataConnectionStateChanged(int state, int networkType) {
        super.onDataConnectionStateChanged(state, networkType);
        onUpdate(createNewCellularProbe());
    }

    /**
     * Callback for changing of servic states
     *
     * @param serviceState in service, emergency only, off service...
     */
    @Override
    public void onServiceStateChanged(ServiceState serviceState) {
        super.onServiceStateChanged(serviceState);
        onUpdate(createNewServiceProbe(serviceState));
    }

    /**
     * Callback invoked when device call state changes.
     *
     * @param state          the call state.
     * @param incomingNumber the incoming number if available.
     */
    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        onUpdate(createNewCallStateProbe(state, incomingNumber));
    }

    @Override
    public void onCellLocationChanged(CellLocation location) {
        super.onCellLocationChanged(location);
        onUpdate(createCellLocationProbe(location));

    }

    private static CellLocationProbe createCellLocationProbe(CellLocation location) {
        CellLocationProbe cellLocationProbe = new CellLocationProbe();
        if (location instanceof GsmCellLocation) {
            GsmCellLocation gcLoc = (GsmCellLocation) location;
            cellLocationProbe.setDate(System.currentTimeMillis());
            cellLocationProbe.setCellType("GSM");
            cellLocationProbe.setAreaCode(gcLoc.getLac() + " ");
        } else if (location instanceof CdmaCellLocation) {
            CdmaCellLocation ccLoc = (CdmaCellLocation) location;
            cellLocationProbe.setDate(System.currentTimeMillis());
            cellLocationProbe.setCellType("CDMA");
            cellLocationProbe.setAreaCode(ccLoc.getBaseStationId() + " ");
            cellLocationProbe.setLat((double) ccLoc.getBaseStationLongitude());
            cellLocationProbe.setLat((double) ccLoc.getBaseStationLatitude());
        } else {
            cellLocationProbe.setDate(System.currentTimeMillis());
            cellLocationProbe.setCellType("UNKNOWN");
        }
        return cellLocationProbe;
    }


    /**
     * Method for creating a new TelecomServiceProbe.
     *
     * @param serviceState the state.
     * @return a new probe.
     */
    private static TelecomServiceProbe createNewServiceProbe(ServiceState serviceState) {
        TelecomServiceProbe telecomServiceProbe = new TelecomServiceProbe();
        telecomServiceProbe.setDate(System.currentTimeMillis());

        if (serviceState.getRoaming()) {
            telecomServiceProbe.setRoaming("ROAMING");
        } else {
            telecomServiceProbe.setRoaming("NO_ROAMING");
        }

        switch (serviceState.getState()) {
            case ServiceState.STATE_IN_SERVICE:
                telecomServiceProbe.setService("STATE_IN_SERVICE");
                break;
            case ServiceState.STATE_OUT_OF_SERVICE:
                telecomServiceProbe.setService("STATE_OUT_OF_SERVICE");
                break;
            case ServiceState.STATE_EMERGENCY_ONLY:
                telecomServiceProbe.setService("STATE_EMERGENCY_ONLY");
                break;
            case ServiceState.STATE_POWER_OFF:
                telecomServiceProbe.setService("STATE_POWER_OFF");
                break;
            default:
                break;
        }

        return telecomServiceProbe;
    }

    private static CallStateProbe createNewCallStateProbe(int state, String incomingNumber) {
        CallStateProbe callStateProbe = new CallStateProbe();
        callStateProbe.setDate(System.currentTimeMillis());

        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                callStateProbe.setState("IDLE");
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                callStateProbe.setState("RINGING");
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                callStateProbe.setState("OFFHOOK");
                break;
            default:
                callStateProbe.setState("UNKNOWN");
                break;
        }

        return callStateProbe;
    }

    /**
     * Creates a Cellular Probe.
     *
     * @return the created Probe.
     */
    private CellProbe createNewCellularProbe() {
        return createNewCellularProbe(telephonyManager.getDataState());
    }

    private static CellProbe createNewCellularProbe(int state) {
        CellProbe cellProbe = new CellProbe();
        cellProbe.setDate(System.currentTimeMillis());

        switch (state) {
            case TelephonyManager.DATA_CONNECTED:
                cellProbe.setState("CONNECTED");
                break;
            case TelephonyManager.DATA_DISCONNECTED:
                cellProbe.setState("DISCONNECTED");
                break;
            case TelephonyManager.DATA_CONNECTING:
                cellProbe.setState("CONNECTING");
                break;
            case TelephonyManager.DATA_SUSPENDED:
                cellProbe.setState("DISCONNECTING");
                break;
            default:
                cellProbe.setState("UNKNOWN");
                break;
        }
        return cellProbe;
    }
}

