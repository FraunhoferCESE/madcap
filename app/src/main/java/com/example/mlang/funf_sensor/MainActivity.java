package com.example.mlang.funf_sensor;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

//import com.example.mlang.funf_sensor.Probe.Security.SecurityProbe;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import edu.mit.media.funf.FunfManager;
import edu.mit.media.funf.json.IJsonObject;
import edu.mit.media.funf.pipeline.BasicPipeline;
import edu.mit.media.funf.probe.Probe.DataListener;
import edu.mit.media.funf.probe.builtin.AccelerometerFeaturesProbe;
import edu.mit.media.funf.probe.builtin.AccelerometerSensorProbe;
import edu.mit.media.funf.probe.builtin.ActivityProbe;
import edu.mit.media.funf.probe.builtin.AndroidInfoProbe;
import edu.mit.media.funf.probe.builtin.AudioFeaturesProbe;
import edu.mit.media.funf.probe.builtin.AudioMediaProbe;
import edu.mit.media.funf.probe.builtin.BluetoothProbe;
import edu.mit.media.funf.probe.builtin.BrowserSearchesProbe;
import edu.mit.media.funf.probe.builtin.CallLogProbe;
import edu.mit.media.funf.probe.builtin.ContactProbe;
import edu.mit.media.funf.probe.builtin.ContentProviderProbe;
import edu.mit.media.funf.probe.builtin.GravitySensorProbe;
import edu.mit.media.funf.probe.builtin.GyroscopeSensorProbe;
import edu.mit.media.funf.probe.builtin.HardwareInfoProbe;
import edu.mit.media.funf.probe.builtin.ImageMediaProbe;
import edu.mit.media.funf.probe.builtin.LightSensorProbe;
import edu.mit.media.funf.probe.builtin.LinearAccelerationSensorProbe;
import edu.mit.media.funf.probe.builtin.MagneticFieldSensorProbe;
import edu.mit.media.funf.probe.builtin.OrientationSensorProbe;
import edu.mit.media.funf.probe.builtin.ProcessStatisticsProbe;
import edu.mit.media.funf.probe.builtin.ProximitySensorProbe;
import edu.mit.media.funf.probe.builtin.RotationVectorSensorProbe;
import edu.mit.media.funf.probe.builtin.RunningApplicationsProbe;
import edu.mit.media.funf.probe.builtin.ScreenProbe;
import edu.mit.media.funf.probe.builtin.ServicesProbe;
import edu.mit.media.funf.probe.builtin.SimpleLocationProbe;
import edu.mit.media.funf.probe.builtin.SmsProbe;
import edu.mit.media.funf.probe.builtin.TelephonyProbe;
import edu.mit.media.funf.probe.builtin.TemperatureSensorProbe;
import edu.mit.media.funf.probe.builtin.VideoMediaProbe;
import edu.mit.media.funf.probe.builtin.WifiProbe;
import edu.mit.media.funf.storage.NameValueDatabaseHelper;

/**
 * Created by MLang on 19.12.2014.
 */
public class MainActivity extends Activity implements DataListener {
    //all the event listeners have to be defined here

    public static final String PIPELINE_NAME = "default";
    private FunfManager funfManager;
    private BasicPipeline pipeline;

    private TextView txtView;
    private String TAG = this.getClass().getSimpleName();

    //probes
    private AccelerometerFeaturesProbe accelerometerFeaturesProbe;
    private AccelerometerSensorProbe accelerometerSensorProbe;
    private ActivityProbe activityProbe;
    private AndroidInfoProbe androidInfoProbe;
    private AudioFeaturesProbe audioFeaturesProbe;
    private AudioMediaProbe audioMediaProbe;
    private BluetoothProbe bluetoothProbe;
    private BrowserSearchesProbe browserSearchesProbe;
    private CallLogProbe callLogProbe;
    private ContactProbe contactProbe;
    private GravitySensorProbe gravitySensorProbe;
    private GyroscopeSensorProbe gyroscopeSensorProbe;
    private HardwareInfoProbe hardwareInfoProbe;
    private ImageMediaProbe imageMediaProbe;
    private LightSensorProbe lightSensorProbe;
    private LinearAccelerationSensorProbe linearAccelerationSensorProbe;
    //private NotificationProbe notificationProbe;
    private MagneticFieldSensorProbe magneticFieldSensorProbe;
    private OrientationSensorProbe orientationSensorProbe;
    private ProcessStatisticsProbe processStatisticsProbe;
    private ProximitySensorProbe proximitySensorProbe;
    private RotationVectorSensorProbe rotationVectorSensorProbe;
    private RunningApplicationsProbe runningApplicationsProbe;
    private ScreenProbe screenProbe;
    private ServicesProbe servicesProbe;
    private SimpleLocationProbe locationProbe;
    private SmsProbe smsProbe;
    private TelephonyProbe telephonyProbe;
    private TemperatureSensorProbe temperatureSensorProbe;
    private VideoMediaProbe videoMediaProbe;
    private WifiProbe wifiProbe;

    private CheckBox enabledCheckbox;
    private Button archiveButton, scanNowButton;
    private TextView dataCountView;
    private Handler handler;

    private ServiceConnection funfManagerConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            //---------------------------------------------------------------------------------------
            //final PackageManager pm = getPackageManager();
//get a list of installed apps.
            /*List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

            for (ApplicationInfo packageInfo : packages) {
                Log.wtf(TAG, "Installed package :" + packageInfo.packageName);
                Log.wtf(TAG, "Source dir : " + packageInfo.sourceDir);
                Log.wtf(TAG, "Class name: " + packageInfo.className);
                Log.wtf(TAG, "Application label: " + pm.getApplicationLabel(packageInfo).toString());
                Log.wtf(TAG, "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));
            }*/
// the getLaunchIntentForPackage returns an intent that you can use with startActivity()
            //--------------------------------------------------------------------------------------

            funfManager = ((FunfManager.LocalBinder) service).getManager();
            Gson gson = funfManager.getGson();
            //accelerometerFeaturesProbe = gson.fromJson(new JsonObject(), AccelerometerFeaturesProbe.class);
            accelerometerSensorProbe = gson.fromJson(new JsonObject(), AccelerometerSensorProbe.class);
            //activityProbe = gson.fromJson(new JsonObject(), ActivityProbe.class);
            //androidInfoProbe = gson.fromJson(new JsonObject(), AndroidInfoProbe.class);
            //audioFeaturesProbe = gson.fromJson(new JsonObject(), AudioFeaturesProbe.class);
            //audioMediaProbe = gson.fromJson(new JsonObject(), AudioMediaProbe.class);
            //bluetoothProbe = gson.fromJson(new JsonObject(), BluetoothProbe.class);
            //browserSearchesProbe = gson.fromJson(new JsonObject(), BrowserSearchesProbe.class);
            //callLogProbe = gson.fromJson(new JsonObject(), CallLogProbe.class);
            //contactProbe = gson.fromJson(new JsonObject(), ContactProbe.class);
            //gravitySensorProbe = gson.fromJson(new JsonObject(), GravitySensorProbe.class);
            //gyroscopeSensorProbe = gson.fromJson(new JsonObject(), GyroscopeSensorProbe.class);
            //hardwareInfoProbe = gson.fromJson(new JsonObject(), HardwareInfoProbe.class);
            //imageMediaProbe = gson.fromJson(new JsonObject(), ImageMediaProbe.class);
            //lightSensorProbe = gson.fromJson(new JsonObject(), LightSensorProbe.class);
            //linearAccelerationSensorProbe = gson.fromJson(new JsonObject(), LinearAccelerationSensorProbe.class);
            locationProbe = gson.fromJson(new JsonObject(), SimpleLocationProbe.class);
            //magneticFieldSensorProbe = gson.fromJson(new JsonObject(), MagneticFieldSensorProbe.class);
            //notificationProbe = gson.fromJson(new JsonObject(), NotificationProbe.class);
            //orientationSensorProbe = gson.fromJson(new JsonObject(), OrientationSensorProbe.class);
            //processStatisticsProbe = gson.fromJson(new JsonObject(), ProcessStatisticsProbe.class);
            //proximitySensorProbe = gson.fromJson(new JsonObject(), ProximitySensorProbe.class);
            //rotationVectorSensorProbe = gson.fromJson(new JsonObject(), RotationVectorSensorProbe.class);
            //runningApplicationsProbe = gson.fromJson(new JsonObject(), RunningApplicationsProbe.class);
            //screenProbe = gson.fromJson(new JsonObject(), ScreenProbe.class);
            //servicesProbe = gson.fromJson(new JsonObject(), ServicesProbe.class);
            //smsProbe = gson.fromJson(new JsonObject(), SmsProbe.class);
            //telephonyProbe = gson.fromJson(new JsonObject(), TelephonyProbe.class);
            //temperatureSensorProbe = gson.fromJson(new JsonObject(), TemperatureSensorProbe.class);
            //videoMediaProbe = gson.fromJson(new JsonObject(), VideoMediaProbe.class);
            //wifiProbe = gson.fromJson(new JsonObject(), WifiProbe.class);

            pipeline = (BasicPipeline) funfManager.getRegisteredPipeline(PIPELINE_NAME);

            //accelerometerFeaturesProbe.registerPassiveListener(MainActivity.this);
            accelerometerSensorProbe.registerPassiveListener(MainActivity.this);
            //activityProbe.registerPassiveListener(MainActivity.this);
            //androidInfoProbe.registerPassiveListener(MainActivity.this);
            //audioFeaturesProbe.registerPassiveListener(MainActivity.this);
            //audioMediaProbe.registerPassiveListener(MainActivity.this);
            //bluetoothProbe.registerPassiveListener(MainActivity.this);
            //browserSearchesProbe.registerPassiveListener(MainActivity.this);
            //callLogProbe.registerPassiveListener(MainActivity.this);
            //contactProbe.registerPassiveListener(MainActivity.this);
            //gravitySensorProbe.registerPassiveListener(MainActivity.this);
            //gyroscopeSensorProbe.registerPassiveListener(MainActivity.this);
            //hardwareInfoProbe.registerPassiveListener(MainActivity.this);
            //imageMediaProbe.registerPassiveListener(MainActivity.this);
            //lightSensorProbe.registerPassiveListener(MainActivity.this);
            //linearAccelerationSensorProbe.registerPassiveListener(MainActivity.this);
            locationProbe.registerPassiveListener(MainActivity.this);
            //magneticFieldSensorProbe.registerPassiveListener(MainActivity.this);
            //notificationProbe.registerPassiveListener(MainActivity.this);
            //orientationSensorProbe.registerPassiveListener(MainActivity.this);
            //processStatisticsProbe.registerPassiveListener(MainActivity.this);
            //proximitySensorProbe.registerPassiveListener(MainActivity.this);
            //rotationVectorSensorProbe.registerPassiveListener(MainActivity.this);
            //runningApplicationsProbe.registerPassiveListener(MainActivity.this);
            //screenProbe.registerPassiveListener(MainActivity.this);
            //servicesProbe.registerPassiveListener(MainActivity.this);
            //smsProbe.registerPassiveListener(MainActivity.this);
            //telephonyProbe.registerPassiveListener(MainActivity.this);
            //temperatureSensorProbe.registerPassiveListener(MainActivity.this);
            //videoMediaProbe.registerPassiveListener(MainActivity.this);
            //wifiProbe.registerPassiveListener(MainActivity.this);

            // This checkbox enables or disables the pipeline
            enabledCheckbox.setChecked(pipeline.isEnabled());
            enabledCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (funfManager != null) {
                        if (isChecked) {
                            funfManager.enablePipeline(PIPELINE_NAME);
                            pipeline = (BasicPipeline) funfManager.getRegisteredPipeline(PIPELINE_NAME);
                        } else {
                            funfManager.disablePipeline(PIPELINE_NAME);
                        }
                    }
                }
            });

            // Set UI ready to use, by enabling buttons
            updateScanCount();
            enabledCheckbox.setEnabled(true);
            archiveButton.setEnabled(true);
            scanNowButton.setEnabled(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            funfManager = null;
        }
    };

    //onCreate is the rendering of the main page
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // Displays the count of rows in the data
        dataCountView = (TextView) findViewById(R.id.dataCountText);

        // Used to make interface changes on main thread
        handler = new Handler();

        //will be enabled in the onServiceConnected method
        enabledCheckbox = (CheckBox) findViewById(R.id.checkBox);
        enabledCheckbox.setEnabled(false);

        // Runs an archive if pipeline is enabled
        archiveButton = (Button) findViewById(R.id.archiveButtonSD);
        archiveButton.setEnabled(false);
        archiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pipeline.isEnabled()) {
                    pipeline.onRun(BasicPipeline.ACTION_ARCHIVE, null);

                    // Wait 1 second for archive to finish, then refresh the UI
                    // (Note: this is kind of a hack since archiving is seamless and there are no messages when it occurs)
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), "Archived!", Toast.LENGTH_SHORT).show();
                            updateScanCount();
                        }
                    }, 1000L);
                } else {
                    Toast.makeText(getBaseContext(), "Pipeline is not enabled.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Forces the pipeline to scan now
        scanNowButton = (Button) findViewById(R.id.scanNowButton);
        scanNowButton.setEnabled(false);
        scanNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pipeline.isEnabled()) {
                    // Manually register the pipeline for each probe
                    //accelerometerFeaturesProbe.registerListener(pipeline);
                    accelerometerSensorProbe.registerListener(pipeline);
                    //activityProbe.registerListener(pipeline);
                    //androidInfoProbe.registerListener(pipeline);
                    //audioFeaturesProbe.registerListener(pipeline);
                    //audioMediaProbe.registerListener(pipeline);
                    //bluetoothProbe.registerListener(pipeline);
                    //browserSearchesProbe.registerListener(pipeline);
                    //callLogProbe.registerListener(pipeline);
                    //contactProbe.registerListener(pipeline);
                    //gravitySensorProbe.registerListener(pipeline);
                    //gyroscopeSensorProbe.registerListener(pipeline);
                    //hardwareInfoProbe.registerListener(pipeline);
                    //imageMediaProbe.registerListener(pipeline);
                    //lightSensorProbe.registerListener(pipeline);
                    //linearAccelerationSensorProbe.registerListener(pipeline);
                    locationProbe.registerListener(pipeline);
                    //magneticFieldSensorProbe.registerListener(pipeline);
                    //notificationProbe.registerListener(pipeline);
                    //orientationSensorProbe.registerListener(pipeline);
                    //processStatisticsProbe.registerListener(pipeline);
                    //proximitySensorProbe.registerListener(pipeline);
                    //rotationVectorSensorProbe.registerListener(pipeline);
                    //runningApplicationsProbe.registerListener(pipeline);
                    //screenProbe.registerListener(pipeline);
                    //servicesProbe.registerListener(pipeline);
                    //smsProbe.registerListener(pipeline);
                    //telephonyProbe.registerListener(pipeline);
                    //temperatureSensorProbe.registerListener(pipeline);
                    //videoMediaProbe.registerListener(pipeline);
                    //wifiProbe.registerListener(pipeline);
                } else {
                    Toast.makeText(getBaseContext(), "Pipeline is not enabled.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Bind to the service, to create the connection with FunfManager
        bindService(new Intent(this, FunfManager.class), funfManagerConn, BIND_AUTO_CREATE);
    }

    @Override
    public void onDataReceived(IJsonObject iJsonObject, IJsonObject iJsonObject2) {

    }

    @Override
    public void onDataCompleted(IJsonObject probeConfig, JsonElement checkpoint) {
        //updateScanCount();
        // Re-register to keep listening after probe completes.
        //accelerometerFeaturesProbe.registerPassiveListener(this);
        accelerometerSensorProbe.registerPassiveListener(this);
        //activityProbe.registerPassiveListener(this);
        //androidInfoProbe.registerPassiveListener(this);
        //audioFeaturesProbe.registerPassiveListener(this);
        //audioMediaProbe.registerPassiveListener(this);
        //bluetoothProbe.registerPassiveListener(this);
        //browserSearchesProbe.registerPassiveListener(this);
        //callLogProbe.registerPassiveListener(this);
        //contactProbe.registerPassiveListener(this);
        //gravitySensorProbe.registerPassiveListener(this);
        //gyroscopeSensorProbe.registerPassiveListener(this);
        //hardwareInfoProbe.registerPassiveListener(this);
        //imageMediaProbe.registerPassiveListener(this);
        //lightSensorProbe.registerPassiveListener(this);
        //linearAccelerationSensorProbe.registerPassiveListener(this);
        locationProbe.registerPassiveListener(this);
        //magneticFieldSensorProbe.registerPassiveListener(this);
        //notificationProbe.registerPassiveListener(this);
        //orientationSensorProbe.registerPassiveListener(this);
        //processStatisticsProbe.registerPassiveListener(this);
        //proximitySensorProbe.registerPassiveListener(this);
        //rotationVectorSensorProbe.registerPassiveListener(this);
        //runningApplicationsProbe.registerPassiveListener(this);
        //screenProbe.registerPassiveListener(this);
        //securityProbe.registerPassiveListener(this);
        //servicesProbe.registerPassiveListener(this);
        //smsProbe.registerPassiveListener(this);
        //telephonyProbe.registerPassiveListener(this);
        //temperatureSensorProbe.registerPassiveListener(this);
        //videoMediaProbe.registerPassiveListener(this);
        //wifiProbe.registerPassiveListener(this);
    }

    private static final String TOTAL_COUNT_SQL = "select count(*) from " + NameValueDatabaseHelper.DATA_TABLE.name;

    private void updateScanCount() {
        // Query the pipeline db for the count of rows in the data table
        //SQLiteDatabase db = SQLiteDatabase.
                //pipeline.getDb();
        //Cursor mCursor = db.rawQuery(TOTAL_COUNT_SQL, null);
        //mCursor.moveToFirst();
        //final int count = mCursor.getInt(0);
        // Update interface on main thread
        //runOnUiThread(new Runnable() {
            //@Override
            //public void run() {
            //    dataCountView.setText("Data Count: " + count);
            //}
        //});
    }

    /**
     * When the button to send a broadcast is clicked certain
     * extras are set to store the broadcast as a funf db
     * this broadcast is getting send then
     * @param view
     */
    public void broadcastIntent(View view)
    {
        //Intent intent = new Intent();
        //intent.setAction("com.example.SendBroadcast");
        //intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        Intent i = new Intent("com.example.SendBroadcast");
        i.putExtra("notification_event", "selfTriggered");
        i.putExtra("notification_trigger", "Button");
        i.putExtra("notification_message", "Intent caught.");
        i.putExtra("timestamp", System.currentTimeMillis());
        sendBroadcast(i);
    }

    /**
     * A new notification is created in the statusbar.
     * Here you can enter the title, its text and its ticker.
     * The set icon will appear in the statusbar.
     * @param view
     */
    public void createNotification(View view) {
        NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder ncomp = new NotificationCompat.Builder(this);
        ncomp.setContentTitle("My Notification");
        ncomp.setContentText("Notification Listener Service Example");
        ncomp.setTicker("Notification Listener Service Example");
        ncomp.setSmallIcon(R.drawable.ic_launcher);
        ncomp.setAutoCancel(true);
        nManager.notify((int)System.currentTimeMillis(),ncomp.build());
    }
}