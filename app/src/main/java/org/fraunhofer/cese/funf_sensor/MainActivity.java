package org.fraunhofer.cese.funf_sensor;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.inject.Inject;

import org.fraunhofer.cese.funf_sensor.Probe.AccelerometerProbe;
import org.fraunhofer.cese.funf_sensor.Probe.AudioProbe;
import org.fraunhofer.cese.funf_sensor.Probe.BluetoothProbe;
import org.fraunhofer.cese.funf_sensor.Probe.CallStateProbe;
import org.fraunhofer.cese.funf_sensor.Probe.ForegroundProbe;
import org.fraunhofer.cese.funf_sensor.Probe.MyRunningApplicationsProbe;
import org.fraunhofer.cese.funf_sensor.Probe.PowerProbe;
import org.fraunhofer.cese.funf_sensor.Probe.SMSProbe;
import org.fraunhofer.cese.funf_sensor.Probe.StateProbe;
import org.fraunhofer.cese.funf_sensor.appengine.GoogleAppEnginePipeline;
import org.fraunhofer.cese.funf_sensor.cache.Cache;
import org.fraunhofer.cese.funf_sensor.cache.RemoteUploadResult;
import org.fraunhofer.cese.funf_sensor.cache.UploadStatusListener;
import org.fraunhofer.cese.madcap.R;

import java.text.DateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.mit.media.funf.FunfManager;
import edu.mit.media.funf.probe.builtin.ScreenProbe;
import edu.mit.media.funf.probe.builtin.SimpleLocationProbe;
import edu.mit.media.funf.probe.builtin.WifiProbe;
import roboguice.activity.RoboActivity;

public class MainActivity extends RoboActivity {
    private static final String TAG = "Fraunhofer." + MainActivity.class.getSimpleName();
    public static final String PIPELINE_NAME = "appengine";
    private static final String STATE_UPLOAD_STATUS = "uploadStatus";
    private static final String STATE_DATA_COUNT = "dataCount";

    
    private FunfManager funfManager;

    @Inject
    private GoogleAppEnginePipeline pipeline;

    //probes
    private AccelerometerProbe accelerometerProbe;
    private ForegroundProbe foregroundProbe;
    private MyRunningApplicationsProbe myRunningApplicationsProbe;
    private ScreenProbe screenProbe;
    private SimpleLocationProbe locationProbe;
    private WifiProbe wifiProbe;
    private SMSProbe sMSProbe;
    private PowerProbe powerProbe;
    private StateProbe stateProbe;
    private CallStateProbe callStateProbe;
    private AudioProbe audioProbe;
    private BluetoothProbe bluetoothProbe;

    // UI elements
    private TextView dataCountView;
    private TextView uploadResultView;

    private UploadStatusListener uploadStatusListener;
    private AsyncTask<Void, Long, Void> cacheCountUpdater;

    private String uploadResultText;
    private String dataCountText;

    private ServiceConnection funfManagerConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            funfManager = ((FunfManager.LocalBinder) service).getManager();
            Gson gson = funfManager.getGson();

            accelerometerProbe = gson.fromJson(new JsonObject(), AccelerometerProbe.class);
            foregroundProbe = gson.fromJson(new JsonObject(), ForegroundProbe.class);
            locationProbe = gson.fromJson(getString(R.string.probe_location), SimpleLocationProbe.class);
            wifiProbe = gson.fromJson(new JsonObject(), WifiProbe.class);
            myRunningApplicationsProbe = gson.fromJson(new JsonObject(), MyRunningApplicationsProbe.class);
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

            Log.i(TAG, "Enabling pipeline: " + PIPELINE_NAME);
            funfManager.enablePipeline(PIPELINE_NAME);
            pipeline.setEnabled(true);
            registerListeners();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (funfManager != null && pipeline.isEnabled()) {
                Log.d(TAG, "Service disconnected. Disabling pipeline: " + PIPELINE_NAME);
                pipeline.setEnabled(false);
                funfManager.disablePipeline(PIPELINE_NAME);
                unregisterListeners();
            }
            funfManager = null;
        }
    };

    private void registerListeners() {
        accelerometerProbe.registerPassiveListener(pipeline);
        foregroundProbe.registerPassiveListener(pipeline);
        locationProbe.registerPassiveListener(pipeline);
        wifiProbe.registerPassiveListener(pipeline);
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

        accelerometerProbe.unregisterPassiveListener(pipeline);
        foregroundProbe.unregisterPassiveListener(pipeline);
        locationProbe.unregisterPassiveListener(pipeline);
        wifiProbe.unregisterPassiveListener(pipeline);
        myRunningApplicationsProbe.unregisterPassiveListener(pipeline);
        screenProbe.unregisterPassiveListener(pipeline);
        sMSProbe.unregisterPassiveListener(pipeline);
        powerProbe.unregisterPassiveListener(pipeline);
        stateProbe.unregisterPassiveListener(pipeline);
        callStateProbe.unregisterPassiveListener(pipeline);
        audioProbe.unregisterPassiveListener(pipeline);
        bluetoothProbe.unregisterListener(pipeline);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null ) {
            this.dataCountText = savedInstanceState.getString(STATE_DATA_COUNT);
            this.uploadResultText = savedInstanceState.getString(STATE_UPLOAD_STATUS);
        }
        else {
            this.dataCountText = "Data count: 0";
            this.uploadResultText = "";
        }

        setContentView(R.layout.main);
        dataCountView = (TextView) findViewById(R.id.dataCountText);
        uploadResultView = (TextView) findViewById(R.id.uploadResult);
        Switch collectDataSwitch = (Switch) findViewById(R.id.switch1);
        ((TextView) findViewById(R.id.deviceIdText)).setText("Device ID: " + ((TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId());
        collectDataSwitch.setChecked(true);
        collectDataSwitch.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            Log.i(TAG, "Enabling pipeline: " + PIPELINE_NAME);
                            funfManager.enablePipeline(PIPELINE_NAME);
                            pipeline.setEnabled(true);
                            registerListeners();
                        } else {
                            Log.d(TAG, "Disabling pipeline: " + PIPELINE_NAME);
                            unregisterListeners();
                            pipeline.setEnabled(false);
                            funfManager.disablePipeline(PIPELINE_NAME);
                        }
                    }
                }
        );

        Button instantSendButton = (Button) findViewById(R.id.SendButton);
        instantSendButton.setOnClickListener(
                new View.OnClickListener() {
                    private final DateFormat df = DateFormat.getDateTimeInstance();

                    @Override
                    public void onClick(View view) {
                        String text = "Upload requested on " + df.format(new Date()) + "\n";

                        int status = pipeline.requestUpload();
                        if (status == Cache.UPLOAD_READY)
                            text += "Upload started...";
                        else if (status == Cache.UPLOAD_ALREADY_IN_PROGRESS)
                            text += "Upload in progress...";
                        else {
                            String errorText = "";
                            if ((status & Cache.INTERNAL_ERROR) == Cache.INTERNAL_ERROR)
                                errorText += "\n- An internal error occurred and data could not be uploaded.";
                            if ((status & Cache.UPLOAD_INTERVAL_NOT_MET) == Cache.UPLOAD_INTERVAL_NOT_MET)
                                errorText += "\n- An upload was just requested; please wait a few seconds.";
                            if ((status & Cache.NO_INTERNET_CONNECTION) == Cache.NO_INTERNET_CONNECTION)
                                errorText += "\n- No internet connection detected.";
                            if ((status & Cache.DATABASE_LIMIT_NOT_MET) == Cache.DATABASE_LIMIT_NOT_MET)
                                errorText += "\n- No entries to upload";

                            if (!errorText.isEmpty()) {
                                text += "Error:" + errorText;
                            } else {
                                text += "No status to report. Please wait.";
                            }
                        }
                        uploadResultText = text;
                        uploadResultView.setText(uploadResultText);
                    }
                }
        );

        // Bind to the service, to create the connection with FunfManager+
        Log.d(TAG, "Starting FunfManager");
        startService(new Intent(this, FunfManager.class));
        Log.d(TAG, "Binding Funf ServiceConnection to activity");
        getApplicationContext().bindService(new Intent(this, FunfManager.class), funfManagerConn, BIND_AUTO_CREATE);
        pipeline.addUploadListener(getUploadStatusListener());

        getCacheCountUpdater().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected void onStart() {
        super.onStart();
        dataCountView.setText(dataCountText);
        uploadResultView.setText(uploadResultText);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private AsyncTask<Void, Long, Void> getCacheCountUpdater() {
        if (cacheCountUpdater == null) {
            cacheCountUpdater = new AsyncTask<Void, Long, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    while (!isCancelled()) {
                        try {
                            if (pipeline != null && pipeline.isEnabled())
                                publishProgress(pipeline.getCacheSize());
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            Log.i("Fraunhofer.CacheCounter", "Cache counter task to update UI thread has been interrupted.", e);
                        }
                    }
                    return null;
                }

                @Override
                protected void onProgressUpdate(Long... values) {
                    updateDataCount(values[0]);
                }
            };
        }
        return cacheCountUpdater;
    }

    private UploadStatusListener getUploadStatusListener() {
        if (uploadStatusListener == null) {

            uploadStatusListener = new UploadStatusListener() {
                private static final String TAG = "UploadStatusListener";
                private static final String pre = "Last upload attempt: ";
                private final DateFormat df = DateFormat.getDateTimeInstance();

                @Override
                public void uploadFinished(RemoteUploadResult result) {
                    // handle the various options
                    if (uploadResultView == null)
                        return;

                    String text = pre + df.format(new Date()) + "\n";
                    if (result == null) {
                        text += "Result: No upload due to an internal error.";
                    } else if (!result.isUploadAttempted()) {
                        text += "Result: No entries to upload.";
                    } else if (result.getException() != null) {
                        String exceptionText = result.getException().getMessage().length() > 20 ? result.getException().getMessage().substring(0, 19) : result.getException().getMessage();
                        text += "Result: Upload failed due to " + exceptionText;
                    } else if (result.getSaveResult() == null) {
                        text += "Result: An error occurred on the remote server.";
                    } else {
                        text += "Result:\n";
                        text += "\t" + (result.getSaveResult().getSaved() == null ? 0 : result.getSaveResult().getSaved().size()) + " entries saved.";
                        if (result.getSaveResult().getAlreadyExists() != null)
                            text += "\n\t" + result.getSaveResult().getAlreadyExists().size() + " duplicate entries ignored.";
                    }

                    uploadResultText = text;
                    if (uploadResultView.isShown())
                        uploadResultView.setText(uploadResultText);
                    if (pipeline != null && pipeline.isEnabled())
                        updateDataCount(-1);
                    Log.d(TAG, "Upload result received");
                }

                private final Pattern pattern = Pattern.compile("[0-9]+% completed.");

                @Override
                public void progressUpdate(int value) {
                    Matcher matcher = pattern.matcher(uploadResultView.getText());
                    if (matcher.find()) {
                        uploadResultText = matcher.replaceFirst(value + "% completed.");
                    } else {
                        uploadResultText += " " + value + "% completed.";
                    }

                    if (uploadResultView.isShown())
                        uploadResultView.setText(uploadResultText);
                }

                @Override
                public void cacheClosing() {
                    Log.d(TAG, "Cache is closing");
                }
            };
        }
        return uploadStatusListener;
    }

    private void updateDataCount(long count) {
        dataCountText = "Data count: " + ((count < 0) ? "Computing..." : count);

        if (dataCountView != null && dataCountView.isShown()) {
            dataCountView.setText(dataCountText);
        }
    }

    protected void onDestroy() {
        super.onDestroy();

        pipeline.removeUploadListener(getUploadStatusListener());
        AsyncTask.Status status = getCacheCountUpdater().getStatus();
        if (!getCacheCountUpdater().isCancelled() && (status == AsyncTask.Status.PENDING || status == AsyncTask.Status.RUNNING)) {
            getCacheCountUpdater().cancel(true);
        }

        boolean isBound = getApplicationContext().bindService(new Intent(getApplicationContext(), FunfManager.class), funfManagerConn, Context.BIND_AUTO_CREATE);
        if (isBound)
            getApplicationContext().unbindService(funfManagerConn);
        stopService(new Intent(this, FunfManager.class));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_UPLOAD_STATUS, uploadResultText);
        outState.putString(STATE_DATA_COUNT, dataCountText);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        pipeline.onTrimMemory();
    }
}