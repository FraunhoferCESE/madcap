package org.fraunhofer.cese.funf_sensor;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.fraunhofer.cese.funf_sensor.Probe.CallStateProbe.CallStateProbe;
import org.fraunhofer.cese.funf_sensor.Probe.ForegroundProbe.ForegroundProbe;
import org.fraunhofer.cese.funf_sensor.Probe.GPSProbe.Constants;
import org.fraunhofer.cese.funf_sensor.Probe.GPSProbe.GPSManager;
import org.fraunhofer.cese.funf_sensor.Probe.RunningApplicationsProbe.MyRunningApplicationsProbe;
import org.fraunhofer.cese.funf_sensor.Probe.SMSProbe.SMSProbe;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import edu.mit.media.funf.FunfManager;
import edu.mit.media.funf.json.IJsonObject;
import edu.mit.media.funf.pipeline.BasicPipeline;
import edu.mit.media.funf.probe.Probe.DataListener;
import edu.mit.media.funf.probe.builtin.AccelerometerSensorProbe;
import edu.mit.media.funf.probe.builtin.BatteryProbe;
import edu.mit.media.funf.probe.builtin.ScreenProbe;
import edu.mit.media.funf.probe.builtin.SimpleLocationProbe;
import edu.mit.media.funf.storage.NameValueDatabaseHelper;

/**
 * Created by MLang on 19.12.2014.
 */
public class MainActivity extends Activity implements DataListener {
    //all the event listeners have to be defined here

    private static final String TAG = "MainActivity";

    public static final String PIPELINE_NAME = "default";
    private FunfManager funfManager;
    private BasicPipeline pipeline;

    final SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

    //probes
    private AccelerometerSensorProbe accelerometerSensorProbe;
    private BatteryProbe batteryProbe;
    private ForegroundProbe foregroundProbe;
    private MyRunningApplicationsProbe myRunningApplicationsProbe;
    private ScreenProbe screenProbe;
    private SimpleLocationProbe locationProbe;

    private SMSProbe sMSProbe;
    private CallStateProbe callStateProbe;

//    private RunningApplicationsProbe runningApplicationsProbe;

    private CheckBox enabledCheckbox;
    private Button archiveButton;
//    scanNowButton;
    private TextView dataCountView;
    private Handler handler;
    private Handler dataCounterUpdater;

    private String applicationPackageName = "org.fraunhofer.cese.funf_sensor";

    private GPSManager gpsManager = null;
    private double speed = 0.0;
    private int measurement_index = Constants.INDEX_KM;
    private AbsoluteSizeSpan sizeSpanLarge = null;
    private AbsoluteSizeSpan sizeSpanSmall = null;

    private ServiceConnection funfManagerConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            funfManager = ((FunfManager.LocalBinder) service).getManager();
            Gson gson = funfManager.getGson();

            accelerometerSensorProbe = gson.fromJson(new JsonObject(), AccelerometerSensorProbe.class);
            foregroundProbe = gson.fromJson(new JsonObject(), ForegroundProbe.class);
            locationProbe = gson.fromJson(new JsonObject(), SimpleLocationProbe.class);
            myRunningApplicationsProbe = gson.fromJson(new JsonObject(), MyRunningApplicationsProbe.class);
            screenProbe = gson.fromJson(new JsonObject(), ScreenProbe.class);
            sMSProbe = gson.fromJson(new JsonObject(), SMSProbe.class);
            callStateProbe = gson.fromJson(new JsonObject(), CallStateProbe.class);
            batteryProbe = gson.fromJson(new JsonObject(), BatteryProbe.class);
//            runningApplicationsProbe = gson.fromJson(new JsonObject(), RunningApplicationsProbe.class);


            pipeline = (BasicPipeline) funfManager.getRegisteredPipeline(PIPELINE_NAME);

            funfManager.enablePipeline(PIPELINE_NAME);
            registerListeners();

            // This checkbox enables or disables the pipeline

            enabledCheckbox.setChecked(pipeline.isEnabled());
            enabledCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (funfManager != null) {
                        if (isChecked) {
                            funfManager.enablePipeline(PIPELINE_NAME);
                            registerListeners();
                        } else {
                            funfManager.disablePipeline(PIPELINE_NAME);
                            unregisterListeners();
                        }
                    }
                }
            });

            // Set UI ready to use, by enabling buttons
            updateScanCount();
            enabledCheckbox.setEnabled(true);
            archiveButton.setEnabled(true);
//            scanNowButton.setEnabled(true);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            funfManager = null;
        }
    };

    private void registerListeners() {
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("org.fraunhofer.funf.NotificationReceivedBroadcast"));
        accelerometerSensorProbe.registerPassiveListener(pipeline);
        batteryProbe.registerPassiveListener(pipeline);
        foregroundProbe.registerPassiveListener(pipeline);
        locationProbe.registerPassiveListener(pipeline);
        myRunningApplicationsProbe.registerPassiveListener(pipeline);
        screenProbe.registerPassiveListener(pipeline);
        sMSProbe.registerPassiveListener(pipeline);
        callStateProbe.registerPassiveListener(pipeline);

        //notificationProbe.registerPassiveListener(pipeline);
    }

    private void unregisterListeners() {
        accelerometerSensorProbe.unregisterListener(pipeline);
        batteryProbe.unregisterListener(pipeline);
        foregroundProbe.unregisterListener(pipeline);
        locationProbe.unregisterListener(pipeline);
        myRunningApplicationsProbe.unregisterListener(pipeline);
        screenProbe.unregisterListener(pipeline);
        sMSProbe.unregisterListener(pipeline);
        callStateProbe.unregisterListener(pipeline);

//        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);

        //locationProbe.unregisterListener((pipeline));
    }

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
                Log.d(TAG, "Archive requested");
                if (pipeline.isEnabled()) {
                    Log.d(TAG, "Archiving started");
                    pipeline.onRun(BasicPipeline.ACTION_ARCHIVE, null);
                    Log.d(TAG, "Archiving finished");

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

        final int delay = 15000;
        handler.postDelayed(new Runnable() {
            public void run() {
                updateScanCount();
                handler.postDelayed(this, delay);
            }

        },delay);

        // Forces the pipeline to scan now
//        scanNowButton = (Button) findViewById(R.id.scanNowButton);
//        scanNowButton.setEnabled(false);
//        scanNowButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (pipeline.isEnabled()) {
//                    //unregisterListeners();
//                } else {
//                    Toast.makeText(getBaseContext(), "Pipeline is not enabled.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });


        // Bind to the service, to create the connection with FunfManager
        bindService(new Intent(this, FunfManager.class), funfManagerConn, BIND_AUTO_CREATE);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("NotificationService", "Calling probe");
//            notificationProbe.sendData(intent.getExtras());
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterListeners();

        boolean isBound = false;
        isBound = getApplicationContext().bindService( new Intent(getApplicationContext(), FunfManager.class), funfManagerConn, Context.BIND_AUTO_CREATE );
        if(isBound)
            getApplicationContext().unbindService(funfManagerConn);
    }

    @Override
    public void onDataReceived(IJsonObject iJsonObject, IJsonObject iJsonObject2) {

    }

    @Override
    public void onDataCompleted(IJsonObject probeConfig, JsonElement checkpoint) {
        //updateScanCount();
        // Re-register to keep listening after probe completes.
        //accelerometerFeaturesProbe.registerPassiveListener(this);
//        accelerometerSensorProbe.registerPassiveListener(this);
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
//        fullLocationProbe.registerPassiveListener(this);
//        locationProbe.registerPassiveListener(this);
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

//        sdf.setCalendar(new GregorianCalendar());

        SQLiteDatabase db = pipeline.getDb();
        Cursor mCursor = db.rawQuery(TOTAL_COUNT_SQL, null);
        mCursor.moveToFirst();
        final int count = mCursor.getInt(0);
        // Update interface on main thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String text = "Data Count: " + count;
//                text += "\nLast archived: "+ sdf.format(sdf.getCalendar().getTime());
                dataCountView.setText(text);
            }
        });
    }

    /**
     * When the button to send a broadcast is clicked certain
     * extras are set to store the broadcast as a funf db
     * this broadcast is getting send then
     *
     * @param view
     */
//    public void broadcastIntent(View view) {
//        //Intent intent = new Intent();
//        //intent.setAction("com.example.SendBroadcast");
//        //intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
//        Intent i = new Intent("org.fraunhofer.funf.NotificationReceivedBroadcast");
//        i.putExtra("notification_event", "selfTriggered");
//        i.putExtra("notification_trigger", "Button");
//        i.putExtra("notification_message", "Intent caught.");
//        i.putExtra("timestamp", System.currentTimeMillis());
//        sendBroadcast(i);
//    }

    /**
     * A new notification is created in the statusbar.
     * Here you can enter the title, its text and its ticker.
     * The set icon will appear in the statusbar.
     *
     * @param view
     */
//    public void createNotification(View view) {
//        Log.i("NotificationService", "Creating notification");
//        NotificationManager nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        NotificationCompat.Builder ncomp = new NotificationCompat.Builder(this);
//        //Title
//        ncomp.setContentTitle("Funf-Sensor");
//        //Description
//        //ncomp.setContentText("ContentText");
//        //Text that appears in statusbar
//        ncomp.setTicker("Create notification was clicked.");
//        ncomp.setSmallIcon(R.drawable.ic_launcher);
//        ncomp.setAutoCancel(true);
//        nManager.notify((int) System.currentTimeMillis(), ncomp.build());
//    }

//    public void changeNotificationSettings(View view) {
//        Intent i = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
//        startActivity(i);
//        String enabledListeners = android.provider.Settings.Secure.getString(getBaseContext().getContentResolver(),
//                "enabled_notification_listeners");
//        TextView tv = ((TextView) findViewById(R.id.checkNotificationsAllowed));
//        if(enabledListeners.contains(applicationPackageName)){
//            tv.setText("Notifications are enabled. Click here to change.");
//        }
//        else{
//            tv.setText("Notifications are disabled. Click here to change.");
//        }
//    }

}