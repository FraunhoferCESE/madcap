package edu.umd.fcmd.sensorlisteners.listener;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.Nullable;
import android.util.Log;

import edu.umd.fcmd.sensorlisteners.NoSensorFoundException;
import edu.umd.fcmd.sensorlisteners.model.State;
import edu.umd.fcmd.sensorlisteners.service.StateManager;

public abstract class SensorListener<T extends State> implements SensorEventListener, Listener<T> {
    private static final String TAG = SensorListener.class.getSimpleName();
    private final Sensor mSensor;
    private final Context mContext;
    private final SensorManager mSensorManager;
    private final StateManager<T> mStateManager;

    private T mLastState;

    public SensorListener(@Nullable Sensor sensor, Context context, StateManager stateManager) {
        this.mSensor = sensor;
        this.mContext = context;
        this.mStateManager = stateManager;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public void startListening() throws NoSensorFoundException {
        if (mSensor != null){
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            throw new NoSensorFoundException();
        }
    }

    @Override
    public void stopListening() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onUpdate(T state) {
        mStateManager.save(state);
    }

    abstract boolean isSignificant(T newState, T lastState);

    abstract T parseEvent(SensorEvent event);

    @Override
    public void onSensorChanged(SensorEvent event) {
        T state = parseEvent(event);
        if (isSignificant(state, mLastState)) {
            onUpdate(state);
            mLastState = state;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.i(TAG, "onAccuracyChanged: " + sensor + accuracy);
    }

    public int percentDiff(float newValue, float oldValue) {
        return Math.abs(((int)((newValue - oldValue)/oldValue)) * 100);
    }
}
