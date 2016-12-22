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
        Log.i(LOG_TAG, "onCellInfoChanged: " + cellInfo);
    }

    /**
     * Callback invoked when data activity state changes.
     * @param direction the direction
     */
    @Override
    public void onDataActivity(int direction) {
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
    }

    @Override
    public void onServiceStateChanged(ServiceState serviceState) {
        super.onServiceStateChanged(serviceState);
//        Log.d(LOG_TAG, "onServiceStateChanged: " + serviceState.toString());
//        Log.d(LOG_TAG, "onServiceStateChanged: getOperatorAlphaLong "
//                + serviceState.getOperatorAlphaLong());
//        Log.d(LOG_TAG, "onServiceStateChanged: getOperatorAlphaShort "
//                + serviceState.getOperatorAlphaShort());
//        Log.d(LOG_TAG, "onServiceStateChanged: getOperatorNumeric "
//                + serviceState.getOperatorNumeric());
//        Log.d(LOG_TAG, "onServiceStateChanged: getIsManualSelection "
//                + serviceState.getIsManualSelection());
//        Log.d(LOG_TAG,
//                "onServiceStateChanged: getRoaming "
//                        + serviceState.getRoaming());

        switch (serviceState.getState()) {
            case ServiceState.STATE_IN_SERVICE:
                Log.i(LOG_TAG, "onServiceStateChanged: STATE_IN_SERVICE");
                break;
            case ServiceState.STATE_OUT_OF_SERVICE:
                Log.i(LOG_TAG, "onServiceStateChanged: STATE_OUT_OF_SERVICE");
                break;
            case ServiceState.STATE_EMERGENCY_ONLY:
                Log.i(LOG_TAG, "onServiceStateChanged: STATE_EMERGENCY_ONLY");
                break;
            case ServiceState.STATE_POWER_OFF:
                Log.i(LOG_TAG, "onServiceStateChanged: STATE_POWER_OFF");
                break;
        }
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                Log.i(LOG_TAG, "onCallStateChanged: CALL_STATE_IDLE");
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                Log.i(LOG_TAG, "onCallStateChanged: CALL_STATE_RINGING");
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                Log.i(LOG_TAG, "onCallStateChanged: CALL_STATE_OFFHOOK");
                break;
            default:
                Log.i(LOG_TAG, "UNKNOWN_STATE: " + state);
                break;
        }
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
        super.onCallForwardingIndicatorChanged(cfi);
        Log.i(LOG_TAG, "onCallForwardingIndicatorChanged: " + cfi);
    }

    @Override
    public void onMessageWaitingIndicatorChanged(boolean mwi) {
        super.onMessageWaitingIndicatorChanged(mwi);
        Log.i(LOG_TAG, "onMessageWaitingIndicatorChanged: " + mwi);
    }
}

