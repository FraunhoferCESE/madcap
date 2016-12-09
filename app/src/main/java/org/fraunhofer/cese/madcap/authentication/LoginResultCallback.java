package org.fraunhofer.cese.madcap.authentication;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Specifies callback methods that are triggered during login activities.
 *
 * Created by llayman on 12/8/2016.
 */

interface LoginResultCallback extends GoogleApiClient.OnConnectionFailedListener {
    void onServicesUnavailable(int connectionResult);
}
