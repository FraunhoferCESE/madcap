package org.fraunhofer.cese.madcap.cache;

import android.content.Context;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Captures the results of writing cache entries to a persistent backing database.
 *
 * @author Lucas
 * @see Cache
 * @see DatabaseAsyncTaskFactory#createWriteTask(Context, UploadStrategy)
 */
@SuppressWarnings("FinalClass")
final class DatabaseWriteResult {

    private Collection<String> savedEntries;
    private long databaseSize;
    @Nullable
    private Exception error;

    private UploadStrategy uploadStrategy;

    /**
     * Direct constructor calls are not supported.
     */
    private DatabaseWriteResult(UploadStrategy strategy) {
        savedEntries = new ArrayList<>(1000);
        databaseSize = -1L;
        error = null;
        uploadStrategy = strategy;
    }

    /**
     * Factory method for getting a new DatabaseWriteResult. Direct constructor calls are not supported.
     *
     * @return a new DatabaseWriteResult instance
     */
    static DatabaseWriteResult create(UploadStrategy uploadStrategy) {
        return new DatabaseWriteResult(uploadStrategy);
    }

    /**
     * Returns an Exception associated with an error, if any, that occurred during writing to the database.
     *
     * @return the exception that occurred, or {@code null} if no write errors were encountered
     */
    public Throwable getError() {
        return error;
    }

    /**
     * Sets the exception encountered during database access
     *
     * @param error the observed exception to pass on
     */
    void setError(@Nullable Exception error) {
        this.error = error;
    }

    /**
     * Returns a Collection of ids whose entries were successfully written to the backing database
     *
     * @return the collection of ids that were saved to the database, or an empty collection if nothing was written
     */
    Collection<String> getSavedEntries() {
        return Collections.unmodifiableCollection(savedEntries);
    }

    /**
     * Sets the collection of ids of cache entries that were successfully written to the backing database
     *
     * @param savedEntries the ids of entries saved
     */
    void setSavedEntries(Collection<String> savedEntries) {
        this.savedEntries = Collections.unmodifiableCollection(savedEntries);
    }

    /**
     * Gets the current database size (number of entries) after attempting to write to the database.
     *
     * @return the current database size, or {@code -1} if the size cannot be obtained because the database is not readable
     */
    long getDatabaseSize() {
        return databaseSize;
    }

    void setDatabaseSize(long databaseSize) {
        this.databaseSize = databaseSize;
    }

    public UploadStrategy getUploadStrategy() {
        return uploadStrategy;
    }

    public void setUploadStrategy(UploadStrategy uploadStrategy) {
        this.uploadStrategy = uploadStrategy;
    }

    @Override
    public String toString() {
        return "DatabaseWriteResult{" +
                "savedEntries=" + savedEntries +
                ", databaseSize=" + databaseSize +
                ", error=" + error +
                ", uploadStrategy=" + uploadStrategy +
                '}';
    }
}
