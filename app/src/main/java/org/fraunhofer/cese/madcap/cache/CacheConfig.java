package org.fraunhofer.cese.madcap.cache;

/**
 * Contains various configuration parameters needed by the Cache to operate.
 *
 * @author Lucas
 * @see Cache
 */
public class CacheConfig {

    private int maxMemEntries;
    private long maxDbEntries;

    private long memForcedCleanupLimit;
    private long dbForcedCleanupLimit;

    private long dbWriteInterval;
    private long uploadInterval;
    private boolean uploadWifiOnly;

    private boolean writeToFile;

    public boolean getWriteToFile() {
        return writeToFile;
    }

    public void setWriteToFile(boolean writeToFile) {
        this.writeToFile = writeToFile;
    }

    public void setMaxMemEntries(int maxMemEntries) {
        this.maxMemEntries = maxMemEntries;
    }

    public void setMaxDbEntries(int maxDbEntries) {
        this.maxDbEntries = maxDbEntries;
    }

    public void setMemForcedCleanupLimit(int memForcedCleanupLimit) {
        this.memForcedCleanupLimit = memForcedCleanupLimit;
    }

    public void setDbForcedCleanupLimit(int dbForcedCleanupLimit) {
        this.dbForcedCleanupLimit = dbForcedCleanupLimit;
    }

    public void setDbWriteInterval(long dbWriteInterval) {
        this.dbWriteInterval = dbWriteInterval;
    }

    public void setUploadInterval(long uploadInterval) {
        this.uploadInterval = uploadInterval;
    }

    public void setUploadWifiOnly(boolean uploadWifiOnly) {
        this.uploadWifiOnly = uploadWifiOnly;
    }

    /**
     * Gets the maximum number of cache entries to hold in memory before writing to the backing database store.
     *
     * @return the maximum number of entries to be held in memory
     */
    public int getMaxMemEntries() {
        return maxMemEntries;
    }

    /**
     * Gets the maximum number of cache entries to hold in the backing database before attempting a remote upload.
     *
     * @return the maximum number of entries to be held in the database
     */
    public long getMaxDbEntries() {
        return maxDbEntries;
    }

    /**
     * Gets the upper limit on the number of entries to hold in memory before purging some of the entries to preserve memory.
     *
     * @return the limit for number of entries to be stored in memory
     */
    public long getMemForcedCleanupLimit() {
        return memForcedCleanupLimit;
    }

    /**
     * Gets the upper limit on the number of entries to hold in the backing database before purging items to preserve disk space.
     *
     * @return the limit for number of entries to be stored in the database
     */
    public long getDbForcedCleanupLimit() {
        return dbForcedCleanupLimit;
    }

    /**
     * Length of time, in milliseconds, that must pass between consecutive attempts to write memory entries to the backing database.
     *
     * @return minimum time in milliseconds between consecutive DB writes
     */
    public long getDbWriteInterval() {
        return dbWriteInterval;
    }

    /**
     * Length of time, in milliseconds, that must pass between consecutive attempts to upload entries to the remote server.
     *
     * @return minimum time in milliseconds between consecutive upload attempts
     */
    public long getUploadInterval() {
        return uploadInterval;
    }

    /**
     * Determines whether uploads should be attempted when connected to WiFi only, or over any data connection (which may result in user's
     * data plan being consumed)
     *
     * @return <code>true</code> if uploads should happen on WiFi only, <code>false</code> if uploads can happen over any data connection.
     */
    public boolean isUploadWifiOnly() {
        return uploadWifiOnly;
    }
}
