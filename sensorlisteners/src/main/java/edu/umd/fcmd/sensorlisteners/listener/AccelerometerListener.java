package edu.umd.fcmd.sensorlisteners.listener;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import java.util.Date;

import edu.umd.fcmd.sensorlisteners.model.AccelerometerState;
import edu.umd.fcmd.sensorlisteners.service.StateManager;

/**
 * Created by ANepaul on 10/28/2016.
 */

public class AccelerometerListener extends SensorListener<AccelerometerState> {
    private static final int ACCELEROMETER_THRESHOLD_PERCENT = 5;

    public AccelerometerListener(Context context, StateManager<AccelerometerState> stateManager) {
        super(((SensorManager) context.getSystemService(Context.SENSOR_SERVICE))
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), context, stateManager);
    }

    /**
     * Checks if the new state is significantly different than the old state depending on the
     * {@value ACCELEROMETER_THRESHOLD_PERCENT}. If old state is null, then the new state is
     * considered significant.
     * @param newState
     * @param oldState
     * @return boolean value if the new state is considered significant
     */
    @Override
    public boolean isSignificant(AccelerometerState newState, AccelerometerState oldState) {
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
    AccelerometerState parseEvent(SensorEvent event) {
        AccelerometerState state = new AccelerometerState();
        state.setDate(new Date(event.timestamp));
        state.setxAxis(event.values[0]);
        state.setyAxis(event.values[1]);
        state.setzAxis(event.values[2]);
        return state;
    }
}
