package org.fraunhofer.cese.funf_sensor;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.fraunhofer.cese.funf_sensor.Probe.CallStateProbe.CallStateProbe;
import org.fraunhofer.cese.funf_sensor.Probe.ForegroundProbe.ForegroundProbe;
import org.fraunhofer.cese.funf_sensor.Probe.GPSProbe.Constants;
import org.fraunhofer.cese.funf_sensor.Probe.GPSProbe.GPSManager;
import org.fraunhofer.cese.funf_sensor.Probe.RunningApplicationsProbe.MyRunningApplicationsProbe;
import org.fraunhofer.cese.funf_sensor.Probe.SMSProbe.SMSProbe;
import org.fraunhofer.cese.funf_sensor.appengine.GoogleAppEnginePipeline;

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
public class MainActivity extends Activity implements
         DataListener,
        GoogleApiClient.ConnectionCallbacks,            //from https://developers.google.com/identity/sign-in/android/start-integrating#declare_permissions 9/28
        GoogleApiClient.OnConnectionFailedListener,     //from https://developers.google.com/identity/sign-in/android/start-integrating#declare_permissions 9/28
        View.OnClickListener {                          //from https://developers.google.com/identity/sign-in/android/start-integrating#declare_permissions 9/28
//    //all the event listeners have to be defined here
//
//    //from https://developers.google.com/identity/sign-in/android/start-integrating#declare_permissions 9/28




    private static final String TAG = "MainActivity";

    public static final String PIPELINE_NAME = "default";
    private FunfManager funfManager;
    private GoogleAppEnginePipeline pipeline;
//    private GoogleAppEnginePipeline appEnginePipeline;

    final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

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


            funfManager.registerPipeline("appengine", new GoogleAppEnginePipeline());
            pipeline = (GoogleAppEnginePipeline) funfManager.getRegisteredPipeline("appengine");
//            appEnginePipeline = (GoogleAppEnginePipeline) funfManager.getRegisteredPipeline("appengine");

            funfManager.enablePipeline(PIPELINE_NAME);
            funfManager.enablePipeline("appengine");
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

        //from https://developers.google.com/identity/sign-in/android/start-integrating#declare_permissions 9/28
        //Build GoogleApiClient with access to basic profile
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();


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

        }, delay);

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


        // Bind to the service, to create the connection with FunfManager+
        startService(new Intent(this, FunfManager.class));
        bindService(new Intent(this, FunfManager.class), funfManagerConn, BIND_AUTO_CREATE);

        //from  https://developers.google.com/identity/sign-in/android/sign-in    Cast hinzugefuegt gegenueber modell
        findViewById(R.id.sign_in_button).setOnClickListener((View.OnClickListener) this);


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
        isBound = getApplicationContext().bindService(new Intent(getApplicationContext(), FunfManager.class), funfManagerConn, Context.BIND_AUTO_CREATE);
        if (isBound)
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

//        SQLiteDatabase db = pipeline.getDb();
//        Cursor mCursor = db.rawQuery(TOTAL_COUNT_SQL, null);
//        mCursor.moveToFirst();
//        final int count = mCursor.getInt(0);
        // Update interface on main thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String text = "Data Count: " + 0;
//                text += "\nLast archived: "+ sdf.format(sdf.getCalendar().getTime());
                dataCountView.setText(text);
            }
        });
    }


    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onStart(){
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop(){
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    // from https://developers.google.com/identity/sign-in/android/sign-in

    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Could not resolve ConnectionResult.", e);
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
                Toast.makeText(MainActivity.this, "There was an error. No Connection", Toast.LENGTH_SHORT).show();
//                showErrorDialog(connectionResult);
            }
        } else {
            // Show the signed-out UI
            Toast.makeText(MainActivity.this, "You are now signed out. Congratulations", Toast.LENGTH_SHORT).show();
//            showSignedOutUI();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_in_button) {
            onSignInClicked();
        }
        if (v.getId() == R.id.SignOutButton) {
            onSignOutClicked();
        }

    }

    private void onSignInClicked() {
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        mShouldResolve = true;
        mGoogleApiClient.connect();

        // Show a message to the user that we are signing in.
        Toast.makeText(MainActivity.this, "You are signing in now", Toast.LENGTH_SHORT).show();
//        mStatus.setText(R.string.signing_in);
    }

    private void onSignOutClicked() {
        //Clear the default account so that GoogleApiClient will not automatically
        //connect in the future
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }
        Toast.makeText(MainActivity.this, "You are signed out.", Toast.LENGTH_SHORT).show();
//        showSignedOutUI();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: " + requestCode + " : " + resultCode + " : " + data);

        if (requestCode == RC_SIGN_IN) {
            //If the error resolution was not succesful we should not resolve further
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        // onConnected indicates that an account was selected on the device, that the selected
        // account has granted any requested permissions to our app and that we were able to
        // establish a service connection to Google Play services.
        Log.d(TAG, "onConnected: " + bundle);
        mShouldResolve = false;

        //show the signed-in UI
        Toast.makeText(MainActivity.this, "You are signed in.", Toast.LENGTH_SHORT).show();
//        showSignedInUI();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    //End of auth-stuff
}