package org.fraunhofer.cese.madcap.authorization;

import org.fraunhofer.cese.madcap.backend.probeEndpoint.model.UserCheckResult;

/**
 * Helper class for wrapping endpoint authorization results and errors.
 */

@SuppressWarnings({"AssignmentToCollectionOrArrayFieldFromParameter", "ReturnOfCollectionOrArrayField"})
public class AuthorizationResult {
    private UserCheckResult userCheckResult;
    private AuthorizationException exception;

    UserCheckResult getUserCheckResult() {
        return userCheckResult;
    }

    void setUserCheckResult(UserCheckResult userCheckResult) {
        this.userCheckResult = userCheckResult;
    }

    public AuthorizationException getException() {
        return exception;
    }

    public void setException(AuthorizationException exception) {
        this.exception = exception;
    }
}
