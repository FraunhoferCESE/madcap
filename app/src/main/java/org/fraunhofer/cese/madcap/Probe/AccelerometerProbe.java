package org.fraunhofer.cese.madcap.Probe;


import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import org.fraunhofer.cese.madcap.MyApplication;

import java.util.List;

import edu.mit.media.funf.probe.Probe;

/**
 *
 */
public class AccelerometerProbe extends Probe.Base implements Probe.ContinuousProbe, SensorEventListener {

    private static final String TAG = "Fraunhofer.AccelPr";
    private SensorManager sensorManager;
    private Sensor acclerometerSensor;

    private static long lastProbeStart;
    private static final long measureInterval = 30000;
    private static final long measureDuration = 1000;
    private static final float alpha = 0.8f;

    /**
     * initial values for the filter.
     */
    float[] gravity = {1, 1, 1};


    @Override
    protected void onEnable() {
        onStart();
        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);

        boolean hasLinearAccelerometer = false;
        List<Sensor> list = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor : list) {
            if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                hasLinearAccelerometer = true;
                break;
            }
        }


        if (hasLinearAccelerometer) {
            acclerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            MyApplication.madcapLogger.i(TAG, "using linear accelerometer");
        } else {
            acclerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            MyApplication.madcapLogger.i(TAG, "using regular accelerometer");
        }

        sensorManager.registerListener(this, acclerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        lastProbeStart = System.currentTimeMillis();
        MyApplication.madcapLogger.i(TAG, " enabled");
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        sensorManager.unregisterListener(this, acclerometerSensor);
        MyApplication.madcapLogger.i(TAG, "disabled.");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        long now = System.currentTimeMillis();

        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            if (now - lastProbeStart > measureInterval - measureDuration) {
                Intent intent = new Intent();
                intent.putExtra("type", "linear accelerometer");
                intent.putExtra("x", event.values[0]);
                intent.putExtra("y", event.values[1]);
                intent.putExtra("z", event.values[2]);
                intent.putExtra("timestamp", event.timestamp);
                sendData(intent);
                if ((now - lastProbeStart) > measureInterval) {
                    lastProbeStart = System.currentTimeMillis();
                }
            }
        }
        else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity[0] = (alpha * gravity[0]) + ((1 - alpha) * event.values[0]);
            gravity[1] = (alpha * gravity[1]) + ((1 - alpha) * event.values[1]);
            gravity[2] = (alpha * gravity[2]) + ((1 - alpha) * event.values[2]);

            if ((now - lastProbeStart) > (measureInterval - measureDuration)) {

                long timestampLocal = event.timestamp;

                float[] linear_acceleration = {0, 0, 0};
                linear_acceleration[0] = event.values[0] - gravity[0];
                linear_acceleration[1] = event.values[1] - gravity[1];
                linear_acceleration[2] = event.values[2] - gravity[2];


                Intent intent = new Intent();
                intent.putExtra("type", "regular acceleration");
                intent.putExtra("x", linear_acceleration[0]);
                intent.putExtra("y", linear_acceleration[1]);
                intent.putExtra("z", linear_acceleration[2]);
                intent.putExtra("timestamp", timestampLocal);
                sendData(intent);
                if ((now - lastProbeStart) > measureInterval) {
                    lastProbeStart = System.currentTimeMillis();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void sendData(Intent intent) {
        sendData(getGson().toJsonTree(intent).getAsJsonObject());
    }
}