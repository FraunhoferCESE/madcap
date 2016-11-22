package org.fraunhofer.cese.madcap.authentication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

import org.fraunhofer.cese.madcap.MyApplication;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by MMueller on 9/29/2016.
 */
@Singleton
public class MadcapAuthManager implements OnConnectionFailedListener, Serializable {

    private static final String TAG = "MADCAP Auth Manager";
    public static final int RC_SIGN_IN = 9001;

    private final GoogleApiClient mGoogleApiClient;

    private volatile GoogleSignInAccount user;

    //private static GoogleSignInApi googleSignInApi = Auth.GoogleSignInApi;

    @Inject
    MadcapAuthManager(GoogleApiClient googleApiClient) {
        mGoogleApiClient = googleApiClient;
    }

    public interface LoginResultCallback extends GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
        void onServicesUnavailable(int connectionResult);

        void onLoginResult(GoogleSignInResult signInResult);
    }


    private synchronized void signin(final LoginResultCallback callback) {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);

        if (opr.isDone()) {
            // In Case there is a result available intermediately. This should happen if they signed in before.
            MyApplication.madcapLogger.d(TAG, "Immediate result available: " + opr.get());
            user = opr.get().getSignInAccount();  // will be null if failed
            callback.onLoginResult(opr.get());
        } else {
            MyApplication.madcapLogger.d(TAG, "silentLogin: Immediate results are not evailable.");
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    MyApplication.madcapLogger.d(TAG, "silentLogin: Received asynchronous login result: " + googleSignInResult);
                    user = googleSignInResult.getSignInAccount();  // will be null if failed
                    callback.onLoginResult(googleSignInResult);
                }
            });
        }
    }

    /**
     * Performs a silent login with cached credentials
     */
    public void silentLogin(final Context context, final LoginResultCallback callback) {
        MyApplication.madcapLogger.d(TAG, "silentLogin initiated.");

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
     * Performs a regualr login with an intent.
     */
    public synchronized void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        MyApplication.madcapLogger.d(TAG, signInIntent.toString());
        startActivityForResult(intent, requestCode);

//        callbackClass.onSignInIntent(signInIntent, RC_SIGN_IN);

    }

    /**
     * Sign out from Google Account. Calls callbackClass.onSignOutResults.
     */
    public synchronized void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status r) {
                        MyApplication.madcapLogger.e(TAG, "Logout status " + r);
                        //revokeAccess();
//                        callbackClass.onSignOutResults(r);
                    }
                });
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
     * Should be called to disconnect Google Account.
     */
    public synchronized void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status r) {
//                callbackClass.onRevokeAccess(r);
            }
        });
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
            result = user.getGivenName() + " " + user.getFamilyName();
        }
        return result;
    }

    public GoogleSignInAccount getUser() {
        return user;
    }
    public synchronized void setUser( GoogleSignInAccount user) {
        this.user = user;
    }

    @Override
    public synchronized void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        MyApplication.madcapLogger.e(TAG, "Connection to Google Authenticatin failed");
    }
}
