package org.fraunhofer.cese.madcap.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.awareness.SnapshotApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.Status;

import org.fraunhofer.cese.madcap.MyApplication;
import org.fraunhofer.cese.madcap.R;
import org.fraunhofer.cese.madcap.SignInActivity;
import org.fraunhofer.cese.madcap.WelcomeActivity;
import org.fraunhofer.cese.madcap.authentification.MadcapAuthEventHandler;
import org.fraunhofer.cese.madcap.authentification.MadcapAuthManager;
import org.fraunhofer.cese.madcap.cache.Cache;
import org.fraunhofer.cese.madcap.cache.UploadStatusListener;
import org.fraunhofer.cese.madcap.factories.CacheFactory;
import org.fraunhofer.cese.madcap.issuehandling.GoogleApiClientConnectionIssueManager;
import org.fraunhofer.cese.madcap.issuehandling.MadcapPermissionDeniedHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import edu.umd.fcmd.sensorlisteners.NoSensorFoundException;
import edu.umd.fcmd.sensorlisteners.listener.Listener;
import edu.umd.fcmd.sensorlisteners.listener.applications.ApplicationsListener;
import edu.umd.fcmd.sensorlisteners.listener.applications.TimedApplicationTaskFactory;
import edu.umd.fcmd.sensorlisteners.listener.location.LocationListener;
import edu.umd.fcmd.sensorlisteners.listener.location.LocationServiceStatusReceiverFactory;
import edu.umd.fcmd.sensorlisteners.listener.location.TimedLocationTaskFactory;

/**
 * Created by MMueller on 10/7/2016.
 */

@Singleton
public class DataCollectionService extends Service implements MadcapAuthEventHandler {
    private static final String TAG = "Madcap DataColl Service";
    private final int RUN_CODE = 1;
    private MadcapAuthManager madcapAuthManager = MadcapAuthManager.getInstance();
    private NotificationManager mNotificationManager;

    private final IBinder mBinder = new DataCollectionServiceBinder();
    private List<Listener> listeners = new CopyOnWriteArrayList<>();

    @Inject
    Cache cache;

    @Inject @Named("AwarenessApi")
    GoogleApiClient locationClient;

    @Inject
    SnapshotApi snapshotApi;

    @Inject
    TimedLocationTaskFactory timedLocationTaskFactory;

    @Inject
    LocationServiceStatusReceiverFactory locationServiceStatusReceiverFactory;

    @Inject
    GoogleApiClientConnectionIssueManager googleApiClientConnectionIssueManager;

    @Inject
    MadcapPermissionDeniedHandler madcapPermissionDeniedHandler;

    @Inject
    TimedApplicationTaskFactory timedApplicationTaskFactory;

    @Inject
    Calendar calendar;

    /**
     * Return the communication channel to the service.  May return null if
     * clients can not bind to the service.  The returned
     * {@link IBinder} is usually for a complex interface
     * that has been <a href="{@docRoot}guide/components/aidl.html">described using
     * aidl</a>.
     * <p>
     * <p><em>Note that unlike other application components, calls on to the
     * IBinder interface returned here may not happen on the main_old thread
     * of the process</em>.  More information about the main_old thread can be found in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html">Processes and
     * Threads</a>.</p>
     *
     * @param intent The Intent that was used to bind to this service,
     *               as given to {@link Context#bindService
     *               Context.bindService}.  Note that any extras that were included with
     *               the Intent at that point will <em>not</em> be seen here.
     * @return Return an IBinder through which clients can call on to the
     * service.
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class DataCollectionServiceBinder extends Binder {
        public DataCollectionService getService() {
            // Return this instance of LocalService so clients can call public methods
            return DataCollectionService.this;
        }
    }

    @Override
    public void onCreate(){
        ((MyApplication) getApplication()).getComponent().inject(this);
        MyApplication.madcapLogger.d(TAG, "onCreate Data collection Service");
        madcapAuthManager.setCallbackClass(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disableAllListeners();

        // Any closeout or disconnect operations
        MyApplication.madcapLogger.d(TAG, "onDestroy");
        cache.close();

        hideRunNotification();
    }

    @Override
    public boolean onUnbind(Intent intent){
        return false;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyApplication.madcapLogger.d(TAG, "OnStartCommand");
        showRunNotification();

        synchronized(listeners){
            listeners.add(new LocationListener(this, new CacheFactory(cache, this),
                    locationClient,
                    snapshotApi,
                    timedLocationTaskFactory,
                    locationServiceStatusReceiverFactory,
                    googleApiClientConnectionIssueManager,
                    googleApiClientConnectionIssueManager,
                    madcapPermissionDeniedHandler));

            listeners.add(new ApplicationsListener(this, new CacheFactory(cache, this),
                    timedApplicationTaskFactory, calendar));
        }

        enableAllListeners();

        return START_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Starts all listeners.
     */
    private void enableAllListeners(){
        synchronized(listeners){
            for(Listener l : listeners){
                try{
                    MyApplication.madcapLogger.d(TAG,"numListeners: "+listeners.size());
                    l.startListening();
                    MyApplication.madcapLogger.d(TAG, l.getClass().getSimpleName()+" started listening");
                } catch (NoSensorFoundException nsf) {
                    MyApplication.madcapLogger.e(TAG, "enableAllListeners", nsf);
                }
            }
        }

    }



    /**
     * Stops all listeners.
     */
    private void disableAllListeners(){
        synchronized(listeners){
            for(Listener l : listeners) {
                l.stopListening();
                MyApplication.madcapLogger.d(TAG, l.getClass().getSimpleName() + " stopped listening");
            }
        }
    }

    /**
     * Requests an on-demand upload of cached data.
     *
     * From LL: This responds to a command from the MainActivity.
     * You can move this to the DataCollectionService and have the
     * MainActivity call this when the "Upload Now" button is pressed.
     */
    public int requestUpload() {
        int status = cache.checkUploadConditions(Cache.UploadStrategy.IMMEDIATE);
        if (status == Cache.UPLOAD_READY)
            cache.flush(Cache.UploadStrategy.IMMEDIATE);
        return status;
    }

    /**
     * Attempts to add an upload status listener to the cache.
     *
     * From LL: This method is called from MainActivity to get upload status information. It
     * should be moved to the DataCollectionService and the reference updated in the MainActivity.
     *
     * @param listener the listener to add
     * @see Cache#addUploadListener(UploadStatusListener)
     */
    public void addUploadListener(UploadStatusListener listener) {
        cache.addUploadListener(listener);
    }

    /**
     * Attempts to remove an upload status listener from the cache.
     *
     * From LL: same as addUploadListener()...
     *
     * @param listener the listener to remove
     * @return {@code true} if the listener was removed, {@code false} otherwise.
     */
    public boolean removeUploadListener(UploadStatusListener listener) {
        return cache.removeUploadListener(listener);
    }

    /**
     * Returns the number of entities currently held in the cache.
     *
     * From LL: Used by the MainActivity to get a count of entries to display.
     * Move to DataCollectionService and update the references in the MainActivity.
     *
     * @return the number of entities in the cache.
     * @see Cache#getSize()
     */
    public long getCacheSize() {
        MyApplication.madcapLogger.d(TAG, "Cache size "+cache.getSize());
        return cache.getSize();
    }

    /**
     * From LL: Called by the MainActivity. Move to DataCollectionService and update reference in MainActivity.
     *
     * Should be called when the OS triggers onTrimMemory in the app
     */
    public void onTrimMemory() {
        cache.flush(Cache.UploadStrategy.NORMAL);
    }


    /**
     * Shows the madcap logo in the notification bar,
     * to signal the user that madcap is collecting data.
     */
    private void showRunNotification(){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.ic_stat_madcaplogo);
        mBuilder.setContentTitle("Madcap Running in Background");
        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        mBuilder.setPriority(Notification.PRIORITY_LOW);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, WelcomeActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(WelcomeActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        Notification note =  mBuilder.build();
        note.flags |= Notification.FLAG_NO_CLEAR;

        mNotificationManager.notify(RUN_CODE, note);
    }

    /**
     * Hides the madcap logo in the notification bar.
     */
    private void hideRunNotification(){
        mNotificationManager.cancel(RUN_CODE);
    }

    /**
     * Specifies what the class is expected to do, when the silent login was sucessfull.
     *
     * @param result
     */
    @Override
    public void onSilentLoginSuccessfull(GoogleSignInResult result) {

    }

    /**
     * Specifies what the class is expected to do, when the silent login was not successfull.
     *
     * @param opr
     */
    @Override
    public void onSilentLoginFailed(OptionalPendingResult<GoogleSignInResult> opr) {

    }

    /**
     * Specifies what the class is expected to do, when the regular sign in was successful.
     */
    @Override
    public void onSignInSucessfull() {

    }

    /**
     * Specifies what the app is expected to do when the Signout was sucessfull.
     *
     * @param status
     */
    @Override
    public void onSignOutResults(Status status) {
            MyApplication.madcapLogger.d(TAG, "Sign out callback");
            stopSelf();
    }

    /**
     * Specifies what the class is expected to do, when disconnected.
     *
     * @param status
     */
    @Override
    public void onRevokeAccess(Status status) {

    }

    /**
     * There the sign in intent has to be sent.
     *
     * @param intent
     * @param requestCode
     */
    @Override
    public void onSignInIntent(Intent intent, int requestCode) {

    }
}
