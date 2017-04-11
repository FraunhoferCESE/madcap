package org.fraunhofer.cese.madcap.util;

import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import org.fraunhofer.cese.madcap.R;
import org.fraunhofer.cese.madcap.authentication.AuthenticationProvider;
//import org.fraunhofer.cese.madcap.backend.Constants;
import org.fraunhofer.cese.madcap.backend.probeEndpoint.ProbeEndpoint;

import javax.inject.Inject;

/**
 * Wrapper for the Endpoint API builder class to communicate with the app enginer. Wrapper is needed to accomodate user login/logout during app lifecycle.
 * <p>
 * Created by llayman on 2/8/2017.
 */

public class EndpointApiBuilder {

    private final AuthenticationProvider authenticationProvider;

    @Inject
    public EndpointApiBuilder(AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }


    /**
     * Create the Endpoint interface for uploading data to App Engine. Uses the logged in user as a credential.
     *
     * @param context the application context
     * @return the interface for uploading data to the app enginine
     */
    public ProbeEndpoint build(Context context) {
        GoogleAccountCredential credential = null;
        GoogleSignInAccount user = (authenticationProvider.getUser() != null) ? authenticationProvider.getUser() : authenticationProvider.getLastLoggedInUser();

        if (user != null) {
            credential = GoogleAccountCredential.usingAudience(context, context.getResources().getString(R.string.webClientId));
            credential.setSelectedAccountName(user.getEmail());
        }

        String endpointUrl = "https://madcap-dev1.appspot.com/_ah/api/";
        //  TODO: change this for production
//      String endpointUrl = "https://madcap-142815.appspot.com/_ah/api/";

        ProbeEndpoint.Builder builder = new ProbeEndpoint.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), credential)
                .setApplicationName("Fraunhofer MADCAP")
                .setRootUrl(endpointUrl);

        return builder.build();
    }
}
