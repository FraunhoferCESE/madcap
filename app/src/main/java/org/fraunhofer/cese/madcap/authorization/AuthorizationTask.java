package org.fraunhofer.cese.madcap.authorization;

import android.os.AsyncTask;

import org.fraunhofer.cese.madcap.MyApplication;
import org.fraunhofer.cese.madcap.backend.probeEndpoint.ProbeEndpoint;
import org.fraunhofer.cese.madcap.backend.probeEndpoint.model.UserCheckResult;

import java.io.IOException;

/**
 * Asynchronous task to check that the currently signed-in user is authorized to upload data to MADCAP.
 */
public class AuthorizationTask extends AsyncTask<Void, Void, AuthorizationResult> {
    private static final String TAG = "AuthorizationTask";

    private final AuthorizationHandler handler;
    private final ProbeEndpoint endpoint;

    AuthorizationTask(AuthorizationHandler handler, ProbeEndpoint endpoint) {
        this.handler = handler;
        this.endpoint = endpoint;
    }

    @Override
    public AuthorizationResult doInBackground(Void... params) {
        AuthorizationResult result = new AuthorizationResult();

        try {
            result.setUserCheckResult(endpoint.checkSignedUpUser().execute());
        } catch (IOException e) {
            MyApplication.madcapLogger.w(TAG, e.getMessage());
            result.setException(new AuthorizationException(e));
        }

        return result;
    }

    @Override
    protected void onPostExecute(AuthorizationResult result) {
        if (result.getException() != null) {
            handler.onError(result.getException());
            return;
        }

        UserCheckResult userCheckResult = result.getUserCheckResult();
        if (userCheckResult == null) {
            handler.onError(new AuthorizationException("userCheckResult is null"));
        } else if (userCheckResult.getAuthorized()) {
            handler.onAuthorized();
        } else {
            handler.onUnauthorized();
        }

    }

}
