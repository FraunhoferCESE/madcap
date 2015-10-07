package org.fraunhofer.cese.funf_sensor.cache;

import org.fraunhofer.cese.funf_sensor.backend.models.probeDataSetApi.ProbeDataSetApi;
import org.fraunhofer.cese.funf_sensor.backend.models.probeDataSetApi.model.ProbeSaveResult;

/**
 * Class for capturing the results of remote upload attempts by the cache.
 *
 * @see Cache
 * @see RemoteUploadAsyncTaskFactory#createRemoteUploadTask(Cache, ProbeDataSetApi)
 */
public class RemoteUploadResult {

    private final boolean uploadAttempted;
    private final ProbeSaveResult saveResult;
    private final Throwable exception;

    private RemoteUploadResult(ProbeSaveResult saveResult, Throwable exception, boolean uploadAttempted) {
        this.saveResult = saveResult;
        this.exception = exception;
        this.uploadAttempted = uploadAttempted;
    }

    public static RemoteUploadResult create(Throwable exception) {
        return new RemoteUploadResult(null, exception, true);
    }

    public static RemoteUploadResult create(ProbeSaveResult saveResult) {
        return new RemoteUploadResult(saveResult, null, true);
    }

    public static RemoteUploadResult noop() {
        return new RemoteUploadResult(null, null, false);
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
}
