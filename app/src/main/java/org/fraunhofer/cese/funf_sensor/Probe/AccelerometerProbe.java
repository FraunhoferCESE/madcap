package org.fraunhofer.cese.funf_sensor.Probe;


import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;


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
    float[] gravity = {1,1,1};



    @Override
    protected void onEnable() {
        super.onStart();
        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);

        boolean hasLinearAccelerometer = false;
        List<Sensor> list = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor : list) {
            if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                hasLinearAccelerometer = true;
            }
        }


        if (hasLinearAccelerometer) {
            acclerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            Log.i(TAG, "using linear accelerometer");
        } else {
            acclerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            Log.i(TAG, "using regular accelerometer");
        }

        sensorManager.registerListener(this, acclerometerSensor, SensorManager.SENSOR_DELAY_UI);
        lastProbeStart = System.currentTimeMillis();
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

        long now = System.currentTimeMillis();

        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            if ((now - lastProbeStart) > (measureInterval - measureDuration)) {
                float[] sensorData = event.values;
                long timestampLocal = event.timestamp;
                Intent intent = new Intent();
                intent.putExtra(TAG, timestampLocal);
                intent.putExtra(TAG, sensorData);
                intent.putExtra(TAG, "linear acceleration sensor.");
                sendData(intent);
                if ((now - lastProbeStart) > measureInterval) {
                    lastProbeStart = System.currentTimeMillis();
                }
            }
        }

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){

            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];


            if ((now - lastProbeStart) > (measureInterval - measureDuration)) {

                long timestampLocal = event.timestamp;

                float[] linear_acceleration={0,0,0};
                linear_acceleration[0] = event.values[0] - gravity[0];
                linear_acceleration[1] = event.values[1] - gravity[1];
                linear_acceleration[2] = event.values[2] - gravity[2];



                Intent intent = new Intent();
                intent.putExtra(TAG, timestampLocal);
                intent.putExtra(TAG, linear_acceleration);
                intent.putExtra(TAG, "regular acceleration sensor.");
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