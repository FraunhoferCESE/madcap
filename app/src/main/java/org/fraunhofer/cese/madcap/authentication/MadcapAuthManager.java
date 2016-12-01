package org.fraunhofer.cese.madcap.authentication;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.fraunhofer.cese.madcap.MyApplication;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Created by MMueller on 9/29/2016.
 */
@Singleton
public class MadcapAuthManager {

    private static final String TAG = "MADCAP Auth Manager";

    private final GoogleApiClient mGoogleApiClient;

    private volatile GoogleSignInAccount user;

    //private static GoogleSignInApi googleSignInApi = Auth.GoogleSignInApi;

    @Inject
    MadcapAuthManager(@Named("SigninApi") GoogleApiClient googleApiClient) {
        mGoogleApiClient = googleApiClient;
    }


    private synchronized void signin(@NonNull final LoginResultCallback callback) {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);

        if (opr.isDone()) {
            // In Case there is a result available intermediately. This should happen if they signed in before.
            GoogleSignInResult result = opr.get();
            MyApplication.madcapLogger.d(TAG, "Immediate result available: " + result);
            user = result.getSignInAccount();  // will be null if failed
            callback.onLoginResult(result);
        } else {
            MyApplication.madcapLogger.d(TAG, "silentLogin: Immediate results are not evailable.");
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult r) {
                    MyApplication.madcapLogger.d(TAG, "silentLogin: Received asynchronous login result. Code: " + r.getStatus().getStatusCode() + ", message: " + r.getStatus().getStatusMessage());
                    user = r.getSignInAccount();  // will be null if failed
                    callback.onLoginResult(r);
                }
            });
        }
    }

    /**
     * Performs a silent login with cached credentials
     */
    public void silentLogin(@NonNull Context context, @NonNull final LoginResultCallback callback) {
        MyApplication.madcapLogger.d(TAG, "silentLogin initiated");

        int connectionResult = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        if (connectionResult != ConnectionResult.SUCCESS) {
            callback.onServicesUnavailable(connectionResult);
            return;
        }

        if (mGoogleApiClient.isConnected()) {
            signin(callback);
        } else {
            mGoogleApiClient.registerConnectionFailedListener(callback);
            mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(@Nullable Bundle bundle) {
                    mGoogleApiClient.unregisterConnectionCallbacks(this);
                    callback.onConnected(bundle);
                    signin(callback);
                }

                @Override
                public void onConnectionSuspended(int i) {
                    MyApplication.madcapLogger.w(TAG, "onConnectionSuspended: Unexpected suspension of connection. Error code: " + i);
                }
            });
            mGoogleApiClient.connect();
        }
    }


    /**
     * Retrieves the currently logged in User Id.
     *
     * @return User ID.
     */
    public String getUserId() {
        String result = null;
        if (user != null) {
            result = user.getId();
        }

        return result;

    }

    /**
     * Makes the Signed in User accessable.
     *
     * @return the last users name.
     */
    @Nullable
    public String getLastSignedInUsersName() {
        String result = null;
        if (user != null) {
            result = user.getGivenName() + ' ' + user.getFamilyName();
        }
        return result;
    }

    public GoogleSignInAccount getUser() {
        return user;
    }

    public synchronized void setUser(GoogleSignInAccount user) {
        this.user = user;
    }

}
