package org.fraunhofer.cese.madcap.firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.fraunhofer.cese.madcap.R;
import org.fraunhofer.cese.madcap.authentication.AuthenticationProvider;
import org.fraunhofer.cese.madcap.authentication.SignInActivity;
import org.fraunhofer.cese.madcap.util.NotificationChannelDescriptor;

import java.util.Map;

import javax.inject.Inject;

import timber.log.Timber;

import static android.graphics.Color.RED;

/**
 * This class does to Firebase message handling.
 */
public class MadcapFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MFirebaseMsgService";
    private SharedPreferences sharedPreferences;

    @Inject
    AuthenticationProvider authenticationProvider;

    private Context context;

    public MadcapFirebaseMessagingService(){
        retrieveCurrentToken();
        // this is called when firebase is sending data
    }

    public MadcapFirebaseMessagingService(Context context){
        this.context = context; // this was just a test - this is not called when firebase sends the message again , so this.context will be null in onMessageReceive
    }

    /**
     * Get the Token of GoogleFirebase for that device
     */
    private void retrieveCurrentToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        String msg = context.getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        // Log token
                        Log.d(TAG, token);
                    }
                });


    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // For Madcap we want to make sure only data messages are being received.

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Timber.d("From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            if ((authenticationProvider.getUser() != null) && sharedPreferences.getBoolean(Resources.getSystem().getString(R.string.pref_dataCollection), true)) {
                processIncomingMessage(remoteMessage.getData());
            }
            else {
                Log.d(TAG, "User either null or shared prefrences are false");
            }
        }
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    // [END on_new_token]


    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        // Send token to service.
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     */
    private void processIncomingMessage(Map<String, String> map) {
        String type = map.get("type");
        switch (type) {
            case "notification":
                String name = map.get("name");
                String text = map.get("text");
                String info = map.get("info");
                showUiNotification(name, text, info);
                break;
            default:
                break;
            // TODO: introduce new types according to David Maimons requirements
        }
    }

    private void showUiNotification(CharSequence title, CharSequence text, CharSequence info) {
        Intent intent = new Intent(context, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        final String channelId = NotificationChannelDescriptor.NOTIFICATION_CHANNEL_ID;
        final Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(android.R.drawable.ic_dialog_alert)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setContentInfo(info)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setColor(RED)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

        // mId allows you to update the notification later on.
        notificationManager.notify(2, notificationBuilder.build());

//        // Creates an explicit intent for an Activity in your app
//        Intent resultIntent = new Intent(this, SignInActivity.class);
//
//        // The stack builder object will contain an artificial back stack for the
//        // started Activity.
//        // This ensures that navigating backward from the Activity leads out of
//        // your application to the Home screen.
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//
//        // Adds the back stack for the Intent (but not the Intent itself)
//        stackBuilder.addParentStack(SignInActivity.class);
//        // Adds the Intent that starts the Activity to the top of the stack
//        stackBuilder.addNextIntent(resultIntent);
//        PendingIntent resultPendingIntent =
//                stackBuilder.getPendingIntent(
//                        0,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );
//        mBuilder.setContentIntent(resultPendingIntent);
    }
}