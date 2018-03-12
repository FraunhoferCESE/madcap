package org.fraunhofer.cese.madcap.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;

import javax.inject.Inject;

/**
 * Created by Thorfinnur on 3/8/2018.
 * <p>
 * Service Class for the wifi connection and is only used to check if the phone is
 * connected to an open wifi. This class and the WifiServiceListener class are used to trigger
 * the open wifi notification in the NotificationService class.
 * <p>
 *
 */
@SuppressWarnings("MethodMayBeStatic")
public class WifiService extends Service {

    Context context;
    String OldSSID;

    @SuppressWarnings("RedundantNoArgConstructor")
    @Inject
    public WifiService(Context context) {
        OldSSID = "-";
        this.context = context;
    }
    public class LocalBinder extends Binder {
        WifiService getService() {
            return WifiService.this;
        }
    }

    @Override
    public void onCreate() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return START_REDELIVER_INTENT;
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    private final IBinder mBinder = new LocalBinder();
    /*
        This function checks if the phone is connected to an open wifi and if it is then
        it sends a notification to the user
         * @param currentSsid a string representing the current SSID. See {@link WifiInfo#getSSID()}
         * @param networkList a list of networks in range. Needed to get the security level. See {@link WifiManager#getScanResults()}
     */
    @NonNull
    public void createWiFiProbe(String currentSsid, Iterable<ScanResult> networkList) {

        String security = "-";
        String SSID = "-";
        for (ScanResult network : networkList) {

            //check if current connected SSID.
            if (currentSsid.equals('\"' + network.SSID + '\"')) {
                //get capabilities of current connection
                String capabilities = network.capabilities;

                if (capabilities.toUpperCase().contains("WPA2")) {
                    security = "WPA2";
                    SSID = network.SSID;
                }
                else if (capabilities.toUpperCase().contains("OPEN")) {
                    security = "OPEN";
                    SSID = network.SSID;
                }
                else{
                    security = capabilities;
                    SSID = network.SSID;
                }
            }
        }
        if (!(OldSSID.equals(SSID))) {

            if (security == "WPA2") {

                context.startService(new Intent(context, NotificationPostingService.class));
            }
        }
        OldSSID = SSID;
    }
}


