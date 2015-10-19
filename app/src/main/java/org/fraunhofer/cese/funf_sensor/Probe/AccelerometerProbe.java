package org.fraunhofer.cese.funf_sensor.Probe;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import edu.mit.media.funf.probe.Probe;

/**
 *
 */
public class AccelerometerProbe extends Probe.Base implements Probe.ContinuousProbe, SensorEventListener {

    private static final String TAG = "AccelerometerProbe: ";
    private SensorManager sensorManager;
    private Sensor acclerometerSensor;
    private static long lastUpdate;

    @Override
    protected void onEnable() {
        super.onStart();
        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        acclerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, acclerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        lastUpdate = System.currentTimeMillis();
        Log.i(TAG, " enabled");
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        sensorManager.unregisterListener(this, acclerometerSensor);
        Log.i(TAG, "disabled.");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            long now = System.currentTimeMillis();

            if((now-lastUpdate)>200) {
                float[] sensorData = event.values;
                long timestamp = event.timestamp;
                Intent intent = new Intent();
                intent.putExtra(TAG, timestamp);
                intent.putExtra(TAG, sensorData);
                sendData(intent);
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.i(TAG, "accuracy changed.");
    }

    public void sendData(Intent intent) {
        Log.i(TAG, "sent.");
        sendData(getGson().toJsonTree(intent).getAsJsonObject());
    }
}
