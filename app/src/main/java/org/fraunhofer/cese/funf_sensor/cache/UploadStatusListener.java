package org.fraunhofer.cese.funf_sensor.cache;

/**
 * Interface for listening to the status of remote upload attempts by the cache. Classes implementing this interface can be registered using
 * {@link Cache#addUploadListener(UploadStatusListener)}.
 */
public interface UploadStatusListener {

    /**
     * Called when a remote upload attempt has finished.
     *
     * @param result the remote upload result, which can be {@code null} in certain rare cases of an internal error.
     */
    void uploadFinished(RemoteUploadResult result);

    /**
     * Called when the the cache is being closed. The listener is automatically unregistered from the cache immediately after this call.
     */
    void cacheClosing();

}
