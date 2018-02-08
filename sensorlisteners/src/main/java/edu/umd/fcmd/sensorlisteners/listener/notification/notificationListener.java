package edu.umd.fcmd.sensorlisteners.listener.notification;


import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.service.notification.NotificationListenerService;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import javax.inject.Inject;

import edu.umd.fcmd.sensorlisteners.issuehandling.PermissionsManager;
import edu.umd.fcmd.sensorlisteners.listener.Listener;
import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.model.notification.NotificationkProbeFactory;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;
import edu.umd.fcmd.sensorlisteners.model.notification.NotificationProbe;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;

/**
 * Created by MMueller on 12/2/2016.
 * <p>
 * Listener Class for everything network related like:
 * <p>
 * Internet, Calls, SMS
 */
public class notificationListener extends BroadcastReceiver implements Listener {
    private boolean runningStatus;
  //  private final NotificationListenerService notificationListenerService;
    private final Context mContext;
    private final ProbeManager<Probe> probeManager;
    private final PermissionsManager permissionsManager;
    private final NotificationkProbeFactory factory;
   // private final Bundle extras;
   // private final StatusBarNotification sbn;

    @Inject
    notificationListener(Context context, ProbeManager<Probe> probeManager, NotificationkProbeFactory factory, PermissionsManager permissionsManager) {

        mContext = context;
        this.probeManager = probeManager;
        this.factory = factory;
        this.permissionsManager = permissionsManager;
        //this.sbn = sbn;
      // this.extras = sbn.getNotification().extras;
       // this.extras = null;
      //  this.sbn = null;
    }
    @Override
    public void startListening() {
        if (!runningStatus) {
            //StatusBarNotification sbn = sbn.clone();
           //Bundle extras = sbn.getNotification().extras;
            IntentFilter intentFilter = new IntentFilter("Msg");
          //  intentFilter.addAction(NotificationManager.);

            mContext.registerReceiver(this, intentFilter);
          //  onUpdate(factory.createNotificationProbe(
         //           sbn.getPackageName(),
        //            sbn.getNotification().tickerText.toString(),
        //            sbn.getNotification().extras,
        //            extras.getString("android.title"),
       //             extras.getCharSequence("android.text").toString()));

            runningStatus = true;
        }
    }

    @Override
    public boolean isPermittedByUser() {
        return true;
    }

    @Override
    public void stopListening() {
        if (runningStatus) {
            mContext.unregisterReceiver(this);
            runningStatus = false;
        }
    }

    @Override
    public void onUpdate(Probe state) {
        probeManager.save(state);
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                System.out.println("incomingNumber : " + incomingNumber);
                Intent msgrcv = new Intent("Msg");
                msgrcv.putExtra("package", "");
                msgrcv.putExtra("ticker", incomingNumber);
                msgrcv.putExtra("title", incomingNumber);
                msgrcv.putExtra("text", "");
                LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);
    }
}

