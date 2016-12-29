package edu.umd.fcmd.sensorlisteners.listener.network;

import android.database.ContentObserver;
import android.os.Handler;

import edu.umd.fcmd.sensorlisteners.model.network.MSMSProbe;

/**
 * Created by MMueller on 12/28/2016.
 *
 * Observer for incoming sms and mms actions.
 */
public class MMSOutObserver extends ContentObserver {
    private final String TAG = getClass().getSimpleName();

    private final NetworkListener networkListener;

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public MMSOutObserver(Handler handler, NetworkListener networkListener) {
        super(handler);
        this.networkListener = networkListener;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        MSMSProbe msmsProbe = new MSMSProbe();
        msmsProbe.setDate(System.currentTimeMillis());
        msmsProbe.setAction("MMS_OUTGOING");

        networkListener.onUpdate(msmsProbe);
    }

    @SuppressWarnings("MethodReturnAlwaysConstant")
    @Override
    public boolean deliverSelfNotifications() {
        return false;
    }
}
