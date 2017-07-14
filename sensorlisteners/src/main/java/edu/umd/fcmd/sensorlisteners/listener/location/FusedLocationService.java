package edu.umd.fcmd.sensorlisteners.listener.location;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;

import com.google.android.gms.location.LocationResult;

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
        Timber.w("On Location Intent");
        //Location location = intent.getParcelableExtra(LocationManager.KEY_LOCATION_CHANGED);
        if (LocationResult.hasResult(intent)) {
//            Timber.d("Fused Location received: " + LocationResult.extractResult(intent));
            LocationResult locationResult = LocationResult.extractResult(intent);
            Location location = locationResult.getLastLocation();
//            Timber.i("Location: "+location);
            EventBus.getDefault().post(location);
        }
    }
}
