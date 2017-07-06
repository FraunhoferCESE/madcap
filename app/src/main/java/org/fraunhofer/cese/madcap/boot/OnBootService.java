package org.fraunhofer.cese.madcap.boot;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;

import org.fraunhofer.cese.madcap.MyApplication;
import org.fraunhofer.cese.madcap.R;
import org.fraunhofer.cese.madcap.authentication.AuthenticationProvider;
import org.fraunhofer.cese.madcap.authentication.SignInActivity;
import org.fraunhofer.cese.madcap.authentication.SilentLoginResultCallback;
import org.fraunhofer.cese.madcap.services.DataCollectionService;

import javax.inject.Inject;

import timber.log.Timber;


/**
 * Service to attempt automatic login and start of MADCAP data collection on boot.
 * <p>
 * Created by llayman on 11/22/2016.
 */

public class OnBootService extends IntentService {

    @SuppressWarnings("PackageVisibleField")
    @Inject
    AuthenticationProvider authManager;

    public OnBootService() {
        super("OnBootService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //noinspection CastToConcreteClass
        ((MyApplication) getApplication()).getComponent().inject(this);

        Timber.d("onHandleIntent");

        if (authManager.getUser() != null) {
            Intent sintent = new Intent(this, DataCollectionService.class);
            sintent.putExtra("boot", true);
            startService(sintent);
        } else {
            final Context context = this;
            authManager.silentLogin(this, new SilentLoginResultCallback() {
                @Override
                public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    Timber.w("Google Play Services connection failed. Error code: " + connectionResult);
                    loginFailed();
                }

                @Override
                public void onServicesUnavailable(int connectionResult) {
                    Timber.w("Google SignIn API is unavailable. Error code: " + connectionResult);
                    loginFailed();
                }

                @Override
                public void onLoginResult(GoogleSignInResult signInResult) {
                    if (signInResult.isSuccess()) {
                        Timber.d("Google SignIn successfully authenticated user to MADCAP.");

                        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(getString(R.string.pref_dataCollection), true)) {
                            startService(new Intent(context, DataCollectionService.class).putExtra("boot",true));
                        }
                    } else {
                        Timber.w("Google SignIn failed to authenticate user to MADCAP.");
                        loginFailed();
                    }
                }

                private void loginFailed() {
                    Toast.makeText(context, R.string.silent_signin_failed, Toast.LENGTH_SHORT).show();

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
                    mBuilder.setSmallIcon(R.drawable.ic_stat_madcaplogo);
                    mBuilder.setContentTitle(getString(R.string.silent_signin_failed_notification_title));
                    mBuilder.setContentText(getString(R.string.silent_signin_failed));
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



