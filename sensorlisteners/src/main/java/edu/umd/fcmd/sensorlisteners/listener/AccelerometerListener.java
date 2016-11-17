package edu.umd.fcmd.sensorlisteners.listener;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import edu.umd.fcmd.sensorlisteners.model.AccelerometerProbe;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;

/**
 * A listener to capture Accelerometer data on sensor changed events.
 *
 * Created by ANepaul on 10/28/2016.
 */

public class AccelerometerListener extends SensorListener<AccelerometerProbe> {
    private static final int ACCELEROMETER_THRESHOLD_PERCENT = 5;

    public AccelerometerListener(Context context, ProbeManager<AccelerometerProbe> probeManager) {
        super(((SensorManager) context.getSystemService(Context.SENSOR_SERVICE))
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), context, probeManager);
    }

    /**
     * Checks if the new state is significantly different than the old state depending on the
     * {@value ACCELEROMETER_THRESHOLD_PERCENT}. If old state is null, then the new state is
     * considered significant.
     * @param newState new data from the accelerometer
     * @param oldState last seen data from the accelerometer
     * @return boolean value if the new state is considered significant
     */
    @Override
    public boolean isSignificant(AccelerometerProbe newState, AccelerometerProbe oldState) {
        // Null Check
        if (oldState == null) {
            return true;
        }
        if (newState == null) {
            return false;
        }

        // Checking X diff
        int xDiff = percentDiff(newState.getxAxis(), oldState.getxAxis());
        if (xDiff > ACCELEROMETER_THRESHOLD_PERCENT) {
            return true;
        }

        // Checking Y diff
        int yDiff = percentDiff(newState.getyAxis(), oldState.getyAxis());
        if (yDiff > ACCELEROMETER_THRESHOLD_PERCENT) {
            return true;
        }

        // Checking Z diff
        int zDiff = percentDiff(newState.getzAxis(), oldState.getzAxis());
        if (zDiff > ACCELEROMETER_THRESHOLD_PERCENT) {
            return true;
        }

        // Otherwise
        return false;
    }

    @Override
    AccelerometerProbe parseEvent(SensorEvent event) {
        AccelerometerProbe state = new AccelerometerProbe();
        state.setDate(System.currentTimeMillis());
        state.setxAxis(event.values[0]);
        state.setyAxis(event.values[1]);
        state.setzAxis(event.values[2]);
        return state;
    }
}
