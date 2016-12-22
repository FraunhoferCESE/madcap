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
import edu.umd.fcmd.sensorlisteners.model.network.CellProbe;
import edu.umd.fcmd.sensorlisteners.model.network.TelecomServiceProbe;

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
     * Callback invoked when a observed cell info has changed, or new cells have been added or removed.
     * @param cellInfo the new cell info list.
     */
    @Override
    public void onCellInfoChanged(List<CellInfo> cellInfo) {
        super.onCellInfoChanged(cellInfo);
        //Log.i(LOG_TAG, "onCellInfoChanged: " + cellInfo);
    }

    /**
     * Callback invoked when data activity state changes.
     * @param direction the direction
     */
    @Override
    public void onDataActivity(int direction) {
        //TODO: Do we want to capture this?
        super.onDataActivity(direction);
        switch (direction) {
            case TelephonyManager.DATA_ACTIVITY_NONE:
                Log.i(LOG_TAG, "onDataActivity: DATA_ACTIVITY_NONE");
                break;
            case TelephonyManager.DATA_ACTIVITY_IN:
                Log.i(LOG_TAG, "onDataActivity: DATA_ACTIVITY_IN");
                break;
            case TelephonyManager.DATA_ACTIVITY_OUT:
                Log.i(LOG_TAG, "onDataActivity: DATA_ACTIVITY_OUT");
                break;
            case TelephonyManager.DATA_ACTIVITY_INOUT:
                Log.i(LOG_TAG, "onDataActivity: DATA_ACTIVITY_INOUT");
                break;
            case TelephonyManager.DATA_ACTIVITY_DORMANT:
                Log.i(LOG_TAG, "onDataActivity: DATA_ACTIVITY_DORMANT");
                break;
            default:
                Log.w(LOG_TAG, "onDataActivity: UNKNOWN " + direction);
                break;
        }
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
        if (location instanceof GsmCellLocation) {
            GsmCellLocation gcLoc = (GsmCellLocation) location;
            Log.i(LOG_TAG,
                    "onCellLocationChanged: GsmCellLocation "
                            + gcLoc.toString());
            Log.i(LOG_TAG, "onCellLocationChanged: GsmCellLocation cell id "
                    + gcLoc.getCid());
            Log.i(LOG_TAG, "onCellLocationChanged: GsmCellLocation location area code "
                    + gcLoc.getLac());
            Log.i(LOG_TAG, "onCellLocationChanged: GsmCellLocation primary scrambling code"
                    + gcLoc.getPsc());
        } else if (location instanceof CdmaCellLocation) {
            CdmaCellLocation ccLoc = (CdmaCellLocation) location;
            Log.i(LOG_TAG,
                    "onCellLocationChanged: CdmaCellLocation "
                            + ccLoc.toString());
            Log.i(LOG_TAG,
                    "onCellLocationChanged: CdmaCellLocation getBaseStationId "
                            + ccLoc.getBaseStationId());
            Log.i(LOG_TAG,
                    "onCellLocationChanged: CdmaCellLocation getBaseStationLatitude "
                            + ccLoc.getBaseStationLatitude());
            Log.i(LOG_TAG,
                    "onCellLocationChanged: CdmaCellLocation getBaseStationLongitude"
                            + ccLoc.getBaseStationLongitude());
            Log.i(LOG_TAG,
                    "onCellLocationChanged: CdmaCellLocation getNetworkId "
                            + ccLoc.getNetworkId());
            Log.i(LOG_TAG,
                    "onCellLocationChanged: CdmaCellLocation getSystemId "
                            + ccLoc.getSystemId());
        } else {
            Log.i(LOG_TAG, "onCellLocationChanged: " + location.toString());
        }
    }

    @Override
    public void onCallForwardingIndicatorChanged(boolean cfi) {
        //TODO: do we want to capture this?

        super.onCallForwardingIndicatorChanged(cfi);
        //Log.i(LOG_TAG, "onCallForwardingIndicatorChanged: " + cfi);
    }

    @Override
    public void onMessageWaitingIndicatorChanged(boolean mwi) {
        //TODO: do we want to capture this?

        super.onMessageWaitingIndicatorChanged(mwi);
        //Log.i(LOG_TAG, "onMessageWaitingIndicatorChanged: " + mwi);
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

