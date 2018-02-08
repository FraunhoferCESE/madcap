package edu.umd.fcmd.sensorlisteners.model.notification;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.provider.Telephony;
import android.service.notification.StatusBarNotification;
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
import edu.umd.fcmd.sensorlisteners.model.network.CallStateProbe;
import edu.umd.fcmd.sensorlisteners.model.network.CellProbe;
import edu.umd.fcmd.sensorlisteners.model.network.MSMSProbe;
import edu.umd.fcmd.sensorlisteners.model.network.NFCProbe;
import edu.umd.fcmd.sensorlisteners.model.network.NetworkProbe;
import edu.umd.fcmd.sensorlisteners.model.network.TelecomServiceProbe;
import edu.umd.fcmd.sensorlisteners.model.network.WiFiProbe;
import timber.log.Timber;

/**
 * Factory class for creating networking-related probes
 */
@SuppressWarnings("MethodMayBeStatic")
public class NotificationkProbeFactory {

    /**
     * Default constructor is necessary for dependency injection with Dagger2
     */
    @SuppressWarnings("RedundantNoArgConstructor")
    @Inject
    public NotificationkProbeFactory() {
    }




    /**
     * Creates a wifi probe from a variety of wifi network state information
     *
     * @param ipAddress   an integer up address. See {@link WifiInfo#getIpAddress()}
     * @param currentSsid a string representing the current SSID. See {@link WifiInfo#getSSID()}
     * @param networkList a list of networks in range. Needed to get the security level. See {@link WifiManager#getScanResults()}
     * @param wifiState   the current wifi adapter state. See {@link WifiManager#getWifiState()}
     * @return a new wifi probe
     */
    @NonNull
    public NotificationProbe createNotificationProbe(String pack, String ticker, Bundle extras, String title, String text, StatusBarNotification sbn) {
        NotificationProbe notificationProbe = new NotificationProbe();
        notificationProbe.setPack(pack);
        notificationProbe.setTicker(ticker);
        notificationProbe.setExtras(extras);
        notificationProbe.setTitle(title);
        notificationProbe.setText(text);
        notificationProbe.setSBN(sbn);

        return notificationProbe;
    }
}