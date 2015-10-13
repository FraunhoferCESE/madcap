package org.fraunhofer.cese.funf_sensor;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.inject.Inject;

import org.fraunhofer.cese.funf_sensor.Probe.BluetoothProbe;
import org.fraunhofer.cese.funf_sensor.Probe.CallStateProbe;
import org.fraunhofer.cese.funf_sensor.Probe.ForegroundProbe;
import org.fraunhofer.cese.funf_sensor.Probe.MyRunningApplicationsProbe;
import org.fraunhofer.cese.funf_sensor.Probe.PowerProbe;
import org.fraunhofer.cese.funf_sensor.Probe.SMSProbe;
import org.fraunhofer.cese.funf_sensor.Probe.StateProbe;
import org.fraunhofer.cese.funf_sensor.Probe.AudioProbe;
import org.fraunhofer.cese.funf_sensor.appengine.GoogleAppEnginePipeline;

import java.text.SimpleDateFormat;

import edu.mit.media.funf.FunfManager;
import edu.mit.media.funf.probe.builtin.AccelerometerSensorProbe;
import edu.mit.media.funf.probe.builtin.BatteryProbe;
import edu.mit.media.funf.probe.builtin.ScreenProbe;
import edu.mit.media.funf.probe.builtin.SimpleLocationProbe;
import roboguice.activity.RoboActivity;

public class MainActivity extends RoboActivity {
    private static final String TAG = "Fraunhofer."+MainActivity.class.getSimpleName();

    public static final String PIPELINE_NAME = "appengine";
    private FunfManager funfManager;

    @Inject
    private GoogleAppEnginePipeline pipeline;

    final SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

    //probes
    private AccelerometerSensorProbe accelerometerSensorProbe;
    private ForegroundProbe foregroundProbe;
    private MyRunningApplicationsProbe myRunningApplicationsProbe;
    private ScreenProbe screenProbe;
    private SimpleLocationProbe locationProbe;

    private SMSProbe sMSProbe;
    private PowerProbe powerProbe;
    private StateProbe stateProbe;
    private CallStateProbe callStateProbe;
    private AudioProbe audioProbe;
    private BluetoothProbe bluetoothProbe;

    private CheckBox enabledCheckbox;

//    private TextView dataCountView;
//    private Handler handler;

    private String applicationPackageName = "org.fraunhofer.cese.funf_sensor";

    private ServiceConnection funfManagerConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            funfManager = ((FunfManager.LocalBinder) service).getManager();
            Gson gson = funfManager.getGson();

            accelerometerSensorProbe = gson.fromJson(getString(R.string.probe_accelerometer), AccelerometerSensorProbe.class); // TODO: not working
            foregroundProbe = gson.fromJson(getString(R.string.probe_foreground), ForegroundProbe.class); // TODO: not working
            locationProbe = gson.fromJson(getString(R.string.probe_location), SimpleLocationProbe.class);
            myRunningApplicationsProbe = gson.fromJson(getString(R.string.probe_runningapplications), MyRunningApplicationsProbe.class); // TODO: not working
            screenProbe = gson.fromJson(new JsonObject(), ScreenProbe.class);
            sMSProbe = gson.fromJson(new JsonObject(), SMSProbe.class);
            powerProbe = gson.fromJson(new JsonObject(), PowerProbe.class);
            stateProbe = gson.fromJson(new JsonObject(), StateProbe.class);
            callStateProbe = gson.fromJson(new JsonObject(), CallStateProbe.class);
            audioProbe = gson.fromJson(new JsonObject(), AudioProbe.class);
            bluetoothProbe = gson.fromJson(new JsonObject(), BluetoothProbe.class);

            // Initialize the pipeline
            funfManager.registerPipeline(PIPELINE_NAME, pipeline);
            pipeline = (GoogleAppEnginePipeline) funfManager.getRegisteredPipeline(PIPELINE_NAME);
            Log.i(TAG, "Enabling pipeline: "+ PIPELINE_NAME);
            funfManager.enablePipeline(PIPELINE_NAME);
            registerListeners();

            // This checkbox enables or disables the pipeline
            enabledCheckbox.setChecked(pipeline.isEnabled());
            enabledCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (funfManager != null) {
                        if (isChecked && !pipeline.isEnabled()) {
                            Log.i(TAG,"Enabling pipeline: "+ PIPELINE_NAME);
                            funfManager.enablePipeline(PIPELINE_NAME);
                            registerListeners();
                        } else {
                            Log.d(TAG, "Disabling pipeline: " + PIPELINE_NAME);
                            funfManager.disablePipeline(PIPELINE_NAME);
                            unregisterListeners();
                        }
                    }
                }
            });
            enabledCheckbox.setEnabled(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            funfManager = null;
        }
    };

    private void registerListeners() {
        accelerometerSensorProbe.registerPassiveListener(pipeline);
        foregroundProbe.registerPassiveListener(pipeline);
        locationProbe.registerPassiveListener(pipeline);
        myRunningApplicationsProbe.registerPassiveListener(pipeline);
        screenProbe.registerPassiveListener(pipeline);
        sMSProbe.registerPassiveListener(pipeline);
        powerProbe.registerPassiveListener(pipeline);
        stateProbe.registerPassiveListener(pipeline);
        callStateProbe.registerPassiveListener(pipeline);
        audioProbe.registerPassiveListener(pipeline);
        bluetoothProbe.registerListener(pipeline);
    }

    private void unregisterListeners() {
        accelerometerSensorProbe.unregisterListener(pipeline);
        foregroundProbe.unregisterListener(pipeline);
        locationProbe.unregisterListener(pipeline);
        myRunningApplicationsProbe.unregisterListener(pipeline);
        screenProbe.unregisterListener(pipeline);
        sMSProbe.unregisterListener(pipeline);
        powerProbe.unregisterListener(pipeline);
        stateProbe.unregisterListener(pipeline);
        callStateProbe.unregisterListener(pipeline);
        audioProbe.unregisterListener(pipeline);
        bluetoothProbe.unregisterListener(pipeline);
    }

    //onCreate is the rendering of the main page
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        // Displays the count of rows in the data
//        dataCountView = (TextView) findViewById(R.id.dataCountText);

        //will be enabled in the onServiceConnected method
        enabledCheckbox = (CheckBox) findViewById(R.id.checkBox);
        enabledCheckbox.setEnabled(false);


        // Used to make interface changes on main thread
//        handler = new Handler();
//        final int delay = 15000;
//        handler.postDelayed(new Runnable() {
//            public void run() {
//                updateScanCount();
//                handler.postDelayed(this, delay);
//            }
//
//        },delay);


        // Bind to the service, to create the connection with FunfManager+
        Log.d(TAG, "Starting FunfManager");
        startService(new Intent(this, FunfManager.class));
        Log.d(TAG, "Binding Funf ServiceConnection to activity");
        getApplicationContext().bindService(new Intent(this, FunfManager.class), funfManagerConn, BIND_AUTO_CREATE);
    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterListeners();

        boolean isBound = false;
        isBound = getApplicationContext().bindService( new Intent(getApplicationContext(), FunfManager.class), funfManagerConn, Context.BIND_AUTO_CREATE );
        if(isBound)
            getApplicationContext().unbindService(funfManagerConn);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        pipeline.onRun(pipeline.ACTION_FLUSH,null);
    }

    //    private void updateScanCount() {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                String text = "Data Count: " + 0;
////                text += "\nLast archived: "+ sdf.format(sdf.getCalendar().getTime());
//                dataCountView.setText(text);
//            }
//        });
//    }

}