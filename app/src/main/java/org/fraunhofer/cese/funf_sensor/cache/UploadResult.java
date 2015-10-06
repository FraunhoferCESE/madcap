package org.fraunhofer.cese.funf_sensor.cache;

import org.fraunhofer.cese.funf_sensor.backend.models.probeDataSetApi.model.ProbeSaveResult;

/**
 * Created by Lucas on 10/6/2015.
 */
public class UploadResult {


    private final boolean uploadAttempted;
    private final ProbeSaveResult saveResult;
    private final Throwable exception;

    private UploadResult(ProbeSaveResult saveResult, Throwable exception, boolean uploadAttempted) {
        this.saveResult = saveResult;
        this.exception = exception;
        this.uploadAttempted = uploadAttempted;
    }

    public static UploadResult create(Throwable exception) {
        return new UploadResult(null, exception, true);
    }

    public static UploadResult create(ProbeSaveResult saveResult) {
        return new UploadResult(saveResult, null, true);
    }

    public static UploadResult noop() {
        return new UploadResult(null, null, false);
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
