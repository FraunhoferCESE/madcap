package org.fraunhofer.cese.madcap.cache;

import android.content.Context;

import org.fraunhofer.cese.madcap.backend.probeEndpoint.ProbeEndpoint;
import org.fraunhofer.cese.madcap.backend.probeEndpoint.model.ProbeSaveResult;

import java.util.List;

/**
 * Class for capturing the results of remote upload attempts by the cache.
 *
 * @see Cache
 * @see RemoteUploadAsyncTaskFactory#createRemoteUploadTask(Context, Cache, ProbeEndpoint, List)
 */
public class RemoteUploadResult {

    private boolean uploadAttempted;
    private ProbeSaveResult saveResult;
    private Throwable exception;

    public RemoteUploadResult() {  }

    public boolean isUploadAttempted() {
        return uploadAttempted;
    }

    public ProbeSaveResult getSaveResult() {
        return saveResult;
    }

    public Throwable getException() {
        return exception;
    }

    public void setUploadAttempted(boolean uploadAttempted) {
        this.uploadAttempted = uploadAttempted;
    }

    public void setSaveResult(ProbeSaveResult saveResult) {
        this.saveResult = saveResult;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }
}
