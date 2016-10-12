package org.fraunhofer.cese.madcap.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.Status;

import org.fraunhofer.cese.madcap.MainActivity;
import org.fraunhofer.cese.madcap.R;
import org.fraunhofer.cese.madcap.SignInActivity;
import org.fraunhofer.cese.madcap.authentification.MadcapAuthEventHandler;
import org.fraunhofer.cese.madcap.authentification.MadcapAuthManager;

import java.util.Stack;

import static com.pathsense.locationengine.lib.detectionLogic.b.C;

/**
 * Created by MMueller on 10/7/2016.
 */

public class LoginService extends Service implements Cloneable, MadcapAuthEventHandler {
    private static final String TAG = "Madcap Login Service";
    private MadcapAuthManager madcapAuthManager = MadcapAuthManager.getInstance();
    private TaskStackBuilder stackBuilder;

    /**
     * Return the communication channel to the service.  May return null if
     * clients can not bind to the service.  The returned
     * {@link IBinder} is usually for a complex interface
     * that has been <a href="{@docRoot}guide/components/aidl.html">described using
     * aidl</a>.
     * <p>
     * <p><em>Note that unlike other application components, calls on to the
     * IBinder interface returned here may not happen on the main thread
     * of the process</em>.  More information about the main thread can be found in
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
        return null;
    }

    public void onDestroy() {
        //Toast.makeText(this, "Login Service Stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate Login Service");
        madcapAuthManager.setCallbackClass(this);
        Log.d(TAG, "Trying to log in silently");
        madcapAuthManager.silentLogin();
    }

    /**
     * Specifies what the class is expected to do, when the silent login was sucessfull.
     *
     * @param result
     */
    @Override
    public void onSilentLoginSuccessfull(GoogleSignInResult result) {
        Log.d(TAG, "Silent login was successfull. Logged in as " + madcapAuthManager.getUserId());

        Intent intent = new Intent(LoginService.this, DataCollectionService.class);
        startService(intent);
    }

    /**
     * Specifies what the class is expected to do, when the silent login was not successfull.
     *
     * @param opr
     */
    @Override
    public void onSilentLoginFailed(OptionalPendingResult<GoogleSignInResult> opr) {
        Log.d(TAG, "Silent login failed");

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.ic_stat_madcaplogo);
        mBuilder.setContentTitle("Madcap Auto Login Failed");
        mBuilder.setContentText("Madcap could not log you in automatically, please login to continue participating in our study (and to get your money).");
        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, SignInActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        if(stackBuilder == null){
            stackBuilder = TaskStackBuilder.create(this);
        }
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

    /**
     * Specifies what the class is expected to do, when the regular sign in was successful.
     */
    @Override
    public void onSignInSucessfull() {
        Intent intent = new Intent(LoginService.this, DataCollectionService.class);
        startService(intent);
    }

    /**
     * Specifies what the app is expected to do when the Signout was sucessfull.
     *
     * @param status
     */
    @Override
    public void onSignOutResults(Status status) {

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

    /**
     * For testing purposes only.
     * @param madcapAuthManager
     */
    protected void setMadcapAuthManager(MadcapAuthManager madcapAuthManager){
        this.madcapAuthManager = madcapAuthManager;
    }

    /**
     * Clone Method used by testing classes.
     * @return The cloned object.
     */
    @Override
    public LoginService clone() throws CloneNotSupportedException {
        final LoginService result = (LoginService) super.clone();
        // copy fields that need to be copied here!
        return result;

    }

    /**
     * For testing purposes
     * @param o Object to be checked if equals.
     * @return True, if objects are same reference or from same Type.
     */
    @Override
    public boolean equals(Object o){
        if(o.getClass() == LoginService.class){
            //Return true, due to no real mutable class variables.
            return true;
        }else{
            return super.equals(o);
        }
    }

    /**
     * Hash code method.
     * @return the hash code.
     */
    @Override
    public int hashCode() {
        int result = madcapAuthManager != null ? madcapAuthManager.hashCode() : 0;
        result = 31 * result + stackBuilder.hashCode();
        return result;
    }

    /**
     * Setter for the TaskStack Builder, for testing purposes only.
     * @deprecated
     * @param stackBuilder
     */
    public void setStackBuilder(TaskStackBuilder stackBuilder){
        this.stackBuilder = stackBuilder;
    }
}
