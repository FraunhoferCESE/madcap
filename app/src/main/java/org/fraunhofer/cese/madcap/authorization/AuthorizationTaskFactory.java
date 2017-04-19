package org.fraunhofer.cese.madcap.authorization;

import android.content.Context;

import org.fraunhofer.cese.madcap.util.EndpointApiBuilder;

import javax.inject.Inject;

/**
 * Factory class for creating AuthorizationTask background tasks.
 */
public class AuthorizationTaskFactory {
    private final EndpointApiBuilder endpointApiBuilder;

    @Inject
    AuthorizationTaskFactory(EndpointApiBuilder endpointApiBuilder) {
        this.endpointApiBuilder = endpointApiBuilder;
    }

    /**
     * Creates an asynctask to check the authorization of the currently signed-in user to upload data to MADCAP.
     *
     * @param context the calling context
     * @return an asynctask to be executed by the callee to check the
     */
    public AuthorizationTask createAuthorizationTask(Context context, AuthorizationHandler handler) {
        return new AuthorizationTask(handler, endpointApiBuilder.build(context));
    }
}
