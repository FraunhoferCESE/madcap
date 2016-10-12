package org.fraunhofer.cese.madcap.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import java.util.HashMap;

/**
 * Created by MMueller on 10/7/2016.
 */
public class OnBootService extends Service implements Cloneable{
    private static final String TAG = "Madcap On Boot Service";
    private Context context = this;

    /**
     * Gets called when the Service gets bonund to an Activity.
     * @param intent the starting Intent.
     * @return the answer.
     */
    @Override
    public final IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Gets invoked when the Service gets destroyed.
     */
    @Override
    public final void onDestroy() {
        Log.d(TAG, "onDestroy");
    }

    /**
     * Gets invoked when the Service gets created, right before it gets started.
     */
    @Override
    public final void onCreate(){
        Log.d(TAG, "onCreate Boot Service");
        Intent intent = new Intent(this, LoginService.class);
        startService(intent);
        this.stopSelf();
    }

    /**
     * Setter for the context. For testing purposes only.
     * @deprecated
     * @param context the context to be set.
     */
    @Deprecated
    public final void setContext(Context context) {
        this.context = context;
    }

    /**
     * Clones this and returns it as a OnBootService objcet.
     * @return clone of this.
     * @throws CloneNotSupportedException inherit.
     */
    @Override
    public final OnBootService clone() throws CloneNotSupportedException {
        return (OnBootService)super.clone();
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
     * @param obj the reference object with which to compare.
     * @return {@code true} if this object is the same as the obj
     * argument; {@code false} otherwise.
     * @see #hashCode()
     * @see HashMap
     */
    @Override
    public final boolean equals(Object obj) {
        if(obj.getClass() == OnBootService.class){
            OnBootService ob = (OnBootService)obj;
            return (ob.getContext() == getContext());
        }else{
            return super.equals(obj);
        }
    }

    /**
     * Implements the normal Hash Code
     * @return the hash Code.
     */
    @Override
    public final int hashCode() {
        return context.hashCode();
    }

    /**
     * Getter for the context. For testing purposes only.
     * @return the current context.
     */
    public final Context getContext() {
        return context;
    }
}