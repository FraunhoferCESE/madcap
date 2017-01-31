package org.fraunhofer.cese.madcap.backend.models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.HashMap;

/**
 * Created by MMueller on 12/30/2016.
 */
@Entity
public class UploadLogEntry implements Comparable<UploadLogEntry> {

    @Id
    private Long id;
    @Index
    private Long requestTime;
    private int savedProbes;
    private int duplicates;
    private Long earliestProbeTimeStamp;
    private Long latestProbeTimestamp;
    private Long payloadSize;
    @Index
    private String userID;

    public UploadLogEntry(){
    }

    public UploadLogEntry( String userId, long requestTime, int savedProbes, int duplicates, long earliestProbeTimeStamp, long latestProbeTimestamp, long payloadSize){
        this.requestTime = requestTime;
        userID = userId;
        this.savedProbes = savedProbes;
        this.duplicates = duplicates;
        this.earliestProbeTimeStamp = earliestProbeTimeStamp;
        this.latestProbeTimestamp = latestProbeTimestamp;
        this.payloadSize = payloadSize;
    }

    @Override
    public int compareTo(UploadLogEntry o) {
        return 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long s) {
        id = s;
    }

    public Long getTimestamp() {
        return requestTime;
    }

    public void setTimestamp(Long l) {
        requestTime = l;
    }

    public Long getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Long l) {
        requestTime = l;
    }

    public int getSavedProbes() {
        return savedProbes;
    }

    public void setSavedProbes(int savedProbes) {
        this.savedProbes = savedProbes;
    }

    public int getDuplicates() {
        return duplicates;
    }

    public void setDuplicates(int duplicates) {
        this.duplicates = duplicates;
    }

    public Long getEarliestProbeTimeStamp() {
        return earliestProbeTimeStamp;
    }

    public void setEarliestProbeTimeStamp(Long earliestProbeTimeStamp) {
        this.earliestProbeTimeStamp = earliestProbeTimeStamp;
    }

    public Long getLatestProbeTimestamp() {
        return latestProbeTimestamp;
    }

    public void setLatestProbeTimestamp(Long latestProbeTimestamp) {
        this.latestProbeTimestamp = latestProbeTimestamp;
    }

    public Long getPayloadSize() {
        return payloadSize;
    }

    public void setPayloadSize(Long payloadSize) {
        this.payloadSize = payloadSize;
    }

    public String getUserID() {
        return userID;
    }

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

        UploadLogEntry that = (UploadLogEntry) o;
        return (requestTime.equals(that.getRequestTime()) &&
        savedProbes == that.getSavedProbes() &&
        duplicates == that.getDuplicates() &&
        earliestProbeTimeStamp.equals(that.getEarliestProbeTimeStamp()) &&
        latestProbeTimestamp.equals(that.getLatestProbeTimestamp()) &&
        payloadSize.equals(that.getPayloadSize()) &&
        userID.equals(that.getUserID()));
    }

    @Override
    public String toString() {
        return "UploadLogEntry{"+
                "id=" + id +
                "\"requestTime\": " + requestTime +
                "\"savedProbes\": " + savedProbes +
                "\"duplicates\": " + duplicates +
                "\"earliestProbeTimeStamp\": " + earliestProbeTimeStamp +
                "\"latestProbeTimestamp\": " + latestProbeTimestamp +
                "\"payloadSize\": " + payloadSize +
                "\"userID\": " + userID +
                '}';
    }

}
