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

    private static final String TAG = "Fraunhofer.AccelPr";
    private SensorManager sensorManager;
    private Sensor linearAcclerometerSensor;
    private static long lastProbeStart;
    private static final long measureInterval = 30000;
    private static final long measureDuration = 1000;

    @Override
    protected void onEnable() {
        super.onStart();
        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        linearAcclerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, linearAcclerometerSensor, SensorManager.SENSOR_DELAY_UI);
        lastProbeStart = System.currentTimeMillis();
        Log.i(TAG, " enabled");
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        sensorManager.unregisterListener(this, linearAcclerometerSensor);
        Log.i(TAG, "disabled.");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            long now = System.currentTimeMillis();

            if ((now - lastProbeStart) > (measureInterval-measureDuration)) {
                float[] sensorData = event.values;
                long timestamp = event.timestamp;
                Intent intent = new Intent();
                intent.putExtra(TAG, timestamp);
                intent.putExtra(TAG, sensorData);
                sendData(intent);
                if ((now - lastProbeStart) > measureInterval) {
                    lastProbeStart = System.currentTimeMillis();
                }
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.i(TAG, "accuracy changed.");
    }

    public void sendData(Intent intent) {
        sendData(getGson().toJsonTree(intent).getAsJsonObject());
    }
}