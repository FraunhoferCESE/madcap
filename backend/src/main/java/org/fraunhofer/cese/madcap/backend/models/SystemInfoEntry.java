package org.fraunhofer.cese.madcap.backend.models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import org.json.JSONObject;

import java.util.HashMap;

import static com.google.appengine.api.datastore.IMHandle.Scheme.unknown;

/**
 * Created by MMueller on 1/3/2017.
 */
@Entity
public class SystemInfoEntry implements Comparable<SystemInfoEntry>, DatastoreEntry {
    @Id
    private String id;
    @Index
    private Long timestamp;
    private String manufacturer;
    private String model;
    private double apiLevel;
    private String madcapVersion;
    @Index
    private String userID;

    public SystemInfoEntry(){
    }

    public SystemInfoEntry(ProbeEntry probeEntry){
        id = probeEntry.getId();
        timestamp = probeEntry.getTimestamp();
        userID = probeEntry.getUserID();

        JSONObject dataJsonObject = new JSONObject(probeEntry.getSensorData());
        manufacturer = dataJsonObject.getString("manufacturer");
        model = dataJsonObject.getString("model");
        apiLevel = dataJsonObject.getDouble("apiLevel");
        madcapVersion = dataJsonObject.getString("madcapVersion");
    }

    /**
     * Gets the device manufacturer.
     * E.g. Samsung
     * @return manufacturer.
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * Sets the device manufacturer.
     * E.g. Samsung
     * @param manufacturer to be set.
     */
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    /**
     * Get the model description.
     * E.g. Galaxy S7
     * @return model/
     */
    public String getModel() {
        return model;
    }

    /**
     * Set the model description.
     * E.g. Galaxy S7
     * @param model to be set.
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Gets the api level.
     * @return api level.
     */
    public double getApiLevel() {
        return apiLevel;
    }

    /**
     * Sets the api level.
     * @param apiLevel
     */
    public void setApiLevel(double apiLevel) {
        this.apiLevel = apiLevel;
    }

    /**
     * Gets the current MadcapVersion.
     * @return the madcap version.
     */
    public String getMadcapVersion() {
        return madcapVersion;
    }

    /**
     * Sets the current MadcapVersion.
     * @param madcapVersion to be set.
     */
    public void setMadcapVersion(String madcapVersion) {
        this.madcapVersion = madcapVersion;
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
    public int compareTo(SystemInfoEntry o) {
        return 0;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String s) {
        id = s;
    }

    @Override
    public Long getTimestamp() {
        return timestamp;
    }

    @Override
    public void setTimestamp(Long l) {
        timestamp = l;
    }

    @Override
    public String getUserID() {
        return userID;
    }

    @Override
    public void setUserID(String s) {
        userID = s;
    }

    /**
     * Returns a hash code value for the object. This method is
     * supported for the benefit of hash tables such as those provided by
     * {@link HashMap}.
     * <p>
     * The general contract of {@code hashCode} is:
     * <ul>
     * <li>Whenever it is invoked on the same object more than once during
     * an execution of a Java application, the {@code hashCode} method
     * must consistently return the same integer, provided no information
     * used in {@code equals} comparisons on the object is modified.
     * This integer need not remain consistent from one execution of an
     * application to another execution of the same application.
     * <li>If two objects are equal according to the {@code equals(Object)}
     * method, then calling the {@code hashCode} method on each of
     * the two objects must produce the same integer result.
     * <li>It is <em>not</em> required that if two objects are unequal
     * according to the {@link Object#equals(Object)}
     * method, then calling the {@code hashCode} method on each of the
     * two objects must produce distinct integer results.  However, the
     * programmer should be aware that producing distinct integer results
     * for unequal objects may improve the performance of hash tables.
     * </ul>
     * <p>
     * As much as is reasonably practical, the hashCode method defined by
     * class {@code Object} does return distinct integers for distinct
     * objects. (This is typically implemented by converting the internal
     * address of the object into an integer, but this implementation
     * technique is not required by the
     * Java&trade; programming language.)
     *
     * @return a hash code value for this object.
     * @see Object#equals(Object)
     * @see System#identityHashCode
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * <p>
     * The {@code equals} method implements an equivalence relation
     * on non-null object references:
     * <ul>
     * <li>It is <i>reflexive</i>: for any non-null reference value
     * {@code x}, {@code x.equals(x)} should return
     * {@code true}.
     * <li>It is <i>symmetric</i>: for any non-null reference values
     * {@code x} and {@code y}, {@code x.equals(y)}
     * should return {@code true} if and only if
     * {@code y.equals(x)} returns {@code true}.
     * <li>It is <i>transitive</i>: for any non-null reference values
     * {@code x}, {@code y}, and {@code z}, if
     * {@code x.equals(y)} returns {@code true} and
     * {@code y.equals(z)} returns {@code true}, then
     * {@code x.equals(z)} should return {@code true}.
     * <li>It is <i>consistent</i>: for any non-null reference values
     * {@code x} and {@code y}, multiple invocations of
     * {@code x.equals(y)} consistently return {@code true}
     * or consistently return {@code false}, provided no
     * information used in {@code equals} comparisons on the
     * objects is modified.
     * <li>For any non-null reference value {@code x},
     * {@code x.equals(null)} should return {@code false}.
     * </ul>
     * <p>
     * The {@code equals} method for class {@code Object} implements
     * the most discriminating possible equivalence relation on objects;
     * that is, for any non-null reference values {@code x} and
     * {@code y}, this method returns {@code true} if and only
     * if {@code x} and {@code y} refer to the same object
     * ({@code x == y} has the value {@code true}).
     * <p>
     * Note that it is generally necessary to override the {@code hashCode}
     * method whenever this method is overridden, so as to maintain the
     * general contract for the {@code hashCode} method, which states
     * that equal objects must have equal hash codes.
     *
     * @param o the reference object with which to compare.
     * @return {@code true} if this object is the same as the obj
     * argument; {@code false} otherwise.
     * @see #hashCode()
     * @see HashMap
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SystemInfoEntry that = (SystemInfoEntry) o;
        return (manufacturer.equals(that.getManufacturer())
                && model.equals(that.getModel())
                && apiLevel == that.getApiLevel()
                && madcapVersion.equals(that.getMadcapVersion()));
    }

    @Override
    public String toString() {
        return "SystemInfoEntry{"+
                "id=" + id +
                "\"manufacturer\": " + manufacturer +
                ", \"model\": " + model +
                ", \"apiLevel\": " + apiLevel +
                ", \"madcapVersion\": " + madcapVersion +
                '}';
    }
}
