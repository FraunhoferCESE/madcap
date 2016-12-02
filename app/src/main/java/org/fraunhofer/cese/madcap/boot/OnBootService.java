package org.fraunhofer.cese.madcap.boot;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;

import org.fraunhofer.cese.madcap.MyApplication;
import org.fraunhofer.cese.madcap.R;
import org.fraunhofer.cese.madcap.SignInActivity;
import org.fraunhofer.cese.madcap.authentication.LoginResultCallback;
import org.fraunhofer.cese.madcap.authentication.MadcapAuthManager;
import org.fraunhofer.cese.madcap.services.DataCollectionService;

import javax.inject.Inject;

import static com.pathsense.locationengine.lib.detectionLogic.b.i;

/**
 * Service to attempt automatic login and start of MADCAP data collection on boot.
 * <p>
 * Created by llayman on 11/22/2016.
 */

public class OnBootService extends IntentService {

    private static final String TAG = "OnBootService";

    @Inject
    MadcapAuthManager authManager;

    public OnBootService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ((MyApplication) getApplication()).getComponent().inject(this);
        MyApplication.madcapLogger.d(TAG, "onHandleIntent");

        if (authManager.getUser() != null) {
            Intent sintent = new Intent(this, DataCollectionService.class);
            sintent.putExtra("callee",TAG);
            startService(sintent);
        } else {
            final Context context = this;
            authManager.silentLogin(this, new LoginResultCallback() {
                @Override
                public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    MyApplication.madcapLogger.w(TAG, "Google Play Services connection failed. Error code: " + connectionResult);
                    loginFailed();
                    // TODO: Unregister this listener from mGoogleClientApi in MadcapAuthManager?
                }

                @Override
                public void onConnected(@Nullable Bundle bundle) {
                    MyApplication.madcapLogger.d(TAG, "Successfully connected to Google Play Services.");
                }

                @Override
                public void onConnectionSuspended(int i) {
                    MyApplication.madcapLogger.w(TAG, "Google SignIn API Connection suspended. Error code: " + i);
                    loginFailed();
                }

                @Override
                public void onServicesUnavailable(int connectionResult) {
                    MyApplication.madcapLogger.w(TAG, "Google SignIn API Connection suspended. Error code: " + i);
                    loginFailed();
                }

                @Override
                public void onLoginResult(GoogleSignInResult signInResult) {
                    if (signInResult.isSuccess()) {
                        MyApplication.madcapLogger.d(TAG, "Google SignIn successfully authenticated user to MADCAP.");

                        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(getString(R.string.data_collection_pref), true)) {
                            Intent intent = new Intent(context, DataCollectionService.class);
                            intent.putExtra("callee",TAG);
                            startService(intent);
                        }
                        // TODO: Create reminder notification to enable MADCAP data collection?
                    } else {
                        MyApplication.madcapLogger.w(TAG, "Google SignIn failed to authenticate user to MADCAP.");
                        loginFailed();
                    }
                }

                private void loginFailed() {
                    Toast.makeText(context, "MADCAP failed to login automatically. Please open the MADCAP application to sign in.", Toast.LENGTH_SHORT).show();

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
                    mBuilder.setSmallIcon(R.drawable.ic_stat_madcaplogo);
                    mBuilder.setContentTitle("MADCAP Automatic SignIn Failed");
                    mBuilder.setContentText("MADCAP failed to login automatically. Please open the MADCAP application to sign in.");
                    mBuilder.setDefaults(Notification.DEFAULT_ALL);
                    mBuilder.setPriority(Notification.PRIORITY_MAX);
                    mBuilder.setAutoCancel(true);
                    // Creates an explicit intent for an Activity in your app
                    Intent resultIntent = new Intent(context, SignInActivity.class);

                    // The stack builder object will contain an artificial back stack for the
                    // started Activity.
                    // This ensures that navigating backward from the Activity leads out of
                    // your application to the Home screen.

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

                    // Adds the back stack for the Intent (but not the Intent itself)
                    stackBuilder.addParentStack(SignInActivity.class);
                    // Adds the Intent that starts the Activity to the top of the stack
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    // mId allows you to update the notification later on.
                    mNotificationManager.notify(1, mBuilder.build());
                }
            });
        }
    }
}



