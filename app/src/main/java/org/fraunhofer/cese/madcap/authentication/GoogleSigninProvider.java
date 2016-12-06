package org.fraunhofer.cese.madcap.authentication;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.Status;

/**
 * Defines the interface consumed by MADCAP for google sign in/out events
 *
 * Created by llayman on 12/6/2016.
 */

public interface GoogleSigninProvider {

    /**
     * Tries to perform a silent sign in using the {@link com.google.android.gms.auth.api.signin.GoogleSignInApi}.
     * <p>
     * How to handle successful and failed sign in attempts should be specified in the {@link LoginResultCallback} parameter
     *
     * @param context  the calling context
     * @param callback parameter that specifies how to handle sign in events
     */
    void silentSignIn(@NonNull Context context, @NonNull LoginResultCallback callback);

    /**
     * Attempts to sign out a user. The user will be signed out and MADCAP's access to the user's account will be revoked.
     * <p>
     * The user is set to @code null on successful signout. The callee should specify how to handle success or failures of signout by checking the {@link Status} on {@link LogoutResultCallback#onSignOut(Status)} and {@link LogoutResultCallback#onRevokeAccess(Status)}
     *
     * @param context  the calling context
     * @param callback specifies how to handle various signout events
     */
    void signout(@NonNull Context context, @NonNull LogoutResultCallback callback);
}
