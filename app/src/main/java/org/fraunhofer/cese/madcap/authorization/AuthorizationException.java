package org.fraunhofer.cese.madcap.authorization;

/**
 * Captures exceptions that can occur during MADCAP authorization to the backend.
 */

@SuppressWarnings({"SerializableClassInSecureContext", "SerializableHasSerializationMethods", "serial"})
class AuthorizationException extends Exception {

    AuthorizationException(String message) { super(message); }

    AuthorizationException(Throwable cause) {
        super(cause);
    }
}
