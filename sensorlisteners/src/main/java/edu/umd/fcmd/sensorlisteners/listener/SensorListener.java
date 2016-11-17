package edu.umd.fcmd.sensorlisteners.listener;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.Nullable;
import android.util.Log;

import edu.umd.fcmd.sensorlisteners.NoSensorFoundException;
import edu.umd.fcmd.sensorlisteners.model.Probe;
import edu.umd.fcmd.sensorlisteners.service.ProbeManager;

abstract class SensorListener<T extends Probe> implements SensorEventListener, Listener<T> {
    private static final String TAG = SensorListener.class.getSimpleName();
    private final Sensor mSensor;
    private final SensorManager mSensorManager;
    private final ProbeManager<T> mProbeManager;

    private T mLastState;

    SensorListener(@Nullable Sensor sensor, Context context, ProbeManager<T> probeManager) {
        mSensor = sensor;
        mProbeManager = probeManager;
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
        mProbeManager.save(state);
    }

    abstract boolean isSignificant(T newState, T oldState);

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
        Log.i(TAG, "onAccuracyChanged: " + sensor + ", accuracy: " + accuracy);
    }

    @SuppressWarnings("NumericCastThatLosesPrecision")
    static int percentDiff(float newValue, float oldValue) {
        return Math.abs(((int)((newValue - oldValue)/oldValue)) * 100);
    }
}
