package org.fraunhofer.cese.madcap.firebasecloudmessaging;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.fraunhofer.cese.madcap.MyApplication;
import org.fraunhofer.cese.madcap.R;
import org.fraunhofer.cese.madcap.authentication.AuthenticationManager;
import org.fraunhofer.cese.madcap.authentication.SignInActivity;

import java.util.Map;

import javax.inject.Inject;

import static android.graphics.Color.RED;

/**
 * This class does to Firebase message handling.
 */
public class MadcapFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MFirebaseMsgService";

    @Inject
    AuthenticationManager authenticationManager;

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

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        MyApplication.madcapLogger.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            MyApplication.madcapLogger.d(TAG, "Message data payload: " + remoteMessage.getData());
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            if (authenticationManager.getUserId() != null && prefs.getBoolean(getString(R.string.data_collection_pref), true)) {
                processIncomingMessage(remoteMessage.getData());
            }
        }

        // Check if message contains a notification payload.
        // Deprecated
//        if (remoteMessage.getNotification() != null) {
//            MyApplication.madcapLogger.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
//        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     */
//    private void sendNotification(String messageBody) {
//        Intent intent = new Intent(this, MainActivityOld.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.madcaplogo2)
//                .setContentTitle("FCM Message")
//                .setContentText(messageBody)
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
//    }
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

            //TODO: introduce new types according to David Maimons requirements
        }
    }

    private void showUiNotification(String title, String text, String info) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(android.R.drawable.ic_dialog_alert);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(text);
        mBuilder.setContentInfo(info);
        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setColor(RED);
        mBuilder.setAutoCancel(true);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, SignInActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

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

//        //For demo purposes
//        Intent reactIntnet = new Intent();
//
//        String reactLabel = "React";
//        RemoteInput remoteInput = new RemoteInput.Builder("key_text_reply")
//                .setLabel(reactLabel)
//                .build();
//
//        Notification.Action reactAction =
//                new Notification.Action.Builder(android.R.drawable.ic_dialog_alert,
//                        "React now", null)
//                        .addRemoteInput(remoteInput)
//                        .build();
//
//        mBuilder.addAction(reactAction);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(2, mBuilder.build());
    }
}
