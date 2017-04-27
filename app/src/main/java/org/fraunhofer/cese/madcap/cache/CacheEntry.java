package org.fraunhofer.cese.madcap.cache;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.fraunhofer.cese.madcap.backend.probeEndpoint.model.ProbeEntry;

import timber.log.Timber;


/**
 * Local cache entry for Probe data. This holds the same data as ProbeEntry, but this data needs to be duplicated locally within the app
 * since an ORM cannot be built for data types imported from the backend API.
 *
 * @author Lucas
 * @see ProbeEntry
 */
@DatabaseTable(tableName = "probedata")
public class CacheEntry {

    @SuppressWarnings("InstanceVariableNamingConvention")
    @DatabaseField(id = true)
    private String id;
    static final String ID_FIELD_NAME = "id";

    @DatabaseField
    private Long timestamp;
    static final String TIMESTAMP_FIELD_NAME = "timestamp";

    @DatabaseField
    private String probeType;

    @DatabaseField
    private String sensorData;

    @DatabaseField
    private String userID;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @SuppressWarnings("WeakerAccess")
    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @SuppressWarnings("WeakerAccess")
    public String getProbeType() {
        return probeType;
    }

    public void setProbeType(String probeType) {
        this.probeType = probeType;
    }

    @SuppressWarnings("WeakerAccess")
    public String getSensorData() {
        return sensorData;
    }

    public void setSensorData(String sensorData) {
        this.sensorData = sensorData;
    }

    @SuppressWarnings("WeakerAccess")
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @SuppressWarnings("CallToSimpleGetterFromWithinClass")
    static ProbeEntry createProbeEntry(CacheEntry entry) {
        if(entry == null) {
            Timber.e("CacheEntry","createProbeEntry: CacheEntry parameter is null.");
            throw new NullPointerException("createProbeEntry: CacheEntry parameter is null.");
        }

        ProbeEntry probeEntry = new ProbeEntry();
        probeEntry.setId(entry.getId());
        probeEntry.setTimestamp(entry.getTimestamp());
        probeEntry.setProbeType(entry.getProbeType());
        probeEntry.setSensorData(entry.getSensorData());
        probeEntry.setUserID(entry.getUserID());
        return probeEntry;
    }

    /**
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "Type: "+probeType+" Data: "+sensorData+" User: "+userID+" Time: "+timestamp+" ID:"+id ;
    }
}
