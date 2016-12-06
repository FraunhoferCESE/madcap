package org.fraunhofer.cese.madcap.authentication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import org.fraunhofer.cese.madcap.MyApplication;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Main entry point for authentication and getting the currently signed in user.
 * <p>
 * Created by MMueller on 9/29/2016.
 */
@Singleton
public class MadcapAuthManager {

    private static final String TAG = "MadcapAuthManager";

    private final GoogleSigninProvider googleSigninProvider;

    @Nullable
    private volatile GoogleSignInAccount user;

    //private static GoogleSignInApi googleSignInApi = Auth.GoogleSignInApi;

    @Inject
    MadcapAuthManager(GoogleSigninProvider googleSigninProvider) {
        this.googleSigninProvider = googleSigninProvider;
    }

    /**
     * Performs a silent login with cached credentials
     */
    public void silentLogin(@NonNull Context context, @NonNull final LoginResultCallback callback) {
        googleSigninProvider.silentSignIn(context, new LoginResultCallback() {
            @Override
            public void onServicesUnavailable(int connectionResult) {
                callback.onServicesUnavailable(connectionResult);
            }

            @Override
            public void onLoginResult(GoogleSignInResult signInResult) {
                callback.onLoginResult(signInResult);
                user = signInResult.getSignInAccount();  // will be null if failed
                MyApplication.madcapLogger.d(TAG, "User set to " + user);
            }

            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                callback.onConnectionFailed(connectionResult);

            }
        });
    }

    public void signout(@NonNull Context context, @NonNull final LogoutResultCallback callback) {
        googleSigninProvider.signout(context, new LogoutResultCallback() {
            @Override
            public void onServicesUnavailable(int connectionResult) {
                callback.onServicesUnavailable(connectionResult);
            }

            @Override
            public void onSignOut(Status result) {
                if (result.getStatusCode() == CommonStatusCodes.SUCCESS) {
                    user = null;
                }
                callback.onSignOut(result);

            }

            @Override
            public void onRevokeAccess(Status result) {
                callback.onRevokeAccess(result);
            }

            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                callback.onConnectionFailed(connectionResult);
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

    @Nullable
    public GoogleSignInAccount getUser() {
        return user;
    }




}
