package org.fraunhofer.cese.funf_sensor.cache;

import android.content.Context;

import org.fraunhofer.cese.funf_sensor.backend.models.probeDataSetApi.ProbeDataSetApi;
import org.fraunhofer.cese.funf_sensor.backend.models.probeDataSetApi.model.ProbeSaveResult;

/**
 * Class for capturing the results of remote upload attempts by the cache.
 *
 * @see Cache
 * @see RemoteUploadAsyncTaskFactory#createRemoteUploadTask(Context, Cache, ProbeDataSetApi)
 */
public class RemoteUploadResult {

    private boolean uploadAttempted;
    private ProbeSaveResult saveResult;
    private Throwable exception;

    public RemoteUploadResult() {
        this.saveResult = null;
        this.exception = null;
        this.uploadAttempted = false;
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
