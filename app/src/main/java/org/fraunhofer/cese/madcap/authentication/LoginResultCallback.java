package org.fraunhofer.cese.madcap.authentication;

import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Captured silent login events from the MadcapAuthManager. Callers to MadcapAuthManager#silentLogin() must supply a callback that implements these methods.
 *
 * Created by llayman on 11/28/2016.
 */

public interface LoginResultCallback extends GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    void onServicesUnavailable(int connectionResult);
    void onLoginResult(GoogleSignInResult signInResult);
}