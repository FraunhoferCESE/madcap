package edu.umd.fcmd.sensorlisteners.listener.location;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by llayman on 4/20/2017.
 */

public class FusedLocationService extends IntentService {
    private String TAG = this.getClass().getSimpleName();

    public FusedLocationService() {
        super("FusedLocationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Location location = intent.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);
        if (location != null) {
            Log.i(TAG, "Fused Location received: " + location);
            EventBus.getDefault().post(location);
        }

    }
}
