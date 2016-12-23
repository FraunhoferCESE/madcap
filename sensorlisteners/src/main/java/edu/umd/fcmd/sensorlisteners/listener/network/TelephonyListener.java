package edu.umd.fcmd.sensorlisteners.listener.network;

import android.content.Context;
import android.telephony.CellInfo;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import java.util.List;

import edu.umd.fcmd.sensorlisteners.model.network.CallStateProbe;
import edu.umd.fcmd.sensorlisteners.model.network.CellLocationProbe;
import edu.umd.fcmd.sensorlisteners.model.network.CellProbe;
import edu.umd.fcmd.sensorlisteners.model.network.TelecomServiceProbe;

import static android.R.attr.type;
import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by MMueller on 12/22/2016.
 *
 * Listening to changes in the telephone manager of any kind.
 */
public class TelephonyListener extends PhoneStateListener {
    private final String TAG = getClass().getSimpleName();
    private final String LOG_TAG = TAG;

    private Context context;
    private NetworkListener networkListener;

    public TelephonyListener(Context context, NetworkListener networkListener) {
        this.context = context;
        this.networkListener = networkListener;
    }

    /**
     * same as above, but with the network type.  Both called.
     *
     * @param state
     * @param networkType
     */
    @Override
    public void onDataConnectionStateChanged(int state, int networkType) {
        super.onDataConnectionStateChanged(state, networkType);

        CellProbe cellProbe = createNewCellularProbe();
        networkListener.onUpdate(cellProbe);
    }

    @Override
    public void onServiceStateChanged(ServiceState serviceState) {
        super.onServiceStateChanged(serviceState);

        TelecomServiceProbe telecomServiceProbe = createNewServiceProbe(serviceState);

        networkListener.onUpdate(telecomServiceProbe);
    }

    /**
     * Callback invoked when device call state changes.
     * @param state the call state.
     * @param incomingNumber the incoming number if available.
     */
    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);

        CallStateProbe callStateProbe = createNewCallStateProbe(state, incomingNumber);

        networkListener.onUpdate(callStateProbe);
    }

    @Override
    public void onCellLocationChanged(CellLocation location) {
        super.onCellLocationChanged(location);

        CellLocationProbe cellLocationProbe = createCellLocationProbe(location);

        networkListener.onUpdate(cellLocationProbe);

    }

    CellLocationProbe createCellLocationProbe(CellLocation location){
        if (location instanceof GsmCellLocation) {
            GsmCellLocation gcLoc = (GsmCellLocation) location;
            CellLocationProbe cellLocationProbe = new CellLocationProbe();
            cellLocationProbe.setDate(System.currentTimeMillis());
            cellLocationProbe.setCellType("GSM");
            cellLocationProbe.setAreaCode(gcLoc.getLac()+" ");

            return cellLocationProbe;
        } else if (location instanceof CdmaCellLocation) {
            CdmaCellLocation ccLoc = (CdmaCellLocation) location;

            CellLocationProbe cellLocationProbe = new CellLocationProbe();
            cellLocationProbe.setDate(System.currentTimeMillis());
            cellLocationProbe.setCellType("CDMA");
            cellLocationProbe.setAreaCode(ccLoc.getBaseStationId()+" ");
            cellLocationProbe.setLat(ccLoc.getBaseStationLongitude());
            cellLocationProbe.setLat(ccLoc.getBaseStationLatitude());

            return cellLocationProbe;
        } else {
            Log.i(LOG_TAG, "onCellLocationChanged: " + location.toString());
            CellLocationProbe cellLocationProbe = new CellLocationProbe();
            cellLocationProbe.setDate(System.currentTimeMillis());
            cellLocationProbe.setCellType("UNKNOWN");

            return cellLocationProbe;
        }
    }


    /**
     * Method for creating a new TelecomServiceProbe.
     * @param serviceState the state.
     * @return a new probe.
     */
    TelecomServiceProbe createNewServiceProbe(ServiceState serviceState) {
        TelecomServiceProbe telecomServiceProbe = new TelecomServiceProbe();
        telecomServiceProbe.setDate(System.currentTimeMillis());

        if(serviceState.getRoaming()){
            telecomServiceProbe.setRoaming("ROAMING");
        }else{
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
        }

        return telecomServiceProbe;
    }

    CallStateProbe createNewCallStateProbe(int state, String incomingNumber) {
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
     * @return the created Probe.
     */
    CellProbe createNewCellularProbe(){
        CellProbe cellProbe = new CellProbe();
        cellProbe.setDate(System.currentTimeMillis());
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);

        switch (telephonyManager.getDataState()) {
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

