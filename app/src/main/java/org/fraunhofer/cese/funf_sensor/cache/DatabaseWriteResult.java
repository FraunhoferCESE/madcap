package org.fraunhofer.cese.funf_sensor.cache;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Captures the results of writing cache entries to a persistent backing database.
 *
 * @author Lucas
 * @see Cache
 * @see DatabaseAsyncTaskFactory#createWriteTask(Cache)
 */
public class DatabaseWriteResult {

    private Collection<String> savedEntries;
    private long databaseSize;
    private Exception error;

    /**
     * Direct constructor calls are not supported.
     */
    private DatabaseWriteResult() {
        savedEntries = new ArrayList<>();
        databaseSize = -1;
        error = null;
    }

    /**
     * Factory method for getting a new DatabaseWriteResult. Direct constructor calls are not supported.
     *
     * @return a new DatabaseWriteResult instance
     */
    static DatabaseWriteResult create() {
        return new DatabaseWriteResult();
    }

    /**
     * Returns an Exception associated with an error, if any, that occurred during writing to the database.
     *
     * @return the exception that occurred, or <code>null</code> if no write errors were encountered
     */
    public Throwable getError() {
        return error;
    }

    /**
     * Sets the exception encountered during database access
     *
     * @param error the observed exception to pass on
     */
    void setError(Exception error) {
        this.error = error;
    }

    /**
     * Returns a Collection of ids whose entries were successfully written to the backing database
     *
     * @return the collection of ids that were saved to the database, or an empty collection if nothing was written
     */
    public Collection<String> getSavedEntries() {
        return savedEntries;
    }

    /**
     * Sets the collection of ids of cache entries that were successfully written to the backing database
     *
     * @param savedEntries the ids of entries saved
     */
    void setSavedEntries(Collection<String> savedEntries) {
        this.savedEntries = savedEntries;
    }

    /**
     * Gets the current database size (number of entries) after attempting to write to the database.
     *
     * @return the current database size, or <code>-1</code> if the size cannot be obtained because the database is not readable
     */
    public long getDatabaseSize() {
        return databaseSize;
    }

    void setDatabaseSize(long databaseSize) {
        this.databaseSize = databaseSize;
    }

    @Override
    public String toString() {
        return "DatabaseWriteResult{" +
                "savedEntries=" + savedEntries +
                ", databaseSize=" + databaseSize +
                ", error=" + error +
                '}';
    }
}
