package org.fraunhofer.cese.funf_sensor;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import edu.mit.media.funf.probe.Probe;

/**
 * Created by MLang on 04.02.2015.
 */
public class SpeedProbe extends Probe.Base {
    Context mContext;
    public SpeedProbe(Context mContext){
        this.mContext = mContext;
    }
    private LocationManager lm;
    private LocationListener ll;
    double mySpeed, maxSpeed;
    private final String Speed = null;
    /** Called when the activity is first created. */
    @Override
    public void onStart() {
        Log.i(Speed, "working1 ");
        super.onStart();
        //tv = new TextView(this);
        //setContentView(tv);

        maxSpeed = mySpeed = 0;
        Log.i(Speed, "working1 ");
        lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        ll = new SpeedActionListener();
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                ll);
        stop();
    }
}
