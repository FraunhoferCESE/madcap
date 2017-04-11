package org.fraunhofer.cese.madcap.issuehandling;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import javax.inject.Inject;


/**
 * Created by MMueller on 11/15/2016.
 *
 * A issue manager for some callbacks from the GoogleApiClient
 */
public class GoogleApiClientConnectionIssueManagerLocation implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private final String TAG = getClass().getSimpleName();

    /**
     * No argument constructor used for dependency injection
     */
    @Inject
    public GoogleApiClientConnectionIssueManagerLocation() { }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e(TAG, "Conntected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "Conntection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Conntection failed");
    }
}
