package org.fraunhofer.cese.madcap.authorization;

/**
 * Describes the callback methods for processing authorization results to MADCAP backend.
 */

interface AuthorizationHandler {
    /**
     * Called when the user has been successfully authorized to upload to the MADCAP backend.
     */
    void onAuthorized();

    /**
     * Called when the user is not authorized to upload to the MADCAP backend.
     */
    void onUnauthorized();

    /**
     * Called when an exception to the authorization process is encountered.
     *
     * @param exception the exception encountered during authoriation
     */
    void onError(AuthorizationException exception);
}
