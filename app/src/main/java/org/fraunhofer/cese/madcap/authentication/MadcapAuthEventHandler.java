package org.fraunhofer.cese.madcap.authentication;

import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.Status;

/**
 * Created by MMueller on 9/29/2016.
 */

public interface MadcapAuthEventHandler {

    /**
     * Specifies what the class is expected to do, when the silent login was sucessfull.
     */
    void onSilentLoginSuccessfull(GoogleSignInResult result);

    /**
     * Specifies what the class is expected to do, when the silent login was not successfull.
     */
    void onSilentLoginFailed(OptionalPendingResult<GoogleSignInResult> opr);

    /**
     * Specifies what the class is expected to do, when the regular sign in was successful.
     */
    void onSignInSucessfull();

    /**
     * Specifies what the app is expected to do when the Signout was sucessfull.
     */
    void onSignOutResults(Status status);

    /**
     * Specifies what the class is expected to do, when disconnected.
     * @param status
     */
    void onRevokeAccess(Status status);

    /**
     * There the sign in intent has to be sent.
     * @param intent
     * @param requestCode
     */
    void onSignInIntent(Intent intent, int requestCode);

}
