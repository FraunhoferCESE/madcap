package org.fraunhofer.cese.madcap.authentication;

import android.content.Context;
import android.content.Intent;
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
 * Main entry point for authentication and getting the currently signed in user.
 * <p>
 * Created by MMueller on 9/29/2016.
 */
@Singleton
public class AuthenticationProvider {

    private static final String TAG = "AuthenticationProvider";

    private final GoogleApiClient mGoogleApiClient;

    @Nullable
    private volatile GoogleSignInAccount user;

    @Inject
    AuthenticationProvider(@Named("SigninApi") GoogleApiClient googleApiClient) {
        mGoogleApiClient = googleApiClient;
    }

    /**
     * Helper method for interactive signin to handle common SignInApi issues.
     *
     * @param activity   the calling activity. Must implement {@link android.app.Activity#onActivityResult(int, int, Intent)}
     * @param resultCode the result code that is listened for by the calling activity's {@link android.app.Activity#onActivityResult(int, int, Intent)} method
     * @param callback   callback class for handling common login events
     */
    void interactiveSignIn(@NonNull final SignInActivity activity, final int resultCode, @NonNull LoginResultCallback callback) {
        MyApplication.madcapLogger.d(TAG, "interactiveSignIn initiated");

        int connectionResult = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(activity);
        if (connectionResult != ConnectionResult.SUCCESS) {
            callback.onServicesUnavailable(connectionResult);
            return;
        }

        if (mGoogleApiClient.isConnected()) {
            activity.startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient), resultCode);
        } else {
            mGoogleApiClient.registerConnectionFailedListener(callback);
            mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(@Nullable Bundle bundle) {
                    mGoogleApiClient.unregisterConnectionCallbacks(this);
                    activity.startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient), resultCode);
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
     * Attemps to perform a silent login using cached credentials
     *
     * @param context  the calling context
     * @param callback callback handler for login event callbacks triggered during the silent login attempt.
     */
    public void silentLogin(@NonNull Context context, @NonNull final SilentLoginResultCallback callback) {
        MyApplication.madcapLogger.d(TAG, "silentSignIn initiated");

        int connectionResult = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        if (connectionResult != ConnectionResult.SUCCESS) {
            callback.onServicesUnavailable(connectionResult);
            return;
        }

        if (mGoogleApiClient.isConnected()) {
            doSignin(callback);
        } else {
            mGoogleApiClient.registerConnectionFailedListener(callback);
            mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(@Nullable Bundle bundle) {
                    mGoogleApiClient.unregisterConnectionCallbacks(this);
                    doSignin(callback);
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
     * private method to handle actual google sign in events
     *
     * @param callback the callback to handle sign in events
     */
    private void doSignin(@NonNull final SilentLoginResultCallback callback) {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);

        if (opr.isDone()) {
            // In Case there is a result available intermediately. This should happen if they signed in before.
            GoogleSignInResult result = opr.get();
            MyApplication.madcapLogger.d(TAG, "Immediate result available: " + result);
            user = result.getSignInAccount();
            callback.onLoginResult(result);
        } else {
            MyApplication.madcapLogger.d(TAG, "Immediate results are not evailable.");
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult r) {
                    MyApplication.madcapLogger.d(TAG, "Received asynchronous login result. Code: " + r.getStatus().getStatusCode() + ", message: " + r.getStatus().getStatusMessage());
                    user = r.getSignInAccount();
                    callback.onLoginResult(r);
                }
            });
        }
    }


    /**
     * Attempts to log the user out using the Google SignIn API
     *
     * @param context  the calling context
     * @param callback callback handler for logout events
     */
    public void signout(@NonNull Context context, @NonNull final LogoutResultCallback callback) {
        int connectionResult = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        if (connectionResult != ConnectionResult.SUCCESS) {
            callback.onServicesUnavailable(connectionResult);
            return;
        }

        if (mGoogleApiClient.isConnected()) {
            doSignout(callback);
        } else {
            mGoogleApiClient.registerConnectionFailedListener(callback);
            mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(@Nullable Bundle bundle) {
                    mGoogleApiClient.unregisterConnectionCallbacks(this);
                    doSignout(callback);
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
     * Private method to handle actual calls to google signout
     *
     * @param callback specifies how to handle various signout events
     */
    private void doSignout(@NonNull final LogoutResultCallback callback) {
        user = null;
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status r) {
                callback.onRevokeAccess(r);
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new SignOutResultCallback(callback));
            }
        });
    }

    /**
     * Retrieves the currently logged in User Id.
     *
     * @return User ID.
     */
    @SuppressWarnings("ConstantConditions")
    @Nullable
    public String getUserId() {
        return (user == null) ? null : user.getId();
    }

    /**
     * Makes the Signed in User accessable.
     *
     * @return the last users name.
     */
    @SuppressWarnings({"TypeMayBeWeakened", "ConstantConditions"})
    @Nullable
    public String getLastSignedInUsersName() {
        return (user == null) ? null : (user.getGivenName() + ' ' + user.getFamilyName());
    }

    /**
     * Gets the currently signed in user.
     *
     * @return the signed in user.
     */
    @Nullable
    public GoogleSignInAccount getUser() {
        return user;
    }

    /**
     * Sets the currently logged in user. Only exposed here to support interactive sign in with the google sign in activitiy.
     *
     * @param user the user to set as currently logged in
     */
    void setUser(@Nullable GoogleSignInAccount user) {
        this.user = user;
    }


    /**
     * Used to handle the signout event following the revoke access event. Static class recommended by inspector to avoid memory issues.
     */
    private static class SignOutResultCallback implements ResultCallback<Status> {
        private final LogoutResultCallback callback;

        SignOutResultCallback(LogoutResultCallback callback) {
            this.callback = callback;
        }

        @Override
        public void onResult(@NonNull Status r) {
            callback.onSignOut(r);
        }
    }
}
