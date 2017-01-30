package org.fraunhofer.cese.madcap.cache;

/**
 * Created by MMueller on 11/23/2016.
 *
 * Interface for Listening to a remote upload update from a UploadStatusListener
 * for purposes of GUI updates.
 *
 * Only activities/fragments etc should implement this interface.
 */

public interface UploadStatusGuiListener {

    /**
     * Getting called when there is an update on the date of the upload status.
     * @param date the date to update.
     */
    void onUploadStatusDateUpdate(String date);

    /**
     * Getting called when there is an update on the completeness is available.
     * @param completeness the new completeness status.
     */
    void onUploadStatusCompletenessUpdate(Completeness completeness);

    /**
     * Getting called when there is an update on the result available.
     * @param result the new result.
     */
    void onUploadStatusResultUpdate(String result);

    /**
     * Getting called when there is an update on the progress of the upload
     * process available.
     * @param percent the percentage of the upload.
     */
    void onUploadStatusProgressUpdate(int percent);

    enum Completeness{
        COMPLETE,
        INCOMPLETE
    }

    void restoreLastUpload();
}
