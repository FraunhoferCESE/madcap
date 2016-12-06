package org.fraunhofer.cese.madcap.authentication;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;

/**
 * Interface that specifies the types of events that should be handled when attempting to sign a user out of MADCAP.
 *
 * Created by llayman on 12/6/2016.
 */

public interface LogoutResultCallback extends GoogleApiClient.OnConnectionFailedListener  {
    void onServicesUnavailable(int connectionResult);
    void onSignOut(Status result);
    void onRevokeAccess(Status result);
}
