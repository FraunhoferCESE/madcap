package org.fraunhofer.cese.madcap.Probe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.provider.Settings;
import android.util.Log;

import edu.mit.media.funf.probe.Probe;

/**
 *
 */
public class StateProbe extends Probe.Base implements Probe.PassiveProbe {


    private static final String TAG = "StateProbe";
    private BroadcastReceiver receiver;

    /**
     * Sets up the Intentfilter for the StateProbe and sends an initial StateProbe
     */
    @Override
    protected void onEnable() {

        super.onStart();

        receiver = new StateInformationReceiver(this);
        IntentFilter filter = new IntentFilter();

        filter.addAction("android.intent.action.AIRPLANE_MODE");
        filter.addAction("android.intent.action.BOOT_COMPLETED");
        filter.addAction("android.intent.action.ACTION_SHUTDOWN");
        filter.addAction("android.intent.action.USER_PRESENT");
        filter.addAction("android.intent.action.DREAMING_STARTED");
        filter.addAction("android.intent.action.DREAMING_STOPPED");
        filter.addAction("android.intent.action.HEADSET_PLUG");


        getContext().registerReceiver(receiver, filter);
        Log.i("StateProbe", "StateProbe enabled");

        sendInitialProbe();
        Log.i("StateProbe: ", "Initial StateProbe sent.");
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        getContext().unregisterReceiver(receiver);
    }


    public void sendData(Intent intent) {
        sendData(getGson().toJsonTree(intent).getAsJsonObject());
    }

    /**
     * Receiver-Class for general phone state events
     */
    public class StateInformationReceiver extends BroadcastReceiver {
        public StateProbe callback;

        public StateInformationReceiver(StateProbe callback) {
            this.callback = callback;
        }

        /**
         * Adds information to the intent according to the triggering event and sends the intent afterwards
         *
         * @param context
         * @param intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()) {
                case Intent.ACTION_AIRPLANE_MODE_CHANGED:
                    intent.putExtra("StateProbe: ", "AirplaneMode changed");
                    break;
                case Intent.ACTION_BOOT_COMPLETED:
                    intent.putExtra("StateProbe: ", "boot completed");
                    break;
                case Intent.ACTION_DREAMING_STARTED:
                    intent.putExtra("StateProbe: ", "Dreaming started");
                    break;
                case Intent.ACTION_DREAMING_STOPPED:
                    intent.putExtra("StateProbe: ", "Dreaming stopped");
                    break;
                case Intent.ACTION_HEADSET_PLUG:
                    intent.putExtra("StateProbe: ", "headset plugged");
                    break;
                case Intent.ACTION_SHUTDOWN:
                    intent.putExtra("StateProbe: ", "Device is shutting down");
                    break;
                case Intent.ACTION_USER_PRESENT:
                    intent.putExtra("StateProbe: ", "user is now present");
                    break;
                default:
                    intent.putExtra("StateProbe: ", "Something went wrong");
                    break;
            }
            callback.sendData(intent);
        }
    }


    /**
     * Sends the initial State when a StateProbe object is set up.
     * Should only be called from within the onEnable().
     */
    private void sendInitialProbe() {
        Intent intent = new Intent();
        boolean airplaneMode = isAirplaneModeOn(getContext());
        intent.putExtra("Initial AirplaneMode: ", airplaneMode);

        AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        boolean headsetPlugged = audioManager.isWiredHeadsetOn();
        intent.putExtra("Initial HeadsetState: ", headsetPlugged);

        sendData(intent);
    }

    private static boolean isAirplaneModeOn(Context context) {

        return Settings.Global.getInt(context.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }
}
