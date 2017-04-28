package org.fraunhofer.cese.madcap.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.service.notification.StatusBarNotification;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import org.fraunhofer.cese.madcap.MyApplication;
import org.fraunhofer.cese.madcap.R;
import org.fraunhofer.cese.madcap.WelcomeActivity;
import org.fraunhofer.cese.madcap.authentication.AuthenticationProvider;
import org.fraunhofer.cese.madcap.cache.Cache;
import org.fraunhofer.cese.madcap.cache.CacheFactory;
import org.fraunhofer.cese.madcap.cache.RemoteUploadResult;
import org.fraunhofer.cese.madcap.cache.UploadStatusListener;
import org.fraunhofer.cese.madcap.cache.UploadStrategy;
import org.fraunhofer.cese.madcap.util.ManualProbeUploadTaskFactory;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import edu.umd.fcmd.sensorlisteners.NoSensorFoundException;
import edu.umd.fcmd.sensorlisteners.listener.Listener;
import edu.umd.fcmd.sensorlisteners.listener.activity.ActivityListener;
import edu.umd.fcmd.sensorlisteners.listener.applications.ApplicationsListener;
import edu.umd.fcmd.sensorlisteners.listener.audio.AudioListener;
import edu.umd.fcmd.sensorlisteners.listener.bluetooth.BluetoothListener;
import edu.umd.fcmd.sensorlisteners.listener.location.LocationListener;
import edu.umd.fcmd.sensorlisteners.listener.network.NetworkListener;
import edu.umd.fcmd.sensorlisteners.listener.power.PowerListener;
import edu.umd.fcmd.sensorlisteners.listener.system.SystemListener;
import edu.umd.fcmd.sensorlisteners.model.system.SystemUptimeProbe;
import edu.umd.fcmd.sensorlisteners.model.util.DataCollectionProbe;
import edu.umd.fcmd.sensorlisteners.model.util.LogOutProbe;
import timber.log.Timber;


/**
 * The main service that manages all listeners that collect data. This service is responsible for handling lifecycle events.
 */
@Singleton
public class DataCollectionService extends Service implements UploadStatusListener {
    private static final String TAG = "Madcap DataColl Service";
    private static final int MAX_EXCEPTION_MESSAGE_LENGTH = 20;
    private static final int RUN_CODE = 1;
    private static final int NOTIFICATION_ID = 918273;
    private static final long HEARTBEAT_DELAY = 100l;

    private boolean isRunning;

    private final IBinder mBinder = new DataCollectionServiceBinder();
    private final List<Listener> listeners = new CopyOnWriteArrayList<>();

    @SuppressWarnings({"PackageVisibleField", "WeakerAccess"})
    @Inject
    HeartBeatRunner heartBeatRunner;

    @SuppressWarnings({"PackageVisibleField", "WeakerAccess"})
    @Inject
    Cache cache;

    @SuppressWarnings({"PackageVisibleField", "WeakerAccess"})
    @Inject
    AuthenticationProvider authManager;

    @SuppressWarnings({"PackageVisibleField", "WeakerAccess"})
    @Inject
    ManualProbeUploadTaskFactory manualProbeUploadTaskFactory;

    @SuppressWarnings({"PackageVisibleField", "WeakerAccess"})
    @Inject
    LocationListener locationListener;

    @SuppressWarnings({"PackageVisibleField", "WeakerAccess", "CanBeFinal", "unused"})
    @Inject
    ApplicationsListener applicationsListener;

    @SuppressWarnings({"PackageVisibleField", "WeakerAccess", "CanBeFinal", "unused"})
    @Inject
    BluetoothListener bluetoothListener;

    @SuppressWarnings({"PackageVisibleField", "WeakerAccess", "CanBeFinal", "unused"})
    @Inject
    ActivityListener activityListener;

    @SuppressWarnings({"PackageVisibleField", "WeakerAccess", "CanBeFinal", "unused"})
    @Inject
    PowerListener powerListener;

    @SuppressWarnings({"PackageVisibleField", "WeakerAccess", "CanBeFinal", "unused"})
    @Inject
    NetworkListener networkListener;

    @SuppressWarnings({"PackageVisibleField", "WeakerAccess", "CanBeFinal", "unused"})
    @Inject
    SystemListener systemListener;

    @SuppressWarnings({"PackageVisibleField", "WeakerAccess", "CanBeFinal", "unused"})
    @Inject
    AudioListener auidioListener;


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

    @SuppressWarnings("PublicInnerClass")
    public class DataCollectionServiceBinder extends Binder {
        public DataCollectionService getService() {
            // Return this instance of LocalService so clients can call public methods
            return DataCollectionService.this;
        }
    }

    @SuppressWarnings({"unchecked", "NonPrivateFieldAccessedInSynchronizedContext"})
    @Override
    public void onCreate() {
        //noinspection CastToConcreteClass
        ((MyApplication) getApplication()).getComponent().inject(this);
        Timber.d("onCreate Data collection Service " + this);

        listeners.clear();

        synchronized (listeners) {
            //dangerous listeners
            listeners.add(locationListener);
            listeners.add(applicationsListener);
            listeners.add(activityListener);
            listeners.add(networkListener);
            //non dangerous listeners
            listeners.add(systemListener);
            listeners.add(bluetoothListener);
            listeners.add(powerListener);
            listeners.add(auidioListener);
        }

    }

    private void sendDataCollectionProbe(String dataCollectionState) {
        DataCollectionProbe probe = new DataCollectionProbe(dataCollectionState);
        probe.setDate(System.currentTimeMillis());
        manualProbeUploadTaskFactory.create().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, probe);
    }

    public void sendLogOutProbe() {
        LogOutProbe probe = new LogOutProbe();
        probe.setDate(System.currentTimeMillis());
        manualProbeUploadTaskFactory.create().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, probe);
    }

    @Override
    public void onDestroy() {
        Timber.d("onDestroy");
        super.onDestroy();

        sendDataCollectionProbe(DataCollectionProbe.OFF);

        // Stop the heartbeat
        if (heartBeatRunner != null) {
            heartBeatRunner.stop();
        }

        synchronized (listeners) {
            for (Listener listener : listeners) {
                listener.stopListening();
                Timber.d(listener.getClass().getSimpleName() + " stopped listening");
            }
            listeners.clear();
        }

        cache.removeUploadListener(this);

        // Any closeout or disconnect operations
        // This is a very bad kludge to handle the case where the user is signed out and all data should be uploaded immediately.
        // This decision should not belong here...
        if (authManager.getUser() == null) {
            cache.close(UploadStrategy.IMMEDIATE);
        } else {
            cache.close(UploadStrategy.NORMAL);
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Timber.d("onStartCommand. isRunning: " + isRunning);

        if (!isRunning) {
            sendDataCollectionProbe(DataCollectionProbe.ON);

            startForeground(NOTIFICATION_ID, getRunNotification());
            Timber.d("numListeners: " + listeners.size());
            synchronized (listeners) {
                for (Listener listener : listeners) {
                    try {
                        Timber.d("Starting " + listener.getClass().getSimpleName());
                        listener.startListening();
                    } catch (NoSensorFoundException nsf) {
                        Timber.e(nsf);
                    }

                }
            }

            cache.addUploadListener(this);

            if ((intent != null) && intent.hasExtra("boot")) {
                cacheInitialBootEvent();
            }



            // Start the heartbeat
            new Handler().postDelayed(heartBeatRunner, HEARTBEAT_DELAY);
            isRunning = true;
        }
        return START_STICKY;
    }

    /**
     * Caches a boot probe when the service has been started via the OnBootService.
     */
    private void cacheInitialBootEvent() {
        SystemUptimeProbe systemUptimeProbe = new SystemUptimeProbe();
        systemUptimeProbe.setDate(System.currentTimeMillis());
        systemUptimeProbe.setState(SystemUptimeProbe.BOOT);

        new CacheFactory(cache, authManager).save(systemUptimeProbe);
    }


    /**
     * Requests an on-demand upload of cached data.
     * <p>
     * From LL: This responds to a command from the MainActivity.
     * You can move this to the DataCollectionService and have the
     * MainActivity call this when the "Upload Now" button is pressed.
     */
    public int requestUpload() {
        Timber.d("Upload requested");
        int status = cache.checkUploadConditions(UploadStrategy.IMMEDIATE);

        Timber.d("Status: " + status);

        //TODO: All of the message formatting should be in the view, not here.
        String text = "";
        if (status == Cache.UPLOAD_READY) {
            cache.flush(UploadStrategy.IMMEDIATE);
            text += "Upload started...";
        } else if (status == Cache.UPLOAD_ALREADY_IN_PROGRESS) {
            text += "Upload in progress...";
        } else {
            String errorText = "";
            if ((status & Cache.INTERNAL_ERROR) == Cache.INTERNAL_ERROR) {
                //noinspection AccessOfSystemProperties
                errorText += System.getProperty("line.separator") + "- An internal error occurred and data could not be uploaded.";
            }
            if ((status & Cache.UPLOAD_INTERVAL_NOT_MET) == Cache.UPLOAD_INTERVAL_NOT_MET) {
                //noinspection AccessOfSystemProperties
                errorText += System.getProperty("line.separator") + "- An upload was just requested; please wait a few seconds.";
            }
            if ((status & Cache.NO_INTERNET_CONNECTION) == Cache.NO_INTERNET_CONNECTION) {
                //noinspection AccessOfSystemProperties
                errorText += System.getProperty("line.separator") + "- No WiFi connection detected.";
            }
            if ((status & Cache.DATABASE_LIMIT_NOT_MET) == Cache.DATABASE_LIMIT_NOT_MET) {
                //noinspection AccessOfSystemProperties
                errorText += System.getProperty("line.separator") + "- No entries to upload";
            }

            text += errorText.isEmpty() ? "No status to report. Please wait." : ("Error:" + errorText);
        }

        String date = String.valueOf(new Date());
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(getString(R.string.pref_lastUploadDate), date);
        editor.putString(getString(R.string.pref_lastUploadMessage), text);
        editor.putString(getString(R.string.pref_lastUploadStatus), getString(R.string.lastUploadStatus_incomplete));
        editor.putInt(getString(R.string.pref_uploadProgress), 0);
        editor.apply();

        return status;
    }

    /**
     * Called when a remote upload attempt has finished.
     *
     * @param result the remote upload result, which can be {@code null} in certain rare cases of an internal error.
     */
    @Override
    public void uploadFinished(RemoteUploadResult result) {

        boolean hasError = false;

        //TODO: All of the message formatting should be in the view, not here.
        String text = "";
        if (result == null) {
            text += "No upload due to an internal error.";
            hasError = true;
        } else if (!result.isUploadAttempted()) {
            text += "No entries to upload.";
        } else if (result.getException() != null) {
            hasError = true;
            String exceptionMessage;
            if (result.getException().getMessage() != null) {
                exceptionMessage = result.getException().getMessage();
            } else if (result.getException().toString() != null) {
                exceptionMessage = result.getException().toString();
            } else {
                exceptionMessage = "Unspecified error";
            }

            text += "Upload failed due to " + (exceptionMessage.length() > MAX_EXCEPTION_MESSAGE_LENGTH ? exceptionMessage.substring(0, MAX_EXCEPTION_MESSAGE_LENGTH - 1) : exceptionMessage);
        } else if (result.getSaveResult() == null) {
            text += "An error occurred on the remote server.";
            hasError = true;
        } else {
            //noinspection AccessOfSystemProperties
            text += ((result.getSaveResult().getSaved() == null) ? 0 : result.getSaveResult().getSaved().size()) + " entries saved.";
            if (result.getSaveResult().getAlreadyExists() != null) {
                //noinspection AccessOfSystemProperties,StringConcatenationMissingWhitespace
                text += System.getProperty("line.separator") + result.getSaveResult().getAlreadyExists().size() + " duplicate entries ignored.";
            }
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        if (hasError) {
            editor.putString(getString(R.string.pref_lastUploadStatus), getString(R.string.lastUploadStatus_error));
        }
        editor.putString(getString(R.string.pref_lastUploadStatus), getString(R.string.lastUploadStatus_complete));
        editor.putString(getString(R.string.pref_lastUploadMessage), text);
        editor.putInt(getString(R.string.pref_uploadProgress), 100);
        editor.apply();

        Timber.d("Upload result received");

    }

    /**
     * Called when the the cache is being closed. The listener is automatically unregistered from the cache immediately after this call.
     */
    @Override
    public void cacheClosing() {
        Timber.d("Cache is closing");
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
        editor.putInt(getString(R.string.pref_uploadProgress), value);
        editor.apply();
    }

    private Notification getRunNotification() {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.ic_stat_madcaplogo);
        // TODO: Refactor
        mBuilder.setContentTitle("MADCAP is collecting research data");
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

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        Notification note = mBuilder.build();
        note.flags |= Notification.FLAG_NO_CLEAR;

        return note;
    }
}
