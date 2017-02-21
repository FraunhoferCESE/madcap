package org.fraunhofer.cese.madcap.firebasecloudmessaging;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import org.fraunhofer.cese.madcap.MyApplication;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * The handling of creation, rotation and updating the registration of Firebase Tokens.
 */
public class MadcapFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MFirebaseIIDService";

    public MadcapFirebaseInstanceIDService() {

    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        MyApplication.madcapLogger.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
    }
}
