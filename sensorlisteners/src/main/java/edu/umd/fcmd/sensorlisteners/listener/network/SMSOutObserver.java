package edu.umd.fcmd.sensorlisteners.listener.network;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import edu.umd.fcmd.sensorlisteners.model.network.MSMSProbe;

import static android.Manifest.permission_group.SMS;
import static android.provider.Telephony.TextBasedSmsColumns.MESSAGE_TYPE_SENT;

/**
 * Created by MMueller on 12/28/2016.
 *
 * Observer for outgoing sms messages.
 */
public class SMSOutObserver extends ContentObserver {
    public static final String OUT = "OUT";
    public static final String NOT_OUT = "NOT_OUT";
    private final String TAG = getClass().getSimpleName();
    private static final String CONTENT_SMS = "content://sms";

    private final NetworkListener networkListener;
    private final Context context;

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public SMSOutObserver(Handler handler, NetworkListener networkListener, Context context) {
        super(handler);
        this.networkListener = networkListener;
        this.context = context;
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);

        MSMSProbe msmsProbe = new MSMSProbe();
        msmsProbe.setDate(System.currentTimeMillis());

        if(readFromOutgoingSMS(context).equals(OUT)){
            msmsProbe.setAction("SMS_OUTGOING");

            networkListener.onUpdate(msmsProbe);
        }

    }

    @SuppressWarnings("MethodReturnAlwaysConstant")
    @Override
    public boolean deliverSelfNotifications() {
        return false;
    }

    /**
     * Returns OUT if the SMS is outgoing.
     * @param context the context
     * @return OUT if the SMS is outgoing
     */
    private String readFromOutgoingSMS(Context context) {
        Cursor cursor = context.getContentResolver().query(
                Uri.parse(CONTENT_SMS), null, null, null, null);
        if (cursor != null && cursor.moveToNext()) {
            String protocol = cursor.getString(cursor.getColumnIndex("protocol"));
            int type = cursor.getInt(cursor.getColumnIndex("type"));

            // If the type is a sent message we return OUT.
            if (protocol != null && type == MESSAGE_TYPE_SENT) {
                return OUT;
            }

            cursor.close();
        }

        return NOT_OUT;
    }
}
