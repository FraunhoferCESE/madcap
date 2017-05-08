package edu.umd.fcmd.sensorlisteners.listener.location;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;

import org.greenrobot.eventbus.EventBus;

import timber.log.Timber;

/**
 * Service class used to handle callbacks from the FusedLocationProviderAPI when a location changes.
 */
public class FusedLocationService extends IntentService {

    public FusedLocationService() {
        super("FusedLocationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Location location = intent.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);
        if (location != null) {
            Timber.d("Fused Location received: " + location);
            EventBus.getDefault().post(location);
        }
    }
}
