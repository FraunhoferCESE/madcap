package org.fraunhofer.cese.madcap.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import org.fraunhofer.cese.madcap.MainActivity;
import org.fraunhofer.cese.madcap.MyApplication;
import org.fraunhofer.cese.madcap.R;
import org.fraunhofer.cese.madcap.WelcomeActivity;
import org.fraunhofer.cese.madcap.authentication.AuthenticationProvider;
import org.fraunhofer.cese.madcap.cache.Cache;
import org.fraunhofer.cese.madcap.cache.CacheFactory;
import org.fraunhofer.cese.madcap.cache.RemoteUploadResult;
import org.fraunhofer.cese.madcap.cache.UploadProgressEvent;
import org.fraunhofer.cese.madcap.cache.UploadStrategy;
import org.fraunhofer.cese.madcap.issuehandling.MadcapPermissionsManager;
import org.fraunhofer.cese.madcap.util.ManualProbeUploadTaskFactory;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import edu.umd.fcmd.sensorlisteners.listener.Listener;
import edu.umd.fcmd.sensorlisteners.listener.activity.ActivityListener;
import edu.umd.fcmd.sensorlisteners.listener.applications.ApplicationsListener;
import edu.umd.fcmd.sensorlisteners.listener.audio.AudioListener;
import edu.umd.fcmd.sensorlisteners.listener.bluetooth.BluetoothListener;
import edu.umd.fcmd.sensorlisteners.listener.location.LocationListener;
import edu.umd.fcmd.sensorlisteners.listener.network.ConnectivityListener;
import edu.umd.fcmd.sensorlisteners.listener.network.NFCListener;
import edu.umd.fcmd.sensorlisteners.listener.network.SMSListener;
import edu.umd.fcmd.sensorlisteners.listener.network.TelephonyListener;
import edu.umd.fcmd.sensorlisteners.listener.network.WifiListener;
import edu.umd.fcmd.sensorlisteners.listener.power.PowerListener;
import edu.umd.fcmd.sensorlisteners.listener.system.SystemListener;
import edu.umd.fcmd.sensorlisteners.model.system.SystemUptimeProbe;
import edu.umd.fcmd.sensorlisteners.model.util.DataCollectionProbe;
import timber.log.Timber;


/**
 * The main service that manages all listeners that collect data. This service is responsible for handling lifecycle events.
 */
@SuppressWarnings("PackageVisibleField")
@Singleton
public class DataCollectionService extends Service {
    private static final String TAG = "Madcap DataColl Service";
    private static final int MAX_EXCEPTION_MESSAGE_LENGTH = 20;
    private static final int RUN_CODE = 1;
    private static final int FOREGROUND_NOTIFICATION_ID = 918273;
    private static String NOTIFICATION_CHANNEL_ID = "org.fraunhofer.cese.madcap.notificationChannelID";
    private static String NOTIFICATION_CHANNEL_NAME = "org.fraunhofer.cese.madcap.notificationChannelName";
    private static final int CAPACITY_NOTIFICATION_ID = 126731245;
    private static final long HEARTBEAT_DELAY = 100L;

    private boolean isRunning;

    private final IBinder mBinder = new DataCollectionServiceBinder();
    private final List<Listener> listeners = new CopyOnWriteArrayList<>();

    @Inject NotificationManager mNotificationManager;


    @Inject
    HeartBeatRunner heartBeatRunner;

    @Inject
    @Named("HeartbeatHandler")
    Handler heartbeatHandler;

    @Inject
    @Named("RemoteConfigUpdateHandler")
    Handler remoteConfigUpdateHandler;


    @Inject Cache cache;
    @Inject CacheFactory cacheFactory;

    @Inject AuthenticationProvider authManager;

    @Inject ManualProbeUploadTaskFactory manualProbeUploadTaskFactory;

    @Inject LocationListener locationListener;
    @Inject ApplicationsListener applicationsListener;
    @Inject BluetoothListener bluetoothListener;
    @Inject ActivityListener activityListener;
    @Inject PowerListener powerListener;
    @Inject WifiListener wifiListener;
    @Inject SystemListener systemListener;
    @Inject AudioListener auidioListener;
    @Inject TelephonyListener telephonyListener;
    @Inject SMSListener smsListener;
    @Inject ConnectivityListener connectivityListener;
    @Inject NFCListener nfcListener;

    @Inject FirebaseRemoteConfig firebaseRemoteConfig;

    @Inject SharedPreferences prefs;

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
        EventBus.getDefault().register(this);

        listeners.clear();

        synchronized (listeners) {
            //dangerous listeners
            listeners.add(locationListener);
            listeners.add(applicationsListener);
            listeners.add(wifiListener);
            listeners.add(telephonyListener);
            listeners.add(smsListener);
            //non dangerous listeners
            listeners.add(nfcListener);
            listeners.add(connectivityListener);
            listeners.add(activityListener);
            listeners.add(systemListener);
            listeners.add(bluetoothListener);
            listeners.add(powerListener);
            listeners.add(auidioListener);
        }

        Timber.d("Starting remoteConfigUpdateHandler");
        remoteConfigUpdateHandler.post(new Runnable() {
            private final Runnable runnable = this;
            private static final long FIREBASE_CACHE_EXPIRATION = 3600L;
            private static final long FIREBASE_CONFIG_UPDATE_INTERVAL = 3600000L;

            @Override
            public void run() {
                // Periodically check to see if there is a configuration updated pushed through firebase.

                firebaseRemoteConfig.fetch(FIREBASE_CACHE_EXPIRATION).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            Timber.d("Fetch of remote FirebaseConfig successful.");
                            firebaseRemoteConfig.activateFetched();
                        } else {
                            Timber.w("Fetching Firebase config was not successful");
                            Timber.w(task.getException());
                        }
                        remoteConfigUpdateHandler.postDelayed(runnable, FIREBASE_CONFIG_UPDATE_INTERVAL);
                    }
                });
            }
        });

    }

    private void sendDataCollectionProbe(String dataCollectionState) {
        DataCollectionProbe probe = new DataCollectionProbe(dataCollectionState);
        probe.setDate(System.currentTimeMillis());
        manualProbeUploadTaskFactory.create().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, probe);
    }

    @Override
    public void onDestroy() {
        Timber.d("onDestroy");
        super.onDestroy();

        EventBus.getDefault().unregister(this);

        prefs.edit().putLong(getString(R.string.pref_dataCount), cache.getSize()).apply();

        sendDataCollectionProbe(DataCollectionProbe.OFF);

        heartbeatHandler.removeCallbacksAndMessages(null);
        remoteConfigUpdateHandler.removeCallbacksAndMessages(null);

        synchronized (listeners) {
            for (Listener listener : listeners) {
                listener.stopListening();
            }
            listeners.clear();
        }

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
            startForeground(FOREGROUND_NOTIFICATION_ID, getRunNotification());
            startBackgroundServicesAndProbes();

            if ((intent != null) && intent.hasExtra("boot")) {
                SystemUptimeProbe systemUptimeProbe = new SystemUptimeProbe();
                systemUptimeProbe.setDate(System.currentTimeMillis());
                systemUptimeProbe.setState(SystemUptimeProbe.BOOT);
                cacheFactory.save(systemUptimeProbe);
            }

            // Start the heartbeat
            Timber.d("Starting heartbeatHandler");
            heartbeatHandler.postDelayed(heartBeatRunner, HEARTBEAT_DELAY);
            isRunning = true;

        }
        return START_STICKY;
    }

    private void startBackgroundServicesAndProbes() {
        Timber.d("numListeners: " + listeners.size());
        synchronized (listeners) {
            for (Listener listener : listeners) {
                Timber.d("Starting " + listener.getClass().getSimpleName());
                listener.startListening();

            }
        }
    }

    /**
     * Called from PermissionsActivity when a dangerous permission is granted.
     */
    @Subscribe
    public void onPermissionGrantedEvent(MadcapPermissionsManager.PermissionGrantedEvent event) {
        Timber.d("PermissionGranted event received: " + event);

        switch (event) {
            case LOCATION:
                locationListener.startListening();
                wifiListener.startListening();
                telephonyListener.startListening();
                break;
            case TELEPHONE:
                telephonyListener.startListening();
                break;
            case SMS:
                smsListener.startListening();
                break;
            case USAGE:
                applicationsListener.startListening();
                break;
        }


    }

    /**
     * Requests an on-demand upload of cached data.
     * <p>
     * From LL: This responds to a command from the MainActivity.
     * You can move this to the DataCollectionService and have the
     * MainActivity call this when the "Upload Now" button is pressed.
     */
    public void requestUpload() {
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

        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(getString(R.string.pref_lastUploadDate), new Date().getTime());
        editor.putString(getString(R.string.pref_lastUploadMessage), text);
        editor.putString(getString(R.string.pref_lastUploadStatus), getString(R.string.lastUploadStatus_incomplete));
        editor.putInt(getString(R.string.pref_uploadProgress), 0);
        editor.apply();
    }

    /**
     * Triggered when a remote upload attempt has finished.
     *
     * @param result the remote upload result, which can be {@code null} in certain rare cases of an internal error.
     */
    @Subscribe
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
            mNotificationManager.cancel(CAPACITY_NOTIFICATION_ID);
        }

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
     * Provides the percentage of upload that is completed thus far.
     *
     * @param event The percentage of the uploaded completed thus far
     */
    @Subscribe
    public void progressUpdate(UploadProgressEvent event) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(getString(R.string.pref_uploadProgress), event.getValue());
        editor.apply();
    }

    private Notification getRunNotification() {
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

        // NotificationCompat builder is deprecated on SDK 27 and above (Oreo)
        // Use Notification.Builder for SDK 27 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_MIN);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager noteMan = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert noteMan != null;
            noteMan.createNotificationChannel(chan);

            Notification.Builder noteBuilder = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setOngoing(true)
                    .setContentTitle("MADCAP is collecting research data")
                    .setSmallIcon(R.drawable.ic_stat_madcaplogo)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setContentIntent(resultPendingIntent);

            Notification note = noteBuilder.build();
            note.flags |= Notification.FLAG_NO_CLEAR;
            return note;

        }
        else {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
            mBuilder.setSmallIcon(R.drawable.ic_stat_madcaplogo);
            // TODO: Refactor
            mBuilder.setContentTitle("MADCAP is collecting research data");
            mBuilder.setDefaults(Notification.DEFAULT_ALL);
            mBuilder.setPriority(Notification.PRIORITY_LOW);
            mBuilder.setContentIntent(resultPendingIntent);


            // mId allows you to update the notification later on.
            Notification note = mBuilder.build();
            note.flags |= Notification.FLAG_NO_CLEAR;
            return note;
        }
    }

    @Subscribe
    public void onCacheCountUpdate(Cache.CacheCountUpdate update) {
        if (update == null) {
            return;
        }

        //noinspection MagicNumber
        double percentage = ((double) update.getCount() * 100.0d) / (double) firebaseRemoteConfig.getLong(getString(R.string.DB_FORCED_CLEANUP_LIMIT_KEY));
        double limit = firebaseRemoteConfig.getDouble(getString(R.string.CLEANUP_WARNING_LIMIT_KEY));

        if (percentage >= limit) {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setAutoCancel(true)
                            .setSmallIcon(R.drawable.ic_stat_madcaplogo)
                            .setContentTitle(getString(R.string.capacity_warning_title))
                            .setContentText(String.format(getString(R.string.capacity_warning), percentage))
                            .setPriority(Notification.PRIORITY_HIGH);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(new Intent(this, MainActivity.class));
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            mNotificationManager.notify(CAPACITY_NOTIFICATION_ID, mBuilder.build());
        } else {
            mNotificationManager.cancel(CAPACITY_NOTIFICATION_ID);
        }
    }

    public long getCount() {
        return cache.getSize();
    }
}
