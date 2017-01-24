package edu.umd.fcmd.sensorlisteners.listener.audio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.util.Log;

import edu.umd.fcmd.sensorlisteners.model.audio.HeadphoneProbe;
import edu.umd.fcmd.sensorlisteners.model.audio.RingerProbe;

/**
 * Created by MMueller on 1/24/2017.
 *
 * Handling incoming audio related intents.
 */
public class AudioReceiver extends BroadcastReceiver {
    private final String TAG = getClass().getSimpleName();

    private final AudioListener audioListener;

    public AudioReceiver(AudioListener audioListener){
        this.audioListener = audioListener;
    }


    /**
     * This method is called when the BroadcastReceiver is receiving an Intent
     * broadcast.  During this time you can use the other methods on
     * BroadcastReceiver to view/modify the current result values.  This method
     * is always called within the main thread of its process, unless you
     * explicitly asked for it to be scheduled on a different thread using
     * {@link Context #registerReceiver(BroadcastReceiver,
     * IntentFilter, String, Handler)}. When it runs on the main
     * thread you should
     * never perform long-running operations in it (there is a timeout of
     * 10 seconds that the system allows before considering the receiver to
     * be blocked and a candidate to be killed). You cannot launch a popup dialog
     * in your implementation of onReceive().
     * <p>
     * <p><b>If this BroadcastReceiver was launched through a &lt;receiver&gt; tag,
     * then the object is no longer alive after returning from this
     * function.</b>  This means you should not perform any operations that
     * return a result to you asynchronously -- in particular, for interacting
     * with services, you should use
     * {@link Context#startService(Intent)} instead of
     * {@link Context #bindService(Intent, ServiceConnection, int)}.  If you wish
     * to interact with a service that is already running, you can use
     * {@link #peekService}.
     * <p>
     * <p>The Intent filters used in {@link Context#registerReceiver}
     * and in application manifests are <em>not</em> guaranteed to be exclusive. They
     * are hints to the operating system about how to find suitable recipients. It is
     * possible for senders to force delivery to specific recipients, bypassing filter
     * resolution.  For this reason, {@link #onReceive(Context, Intent) onReceive()}
     * implementations should respond only to known actions, ignoring any unexpected
     * Intents that they may receive.
     *
     * @param context The Context in which the receiver is running.
     * @param intent  The Intent being received.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case AudioManager.ACTION_HEADSET_PLUG:
                HeadphoneProbe headphoneProbe = new HeadphoneProbe();
                headphoneProbe.setDate(System.currentTimeMillis());
                headphoneProbe.setPlugState(audioListener.getCurrentHeadphonePlugState());
                audioListener.onUpdate(headphoneProbe);
                break;
            case AudioManager.RINGER_MODE_CHANGED_ACTION:
                RingerProbe ringerProbe = new RingerProbe();
                ringerProbe.setDate(System.currentTimeMillis());
                ringerProbe.setMode(audioListener.getCurrentRingerMode());
                audioListener.onUpdate(ringerProbe);
                break;
            case "android.media.VOLUME_CHANGED_ACTION":
                audioListener.evaluateVolumeChanges();
            default:
                Log.e(TAG, "Received undefined");
                break;
        }
    }
}
