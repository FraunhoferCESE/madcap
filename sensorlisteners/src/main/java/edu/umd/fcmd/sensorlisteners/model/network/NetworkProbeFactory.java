package edu.umd.fcmd.sensorlisteners.model.network;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.inject.Inject;

import edu.umd.fcmd.sensorlisteners.listener.network.SMSListener;
import timber.log.Timber;

/**
 * Factory class for creating networking-related probes
 */
@SuppressWarnings("MethodMayBeStatic")
public class NetworkProbeFactory {

    /**
     * Default constructor is necessary for dependency injection with Dagger2
     */
    @SuppressWarnings("RedundantNoArgConstructor")
    @Inject
    public NetworkProbeFactory() {
    }

    /**
     * Creates a NetworkProbe that captures general network connection/disconnection activity.
     *
     * @param intent An Intent created from {@link ConnectivityManager#CONNECTIVITY_ACTION}
     * @return a new NetworkProbe
     */
    @NonNull
    public NetworkProbe createNetworkProbe(@NonNull Intent intent) {
        NetworkProbe networkProbe = new NetworkProbe();
        networkProbe.setDate(System.currentTimeMillis());
        networkProbe.setInfo(intent.getStringExtra(ConnectivityManager.EXTRA_EXTRA_INFO));
        if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
            networkProbe.setState(NetworkProbe.NOT_CONNECTED);
        } else {
            networkProbe.setState(NetworkProbe.CONNECTED);
        }
        return networkProbe;
    }

    /**
     * Creates a NetworkProbe that captures general network connection/disconnection activity based on the current network
     *
     * @param networkInfo from {@link ConnectivityManager#getActiveNetworkInfo()}
     * @return a new NetworkProbe
     */
    @NonNull
    public NetworkProbe createNetworkProbe(@Nullable NetworkInfo networkInfo) {
        NetworkProbe networkProbe = new NetworkProbe();
        networkProbe.setDate(System.currentTimeMillis());
        if (networkInfo == null) {
            networkProbe.setState(NetworkProbe.NOT_CONNECTED);
            networkProbe.setInfo(null);
        } else {
            networkProbe.setState(NetworkProbe.CONNECTED);
            networkProbe.setInfo(networkInfo.getExtraInfo());
        }
        return networkProbe;
    }

    /**
     * Creates an {@link NFCProbe} based on the current NFC adapter status.
     *
     * @param nfcAdapter From {@link NfcAdapter}
     * @return a new {@link NFCProbe}. {@link NFCProbe#getState()} will return {@link NFCProbe#ON} if the adapter exists and is turned on, and {@link NFCProbe#OFF} otherwise
     */
    @NonNull
    public NFCProbe createNfcProbe(@Nullable NfcAdapter nfcAdapter) {
        NFCProbe nfcProbe = new NFCProbe();
        nfcProbe.setDate(System.currentTimeMillis());
       // nfcProbe.setState(((nfcAdapter != null) && nfcAdapter.isEnabled()) ? NFCProbe.ON : NFCProbe.OFF);
        if ((nfcAdapter != null) && nfcAdapter.isEnabled()) {
            nfcProbe.setState(NFCProbe.ON);
        }
        else if(nfcAdapter == null){
            nfcProbe.setState(NFCProbe.UNAVAILABLE);
        }
        else {
            nfcProbe.setState(NFCProbe.OFF);
        }

        nfcProbe.setTagDiscoveryState(NFCProbe.UNKNOWN);
        nfcProbe.setTransactionConductedState(NFCProbe.UNKNOWN);
        return nfcProbe;
    }

    /**
     * Creates an {@link NFCProbe} based on the {@link NfcAdapter#ACTION_ADAPTER_STATE_CHANGED} broadcast
     * Possible NFC adapter statuses: Enabled, disabled, turning on, turning off, unknown.
     * @param intent From {@link NfcAdapter#ACTION_ADAPTER_STATE_CHANGED} broadcast
     * @return a new {@link NFCProbe}. {@link NFCProbe#getState()} will return {@link NFCProbe#ON} if the adapter exists and is turned on, and {@link NFCProbe#OFF} otherwise
     */
    @NonNull
    public NFCProbe createNfcProbe(@NonNull Intent intent) {
        NFCProbe nfcProbe = new NFCProbe();
        nfcProbe.setDate(System.currentTimeMillis());

        String intentAction = intent.getAction() != null ? intent.getAction() : "-";

        switch (intentAction) {
            case NfcAdapter.ACTION_ADAPTER_STATE_CHANGED:
                switch (intent.getIntExtra(NfcAdapter.EXTRA_ADAPTER_STATE, -1)) {
                    case NfcAdapter.STATE_OFF:
                        nfcProbe.setState(NFCProbe.OFF);
                        break;
                    case NfcAdapter.STATE_ON:
                        nfcProbe.setState(NFCProbe.ON);
                        break;
                    case NfcAdapter.STATE_TURNING_OFF:
                        nfcProbe.setState(NFCProbe.TURNING_OFF);
                        break;
                    case NfcAdapter.STATE_TURNING_ON:
                        nfcProbe.setState(NFCProbe.TURNING_ON);
                        break;
                    default:
                        nfcProbe.setState(NFCProbe.UNKNOWN);
                }
                nfcProbe.setTagDiscoveryState(NFCProbe.UNKNOWN);
                nfcProbe.setTransactionConductedState(NFCProbe.UNKNOWN);
                break;

            case NfcAdapter.ACTION_NDEF_DISCOVERED:
                nfcProbe.setState(NFCProbe.ON);
                nfcProbe.setTagDiscoveryState(NFCProbe.NDEF_DISCOVERY);
                break;

            case NfcAdapter.ACTION_TECH_DISCOVERED:
                nfcProbe.setState(NFCProbe.ON);
                nfcProbe.setTagDiscoveryState(NFCProbe.TECH_DISCOVERY);
                break;

            case NfcAdapter.ACTION_TAG_DISCOVERED:
                nfcProbe.setState(NFCProbe.ON);
                nfcProbe.setTagDiscoveryState(NFCProbe.TAG_DISCOVERY);
                break;

            case NfcAdapter.ACTION_TRANSACTION_DETECTED:
                nfcProbe.setState(NFCProbe.ON);
                nfcProbe.setTagDiscoveryState(NFCProbe.UNKNOWN);
                nfcProbe.setTransactionConductedState(NFCProbe.TRANSACTION_CONDUCTED);
                break;

            default:
                nfcProbe.setState(NFCProbe.UNKNOWN);
                nfcProbe.setTagDiscoveryState(NFCProbe.UNKNOWN);
                nfcProbe.setTransactionConductedState(NFCProbe.UNKNOWN);
                break;
        }

        return nfcProbe;
    }

    /**
     * Creates a new {@link MSMSProbe} in response to certain SMS events.
     *
     * @param intent an intent received in response to one of several SMS-related broadcasts. See {@link SMSListener#startListening()}
     * @return a new SMS Probe
     */
    @NonNull
    public MSMSProbe createSmsProbe(@NonNull Intent intent) {
        MSMSProbe msmsProbe = new MSMSProbe();
        msmsProbe.setDate(System.currentTimeMillis());

        switch (intent.getAction()) {
            case Telephony.Sms.Intents.SMS_RECEIVED_ACTION:
                msmsProbe.setAction("SMS_RECEIVED_TEXT_BASED");
                break;
            case Telephony.Sms.Intents.DATA_SMS_RECEIVED_ACTION:
                msmsProbe.setAction("SMS_RECEIVED_DATA_BASED");
                break;
            case Telephony.Sms.Intents.SMS_CB_RECEIVED_ACTION:
                msmsProbe.setAction("CELL_BROADCAST_RECEIVED");
                break;
            case Telephony.Sms.Intents.SMS_REJECTED_ACTION:
                msmsProbe.setAction("SMS_REJECTED");
                break;
            case Telephony.Sms.Intents.WAP_PUSH_RECEIVED_ACTION:
                msmsProbe.setAction("MMS_RECEIVED");
                break;
            case Telephony.Mms.Intents.CONTENT_CHANGED_ACTION:
                msmsProbe.setAction("MMS_CONTENT_CHANGED");
                break;
            default:
                msmsProbe.setAction("UNKNOWN");
                break;
        }
        return msmsProbe;
    }

    /**
     * Creates a probe that captures cellular data connection state.
     *
     * @param state One of {@link TelephonyManager#getDataState()}
     * @return a new cell probe
     */
    @NonNull
    public CellProbe createCellProbe(int state) {
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


    /**
     * Creates a new probe that captures telephone call-related state changes
     *
     * @param state see {@link TelephonyManager#getCallState()}
     * @return a new call state probe
     */
    @NonNull
    public CallStateProbe createCallStateProbe(int state) {
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
     * Creates a probe representing the telecom (voice/text) service state.
     *
     * @param serviceState see {@link ServiceState}
     * @return a new service state probe
     */
    @NonNull
    public TelecomServiceProbe createServiceStateProbe(@NonNull ServiceState serviceState) {
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


    /**
     * Creates a wifi probe from a variety of wifi network state information
     *
     * @param ipAddress   an integer up address. See {@link WifiInfo#getIpAddress()}
     * @param currentSsid a string representing the current SSID. See {@link WifiInfo#getSSID()}
     * @param networkList a list of networks in range. Needed to get the security level. See {@link WifiManager#getScanResults()}
     * @param intent      the intent which triggered WiFiProbe creation
     * @param wifiManager wifiManager instance used by the WiFiListener service
     * //@param wifiState   the current wifi adapter state. See {@link WifiManager#getWifiState()}
     * @return a new wifi probe
     */
    @NonNull
    public WiFiProbe createWiFiProbe(int ipAddress, String currentSsid, Iterable<ScanResult> networkList, Intent intent, WifiManager wifiManager) {
        WiFiProbe wiFiProbe = new WiFiProbe();
        wiFiProbe.setDate(System.currentTimeMillis());

        //sanitizing the String and only allowing numbers and letters
        String wifiSsid = currentSsid.replaceAll("[^a-zA-Z0-9]", "");

        if(wifiSsid == null || wifiSsid.isEmpty()){
            wifiSsid = "-";
        }
        wiFiProbe.setSsid(wifiSsid);

        // Convert IP address from int to string
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.order(ByteOrder.nativeOrder());
        byteBuffer.putInt(ipAddress);
        String ipAddressString;
        try {
            ipAddressString = InetAddress.getByAddress(byteBuffer.array()).getHostAddress();
        } catch (UnknownHostException ex) {
            Timber.d("Unable to convert host address", ex);
            //noinspection HardcodedFileSeparator
            ipAddressString = "N/A";
        }
        wiFiProbe.setIp(ipAddressString);

        //get current connected SSID for comparison to ScanResult
        String security = "-";

        for (ScanResult network : networkList) {

            //check if current connected SSID.
            if (currentSsid.equals('\"' + network.SSID + '\"')) {
                //get capabilities of current connection
                String capabilities = network.capabilities;

                //noinspection IfStatementWithTooManyBranches
                if (capabilities.toUpperCase().contains("WPA2")) {
                    security = "WPA2";
                } else if (capabilities.toUpperCase().contains("WPA")) {
                    security = "WPA";
                } else if (capabilities.toUpperCase().contains("WEP")) {
                    security = "WEP";
                } else if (capabilities.toUpperCase().contains("OPEN")) {
                    security = "OPEN";
                } else if (capabilities.toUpperCase().contains("WPS")){
                    security = "WPS";
                } else {
                    //sanitizing the String and only allowing numbers and letters
                    security = capabilities.replaceAll("[^a-zA-Z0-9]", "");
                }
            }
        }

        wiFiProbe.setNetworkSecurity(security);
        String networkState = intent != null ? intent.getAction() : "NA";
        // Trim the string to remove "android.net.wifi" and have only the actual event name
        networkState = networkState.substring(networkState.lastIndexOf('.')+1);
        // Sanitize the networkState string to include only letters and numbers
        networkState = networkState.replaceAll("[^_a-zA-Z0-9]", "");
        // set WiFi network state as the intent action which triggered the WiFi Probe creation
        // set network state as "-" if probe creation was triggered not by an intent but by serviceStart()
        wiFiProbe.setNetworkState(networkState);

        String state;
        // Get WiFi state from the intent and convert wifi state integer to string
        int finalWifiState = intent != null ? intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,  WifiManager.WIFI_STATE_UNKNOWN) : wifiManager.getWifiState();
        switch (finalWifiState) {
            case WifiManager.WIFI_STATE_ENABLED:
                state = "ENABLED";
                break;
            case WifiManager.WIFI_STATE_DISABLED:
                state = "DISABLED";
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                state = "ENABLING";
                break;
            case WifiManager.WIFI_STATE_DISABLING:
                state = "DISABLING";
                break;
            case WifiManager.WIFI_STATE_UNKNOWN:
                state = "UNKNOWN";
                break;
            default:
                state = "-";
                break;
        }
        wiFiProbe.setState(state);

        return wiFiProbe;
    }

}
