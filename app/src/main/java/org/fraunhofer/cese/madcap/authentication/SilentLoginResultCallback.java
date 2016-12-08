package org.fraunhofer.cese.madcap.authentication;

import com.google.android.gms.auth.api.signin.GoogleSignInResult;

/**
 * Captured silent login events from the AuthenticationProvider. Callers to AuthenticationProvider#silentLogin() must supply a callback that implements these methods.
 *
 * Created by llayman on 11/28/2016.
 */

public interface SilentLoginResultCallback extends LoginResultCallback {
    void onLoginResult(GoogleSignInResult signInResult);
}