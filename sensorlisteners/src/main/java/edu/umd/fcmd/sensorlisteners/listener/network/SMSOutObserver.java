package edu.umd.fcmd.sensorlisteners.listener.network;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

import edu.umd.fcmd.sensorlisteners.model.network.MSMSProbe;

/**
 * Created by MMueller on 12/28/2016.
 *
 * Observer for outgoing sms messages.
 */
public class SMSOutObserver extends ContentObserver {
    private final String TAG = getClass().getSimpleName();
    private final NetworkListener networkListener;

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public SMSOutObserver(Handler handler, NetworkListener networkListener) {
        super(handler);
        this.networkListener = networkListener;
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);

        MSMSProbe msmsProbe = new MSMSProbe();
        msmsProbe.setDate(System.currentTimeMillis());
        msmsProbe.setAction("SMS_OUTGOING");

        networkListener.onUpdate(msmsProbe);
    }

    @SuppressWarnings("MethodReturnAlwaysConstant")
    @Override
    public boolean deliverSelfNotifications() {
        return false;
    }
}
