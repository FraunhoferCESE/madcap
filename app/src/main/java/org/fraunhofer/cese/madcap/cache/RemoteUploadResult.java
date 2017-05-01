package org.fraunhofer.cese.madcap.cache;

import android.content.Context;

import org.fraunhofer.cese.madcap.backend.probeEndpoint.model.ProbeSaveResult;
import org.fraunhofer.cese.madcap.util.EndpointApiBuilder;

/**
 * Class for capturing the results of remote upload attempts by the cache.
 *
 * @see Cache
 * @see RemoteUploadAsyncTaskFactory#createRemoteUploadTask(Context, EndpointApiBuilder)
 */
public class RemoteUploadResult {

    private boolean uploadAttempted;
    private ProbeSaveResult saveResult;
    private Throwable exception;

    RemoteUploadResult() {
    }

    public boolean isUploadAttempted() {
        return uploadAttempted;
    }

    public ProbeSaveResult getSaveResult() {
        return saveResult;
    }

    public Throwable getException() {
        return exception;
    }

    void setUploadAttempted() {
        uploadAttempted = true;
    }

    void setSaveResult(ProbeSaveResult saveResult) {
        this.saveResult = saveResult;
    }

    void setException(Throwable exception) {
        this.exception = exception;
    }
}
