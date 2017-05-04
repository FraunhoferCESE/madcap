package org.fraunhofer.cese.madcap.authorization;

/**
 * Captures exceptions that can occur during MADCAP authorization to the backend.
 */

@SuppressWarnings({"SerializableClassInSecureContext", "SerializableHasSerializationMethods", "serial"})
class AuthorizationException extends Exception {

    AuthorizationException() { super("userCheckResult is null"); }

    AuthorizationException(Throwable cause) {
        super(cause);
    }
}
