package edu.umd.fcmd.sensorlisteners.listener.location;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import timber.log.Timber;


/**
 * Created by MMueller on 11/15/2016.
 *
 * A issue manager for some callbacks from the GoogleApiClient
 */
public class LocationConnectionCallbacks implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    /**
     * No argument constructor used for dependency injection
     */
    @Inject
    public LocationConnectionCallbacks() { }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Timber.d("Connected");
        EventBus.getDefault().post(new FusedLocationConnectedEvent());
    }

    @Override
    public void onConnectionSuspended(int i) {
        Timber.w("Connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Timber.w("Connection failed");
    }

    public class FusedLocationConnectedEvent {
    }
}
