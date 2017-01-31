package org.fraunhofer.cese.madcap.backend.models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import org.json.JSONObject;

import java.util.HashMap;


/**
 * Created by MMueller on 11/7/2016.
 */

@Entity
public class LocationEntry implements Comparable<LocationEntry>, DatastoreEntry {

    @Id
    private String id;
    @Index
    private Long timestamp;
    @Index
    private Double latitude;
    @Index
    private Double longitude;
    private Double altitude;
    private Double accuracy;
    private String extras;
    private Double bearing;
    private Double speed;
    private String origin;
    @Index
    private String userID;

    public LocationEntry(){
    }

    public LocationEntry(ProbeEntry probeEntry){
        id = probeEntry.getId();
        timestamp = probeEntry.getTimestamp();
        userID = probeEntry.getUserID();

        // parsing the data
        JSONObject dataJsonObject = new JSONObject(probeEntry.getSensorData());
        latitude = dataJsonObject.getDouble("latitude");
        longitude = dataJsonObject.getDouble("longitude");
        altitude = dataJsonObject.getDouble("altitude");
        accuracy = dataJsonObject.getDouble("accuracy");
        extras =  dataJsonObject.getString("extras");
        bearing = dataJsonObject.getDouble("bearing");
        speed = dataJsonObject.getDouble("speed");
        origin = dataJsonObject.getString("origin");

//        try{
//            extras =  dataJsonObject.getString("extras");
//        }catch (JSONException j){
//            j.printStackTrace();
//            System.out.println(dataJsonObject.toString());
//        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Double getBearing() {
        return bearing;
    }

    public void setBearing(Double bearing) {
        this.bearing = bearing;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public Double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Double accuracy) {
        this.accuracy = accuracy;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
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
    public int compareTo(LocationEntry o) {
        return 0;
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

        LocationEntry that = (LocationEntry) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null)
            return false;
        if (latitude != null ? !latitude.equals(that.latitude) : that.latitude !=null)
            return false;
        if (longitude != null ? !longitude.equals(that.longitude) : that.longitude !=null)
            return false;
        if (altitude != null ? !altitude.equals(that.altitude) : that.altitude !=null)
            return false;
        if (accuracy != null ? !accuracy.equals(that.accuracy) : that.accuracy !=null)
            return false;
        if (extras != null ? !extras.equals(that.extras) : that.extras !=null)
            return false;
        if (speed != null ? !speed.equals(that.speed) : that.speed !=null)
            return false;
        if (origin != null ? !origin.equals(that.origin) : that.origin !=null)
            return false;
        return (userID != null ? !userID.equals(that.userID) : that.userID !=null);

    }

    @Override
    public String toString() {
        return "ProbeEntry{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", altitude='" + altitude + '\'' +
                ", accuracy='" + accuracy + '\'' +
                ", speed='" + speed + '\'' +
                ", origin='" + origin + '\'' +
                ", extras='" + extras.toString() + '\'' +
                '}';
    }

}
