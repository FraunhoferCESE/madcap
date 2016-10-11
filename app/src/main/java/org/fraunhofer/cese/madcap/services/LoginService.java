package org.fraunhofer.cese.madcap.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.Status;

import org.fraunhofer.cese.madcap.MainActivity;
import org.fraunhofer.cese.madcap.SignInActivity;
import org.fraunhofer.cese.madcap.authentification.MadcapAuthEventHandler;
import org.fraunhofer.cese.madcap.authentification.MadcapAuthManager;

/**
 * Created by MMueller on 10/7/2016.
 */

public class LoginService extends Service implements MadcapAuthEventHandler {
    private static final String TAG = "Madcap Login Service";

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
    public void onCreate(){
        Log.d(TAG, "onCreate Login Service");
        MadcapAuthManager.setCallbackClass(this);
        Log.d(TAG, "Trying to log in silently");
        MadcapAuthManager.silentLogin();
    }

    /**
     * Specifies what the class is expected to do, when the silent login was sucessfull.
     *
     * @param result
     */
    @Override
    public void onSilentLoginSuccessfull(GoogleSignInResult result) {
        Log.d(TAG, "Silent login was successfull. Logged in as "+MadcapAuthManager.getUserId());
        
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
        Intent intent = new Intent(getBaseContext(),SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
}
