package org.fraunhofer.cese.funf_sensor.Probe.GPSProbe;

import android.location.Location;

/**
 * Created by MLang on 05.02.2015.
 */
public interface GPSCallback {
    public abstract void onGPSUpdate(Location location);
}

