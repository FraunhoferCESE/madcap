package org.fraunhofer.cese.madcap.backend.models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import org.json.JSONObject;

/**
 * Created by APatil on 07/30/2019.
 * The corresponding entity hsa not been created in the cloud datastore yet
 * Do not enable the AppPermissionsProbe creation and upload without creating the entity
 */

@Entity
public class AppPermissionsEntry implements Comparable<AppPermissionsEntry>, DatastoreEntry {
    @Id
    private String id;
    @Index
    private Long timestamp;
    @Index
    private String packageName;
    private String permissionsRejected;
    private String permissionsGranted;
    @Index
    private String userID;

    public AppPermissionsEntry() {

    }

    public AppPermissionsEntry(ProbeEntry probeEntry) {
        id = probeEntry.getId();
        timestamp = probeEntry.getTimestamp();
        userID = probeEntry.getUserID();

        // parsing the data
        JSONObject dataJsonObject = new JSONObject(probeEntry.getSensorData());
        packageName = dataJsonObject.getString("packageName");
        permissionsGranted = dataJsonObject.getString("permissionsGranted");
        permissionsRejected = dataJsonObject.getString("permissionsRejected");
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     * <p>
     * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)
     * <p>
     * <p>The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.
     * <p>
     * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.
     * <p>
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     * <p>
     * <p>In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of
     * <i>expression</i> is negative, zero or positive.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(AppPermissionsEntry o) {
        return 0;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Long getTimestamp() {
        return timestamp;
    }

    @Override
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPermissionsRejected() {
        return permissionsRejected;
    }

    public void setPermissionsRejected(String permissionsRejected) {
        this.permissionsRejected = permissionsRejected;
    }

    public String getPermissionsGranted() {
        return permissionsGranted;
    }

    public void setPermissionsGranted(String permissionsGranted) {
        this.permissionsGranted = permissionsGranted;
    }

    @Override
    public String getUserID() {
        return userID;
    }

    @Override
    public void setUserID(String userID) {
        this.userID = userID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppPermissionsEntry that = (AppPermissionsEntry) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null)
            return false;
        if (packageName != null ? !packageName.equals(that.packageName) : that.packageName != null)
            return false;
        if (permissionsRejected != null ? !permissionsRejected.equals(that.permissionsRejected) : that.permissionsRejected != null)
            return false;
        if (permissionsGranted != null ? !permissionsGranted.equals(that.permissionsGranted) : that.permissionsGranted != null)
            return false;
        return (userID != null ? !userID.equals(that.userID) : that.userID !=null);

    }

    @Override
    public int hashCode() {
        int result;
        result = id.hashCode();
        result = 31 * result + timestamp.hashCode();
        result = 31 * result + packageName.hashCode();
        result = 31 * result + permissionsRejected.hashCode();
        result = 31 * result + permissionsGranted.hashCode();
        result = 31 * result + userID.hashCode();
        return result;
    }
}
