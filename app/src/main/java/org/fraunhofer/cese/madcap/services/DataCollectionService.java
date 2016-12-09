package org.fraunhofer.cese.madcap.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.awareness.FenceApi;
import com.google.android.gms.awareness.SnapshotApi;
import com.google.android.gms.common.api.GoogleApiClient;

import org.fraunhofer.cese.madcap.MyApplication;
import org.fraunhofer.cese.madcap.R;
import org.fraunhofer.cese.madcap.WelcomeActivity;
import org.fraunhofer.cese.madcap.authentication.AuthenticationProvider;
import org.fraunhofer.cese.madcap.cache.Cache;
import org.fraunhofer.cese.madcap.cache.RemoteUploadResult;
import org.fraunhofer.cese.madcap.cache.UploadStatusGuiListener;
import org.fraunhofer.cese.madcap.cache.UploadStatusListener;
import org.fraunhofer.cese.madcap.cache.UploadStrategy;
import org.fraunhofer.cese.madcap.factories.CacheFactory;
import org.fraunhofer.cese.madcap.issuehandling.GoogleApiClientConnectionIssueManagerLocation;
import org.fraunhofer.cese.madcap.issuehandling.MadcapPermissionDeniedHandler;
import org.fraunhofer.cese.madcap.issuehandling.MadcapSensorNoAnswerReceivedHandler;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import edu.umd.fcmd.sensorlisteners.NoSensorFoundException;
import edu.umd.fcmd.sensorlisteners.listener.IntentFilterFactory;
import edu.umd.fcmd.sensorlisteners.listener.Listener;
import edu.umd.fcmd.sensorlisteners.listener.activity.ActivityFenceReceiverFactory;
import edu.umd.fcmd.sensorlisteners.listener.activity.ActivityListener;
import edu.umd.fcmd.sensorlisteners.listener.applications.ApplicationsListener;
import edu.umd.fcmd.sensorlisteners.listener.applications.TimedApplicationTaskFactory;
import edu.umd.fcmd.sensorlisteners.listener.bluetooth.BluetoothInformationReceiverFactory;
import edu.umd.fcmd.sensorlisteners.listener.bluetooth.BluetoothListener;
import edu.umd.fcmd.sensorlisteners.listener.location.LocationListener;
import edu.umd.fcmd.sensorlisteners.listener.location.LocationServiceStatusReceiverFactory;
import edu.umd.fcmd.sensorlisteners.listener.location.TimedLocationTaskFactory;

import static org.fraunhofer.cese.madcap.cache.UploadStatusGuiListener.Completeness.COMPLETE;
import static org.fraunhofer.cese.madcap.cache.UploadStatusGuiListener.Completeness.INCOMPLETE;

/**
 * Created by MMueller on 10/7/2016.
 */

@Singleton
public class DataCollectionService extends Service implements UploadStatusListener {
    private static final String TAG = "Madcap DataColl Service";
    private static final int MAX_EXCEPTION_MESSAGE_LENGTH = 20;
    private final int RUN_CODE = 1;

    private NotificationManager mNotificationManager;

    private final IBinder mBinder = new DataCollectionServiceBinder();
    private List<Listener> listeners = new CopyOnWriteArrayList<>();
    private UploadStatusGuiListener uploadStatusGuiListener;

    @Inject
    Cache cache;

    @Inject
    AuthenticationProvider authManager;

    @Inject
    @Named("AwarenessApi")
    GoogleApiClient locationClient;

    @Inject
    @Named("AwarenessApi")
    GoogleApiClient activityClient;

    @Inject
    SnapshotApi snapshotApi;

    @Inject
    FenceApi fenceApi;

    @Inject
    TimedLocationTaskFactory timedLocationTaskFactory;

    @Inject
    LocationServiceStatusReceiverFactory locationServiceStatusReceiverFactory;

    @Inject
    ActivityFenceReceiverFactory activityFenceReceiverFactory;

    @Inject
    GoogleApiClientConnectionIssueManagerLocation googleApiClientConnectionIssueManagerLocation;

    @Inject
    MadcapPermissionDeniedHandler madcapPermissionDeniedHandler;

    @Inject
    MadcapSensorNoAnswerReceivedHandler madcapSensorNoAnswerReceivedHandler;

    @Inject
    TimedApplicationTaskFactory timedApplicationTaskFactory;

    @Inject
    Calendar calendar;

    @Inject
    BluetoothAdapter bluetoothAdapter;

    @Inject
    BluetoothInformationReceiverFactory bluetoothInformationReceiverFactory;

    @Inject
    IntentFilterFactory intentFilterFactory;

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
    public void onCreate() {
        //noinspection CastToConcreteClass
        ((MyApplication) getApplication()).getComponent().inject(this);
        MyApplication.madcapLogger.d(TAG, "onCreate Data collection Service");

        listeners.clear();

        synchronized (listeners) {
            listeners.add(new LocationListener(this, new CacheFactory(cache, authManager),
                    locationClient,
                    snapshotApi,
                    timedLocationTaskFactory,
                    locationServiceStatusReceiverFactory,
                    googleApiClientConnectionIssueManagerLocation,
                    googleApiClientConnectionIssueManagerLocation,
                    madcapPermissionDeniedHandler,
                    madcapSensorNoAnswerReceivedHandler));


            listeners.add(new ApplicationsListener(this, new CacheFactory(cache, authManager),
                    timedApplicationTaskFactory, madcapPermissionDeniedHandler));

            listeners.add(new BluetoothListener(this, new CacheFactory(cache, authManager),
                    bluetoothAdapter,
                    madcapPermissionDeniedHandler,
                    bluetoothInformationReceiverFactory,
                    intentFilterFactory));

            listeners.add(new ActivityListener(this, new CacheFactory(cache, authManager),
                    activityClient,
                    fenceApi,
                    snapshotApi,
                    activityFenceReceiverFactory,
                    madcapPermissionDeniedHandler));
        }
//        madcapAuthManager.setCallbackClass(this);

    }

    @Override
    public void onDestroy() {
        MyApplication.madcapLogger.d(TAG, "onDestroy");
        super.onDestroy();

        synchronized (listeners) {
            for (Listener l : listeners) {
                l.stopListening();
                MyApplication.madcapLogger.d(TAG, l.getClass().getSimpleName() + " stopped listening");
            }
            listeners.clear();
        }

        cache.removeUploadListener(this);

        // Any closeout or disconnect operations

        cache.close();

        //hideRunNotification();

        //MyApplication.dataCollectionRunning = false;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyApplication.madcapLogger.d(TAG, "OnStartCommand. Intent callee: " + (intent == null ? "null" : intent.getStringExtra("callee")));

        startForeground(1337, getRunNotification());

        synchronized (listeners) {
            for (Listener l : listeners) {
                try {
                    MyApplication.madcapLogger.d(TAG, "numListeners: " + listeners.size());
                    l.startListening();
                    MyApplication.madcapLogger.d(TAG, l.getClass().getSimpleName() + " started listening");
                } catch (NoSensorFoundException nsf) {
                    MyApplication.madcapLogger.e(TAG, "enableAllListeners", nsf);
                }
            }
        }

        cache.addUploadListener(this);

        return START_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }


    /**
     * Requests an on-demand upload of cached data.
     * <p>
     * From LL: This responds to a command from the MainActivity.
     * You can move this to the DataCollectionService and have the
     * MainActivity call this when the "Upload Now" button is pressed.
     */
    public int requestUpload() {
        MyApplication.madcapLogger.d(TAG, "Upload requested");
        int status = cache.checkUploadConditions(UploadStrategy.IMMEDIATE);

        String text = "Result: ";
        MyApplication.madcapLogger.d(TAG, "Status: " + status);

        if (status == Cache.UPLOAD_READY) {
            cache.flush(UploadStrategy.IMMEDIATE);
            text += "Upload started...";
        } else if (status == Cache.UPLOAD_ALREADY_IN_PROGRESS) {
            text += "Upload in progress...";
        } else {
            String errorText = "";
            if ((status & Cache.INTERNAL_ERROR) == Cache.INTERNAL_ERROR)
                errorText += "\n- An internal error occurred and data could not be uploaded.";
            if ((status & Cache.UPLOAD_INTERVAL_NOT_MET) == Cache.UPLOAD_INTERVAL_NOT_MET)
                errorText += "\n- An upload was just requested; please wait a few seconds.";
            if ((status & Cache.NO_INTERNET_CONNECTION) == Cache.NO_INTERNET_CONNECTION)
                errorText += "\n- No WiFi connection detected.";
            if ((status & Cache.DATABASE_LIMIT_NOT_MET) == Cache.DATABASE_LIMIT_NOT_MET)
                errorText += "\n- No entries to upload";

            text += !errorText.isEmpty() ? "Error:" + errorText : "No status to report. Please wait.";
        }

        String date = new Date() + "";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(getString(R.string.last_upload_date), date);
        editor.commit();
        uploadStatusGuiListener.onUploadStatusDateUpdate(date);

        editor.putString(getString(R.string.last_upload_result), text);
        editor.commit();
        uploadStatusGuiListener.onUploadStatusResultUpdate(text);

        uploadStatusGuiListener.onUploadStatusProgressUpdate(0);
        uploadStatusGuiListener.onUploadStatusCompletenessUpdate(INCOMPLETE);

        return status;
    }

    /**
     * Returns the number of entities currently held in the cache.
     * <p>
     * From LL: Used by the MainActivity to get a count of entries to display.
     * Move to DataCollectionService and update the references in the MainActivity.
     *
     * @return the number of entities in the cache.
     * @see Cache#getSize()
     */
    public long getCacheSize() {
        //MyApplication.madcapLogger.d(TAG, "Cache size "+cache.getSize());
        return cache.getSize();
    }

    /**
     * From LL: Called by the MainActivity. Move to DataCollectionService and update reference in MainActivity.
     * <p>
     * Should be called when the OS triggers onTrimMemory in the app
     */
    public void onTrimMemory() {
        cache.flush(UploadStrategy.NORMAL);
    }

    /**
     * Called when a remote upload attempt has finished.
     *
     * @param result the remote upload result, which can be {@code null} in certain rare cases of an internal error.
     */
    @Override
    public void uploadFinished(RemoteUploadResult result) {

        String text = "";
        if (result == null) {
            text += "Result: No upload due to an internal error.";
        } else if (!result.isUploadAttempted()) {
            text += "Result: No entries to upload.";
        } else if (result.getException() != null) {
            String exceptionMessage;
            if (result.getException().getMessage() != null)
                exceptionMessage = result.getException().getMessage();
            else if (result.getException().toString() != null)
                exceptionMessage = result.getException().toString();
            else
                exceptionMessage = "Unspecified error";

            text += "Result: Upload failed due to " + (exceptionMessage.length() > MAX_EXCEPTION_MESSAGE_LENGTH ? exceptionMessage.substring(0, MAX_EXCEPTION_MESSAGE_LENGTH - 1) : exceptionMessage);
        } else if (result.getSaveResult() == null) {
            text += "Result: An error occurred on the remote server.";
        } else {
            text += "Result:\n";
            text += "\t" + (result.getSaveResult().getSaved() == null ? 0 : result.getSaveResult().getSaved().size()) + " entries saved.";
            if (result.getSaveResult().getAlreadyExists() != null)
                text += "\n\t" + result.getSaveResult().getAlreadyExists().size() + " duplicate entries ignored.";
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(getString(R.string.last_upload_completeness), getString(R.string.status_complete));
        editor.commit();
        editor.putString(getString(R.string.last_upload_result), text);
        editor.commit();
        editor.putInt(getString(R.string.last_upload_progress), 100);
        editor.commit();

        if (uploadStatusGuiListener != null) {
            uploadStatusGuiListener.onUploadStatusCompletenessUpdate(COMPLETE);
            uploadStatusGuiListener.onUploadStatusResultUpdate(text);
            uploadStatusGuiListener.onUploadStatusProgressUpdate(100);
        } else {
            MyApplication.madcapLogger.d(TAG, "No UploadStatusGuiListener registered");
        }
        MyApplication.madcapLogger.d(TAG, "Upload result received");

    }

    /**
     * Called when the the cache is being closed. The listener is automatically unregistered from the cache immediately after this call.
     */
    @Override
    public void cacheClosing() {
        MyApplication.madcapLogger.d(TAG, "Cache is closing");
    }

    /**
     * Provides the percentage of upload that is completed thus far.
     *
     * @param value The percentage of the uploaded completed thus far
     */
    @Override
    public void progressUpdate(int value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(getString(R.string.last_upload_progress), value);
        editor.commit();

        if (uploadStatusGuiListener != null) {
            uploadStatusGuiListener.onUploadStatusProgressUpdate(value);
            if (value < 100) {
                uploadStatusGuiListener.onUploadStatusCompletenessUpdate(INCOMPLETE);
            }
        } else {
            MyApplication.madcapLogger.d(TAG, "No UploadStatusGuiListener registered");
        }

    }

    public void setUploadStatusGuiListener(UploadStatusGuiListener uploadStatusGuiListener) {
        this.uploadStatusGuiListener = uploadStatusGuiListener;
    }

    /**
     * Shows the madcap logo in the notification bar,
     * to signal the user that madcap is collecting data.
     */
    private void showRunNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.ic_stat_madcaplogo);
        mBuilder.setContentTitle("MADCAP is running in the background.");
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
        Notification note = mBuilder.build();
        note.flags |= Notification.FLAG_NO_CLEAR;

        mNotificationManager.notify(RUN_CODE, note);
    }

    private Notification getRunNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.ic_stat_madcaplogo);
        mBuilder.setContentTitle("MADCAP is running in the background.");
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
        Notification note = mBuilder.build();
        note.flags |= Notification.FLAG_NO_CLEAR;

        return note;
    }

    /**
     * Hides the madcap logo in the notification bar.
     */
    private void hideRunNotification() {
        mNotificationManager.cancel(RUN_CODE);
    }


}
