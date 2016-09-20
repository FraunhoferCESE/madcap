package org.fraunhofer.cese.madcap;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.iid.InstanceID;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.inject.Inject;

import org.fraunhofer.cese.madcap.Probe.AccelerometerProbe;
import org.fraunhofer.cese.madcap.Probe.ActivityProbe.ActivityProbe;
import org.fraunhofer.cese.madcap.Probe.AudioProbe;
import org.fraunhofer.cese.madcap.Probe.BluetoothProbe;
import org.fraunhofer.cese.madcap.Probe.CallStateProbe;
import org.fraunhofer.cese.madcap.Probe.ForegroundProbe;
import org.fraunhofer.cese.madcap.Probe.NetworkConnectionProbe;
import org.fraunhofer.cese.madcap.Probe.PowerProbe;
import org.fraunhofer.cese.madcap.Probe.RunningApplicationsProbe;
import org.fraunhofer.cese.madcap.Probe.SMSProbe;
import org.fraunhofer.cese.madcap.Probe.StateProbe;
import org.fraunhofer.cese.madcap.appengine.GoogleAppEnginePipeline;
import org.fraunhofer.cese.madcap.cache.Cache;
import org.fraunhofer.cese.madcap.cache.RemoteUploadResult;
import org.fraunhofer.cese.madcap.cache.UploadStatusListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.mit.media.funf.FunfManager;
import edu.mit.media.funf.probe.builtin.ScreenProbe;
import edu.mit.media.funf.probe.builtin.SimpleLocationProbe;
import roboguice.activity.RoboActivity;

public class MainActivity extends RoboActivity {
    private static final String TAG = "Fraunhofer." + MainActivity.class.getSimpleName();
    public static final String PIPELINE_NAME = "appengine";
    private static final String STATE_UPLOAD_STATUS = "uploadStatus";
    private static final String STATE_DATA_COUNT = "dataCount";
    private static final String STATE_COLLECTING_DATA = "isCollectingData";


    private FunfManager funfManager;

    @Inject
    private GoogleAppEnginePipeline pipeline;

    //probes
    private ActivityProbe activityProbe;
    private AccelerometerProbe accelerometerProbe;
    private AudioProbe audioProbe;
    private BluetoothProbe bluetoothProbe;
    private CallStateProbe callStateProbe;
    private ForegroundProbe foregroundProbe;
    private NetworkConnectionProbe networkConnectionProbe;
    private PowerProbe powerProbe;
    private RunningApplicationsProbe runningApplicationsProbe;
    private ScreenProbe screenProbe;
    private SimpleLocationProbe locationProbe;
    private SMSProbe sMSProbe;
    private StateProbe stateProbe;


    // UI elements
    private TextView dataCountView;
    private TextView uploadResultView;

    private UploadStatusListener uploadStatusListener;
    private AsyncTask<Void, Long, Void> cacheCountUpdater;

    private String uploadResultText;
    private String dataCountText;
    private boolean isCollectingData;

    private ServiceConnection funfManagerConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            funfManager = ((FunfManager.LocalBinder) service).getManager();
            Gson gson = funfManager.getGson();

            activityProbe = gson.fromJson(new JsonObject(), ActivityProbe.class);
            accelerometerProbe = gson.fromJson(new JsonObject(), AccelerometerProbe.class);
            audioProbe = gson.fromJson(new JsonObject(), AudioProbe.class);
            bluetoothProbe = gson.fromJson(new JsonObject(), BluetoothProbe.class);
            callStateProbe = gson.fromJson(new JsonObject(), CallStateProbe.class);
            foregroundProbe = gson.fromJson(new JsonObject(), ForegroundProbe.class);
            locationProbe = gson.fromJson(getString(R.string.probe_location), SimpleLocationProbe.class);
            networkConnectionProbe = gson.fromJson(new JsonObject(), NetworkConnectionProbe.class);
            powerProbe = gson.fromJson(new JsonObject(), PowerProbe.class);
            runningApplicationsProbe = gson.fromJson(new JsonObject(), RunningApplicationsProbe.class);
            screenProbe = gson.fromJson(new JsonObject(), ScreenProbe.class);
            sMSProbe = gson.fromJson(new JsonObject(), SMSProbe.class);
            stateProbe = gson.fromJson(new JsonObject(), StateProbe.class);

            // Initialize the pipeline
            funfManager.registerPipeline(PIPELINE_NAME, pipeline);
            pipeline = (GoogleAppEnginePipeline) funfManager.getRegisteredPipeline(PIPELINE_NAME);

            if (isCollectingData)
                enablePipelines();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (funfManager != null && pipeline.isEnabled()) {
                disablePipelines();
            }
            funfManager = null;
        }
    };

    private void disablePipelines() {
        Log.d(TAG, "Disabling pipeline: " + PIPELINE_NAME);

        accelerometerProbe.unregisterPassiveListener(pipeline);
        accelerometerProbe.unregisterPassiveListener(pipeline);
        audioProbe.unregisterPassiveListener(pipeline);
        bluetoothProbe.unregisterListener(pipeline);
        callStateProbe.unregisterPassiveListener(pipeline);
        foregroundProbe.unregisterPassiveListener(pipeline);
        locationProbe.unregisterPassiveListener(pipeline);
        networkConnectionProbe.unregisterPassiveListener(pipeline);
        powerProbe.unregisterPassiveListener(pipeline);
        runningApplicationsProbe.unregisterPassiveListener(pipeline);
        screenProbe.unregisterPassiveListener(pipeline);
        sMSProbe.unregisterPassiveListener(pipeline);
        stateProbe.unregisterPassiveListener(pipeline);


        pipeline.setEnabled(false);
        funfManager.disablePipeline(PIPELINE_NAME);
    }

    private void enablePipelines() {
        Log.i(TAG, "Enabling pipeline: " + PIPELINE_NAME);
        funfManager.enablePipeline(PIPELINE_NAME);
        pipeline.setEnabled(true);

        accelerometerProbe.registerPassiveListener(pipeline);
        accelerometerProbe.registerPassiveListener(pipeline);
        audioProbe.registerPassiveListener(pipeline);
        bluetoothProbe.registerListener(pipeline);
        callStateProbe.registerPassiveListener(pipeline);
        foregroundProbe.registerPassiveListener(pipeline);
        locationProbe.registerPassiveListener(pipeline);
        networkConnectionProbe.registerPassiveListener(pipeline);
        powerProbe.registerPassiveListener(pipeline);
        runningApplicationsProbe.registerPassiveListener(pipeline);
        screenProbe.registerPassiveListener(pipeline);
        sMSProbe.registerPassiveListener(pipeline);
        stateProbe.registerPassiveListener(pipeline);

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            this.dataCountText = savedInstanceState.getString(STATE_DATA_COUNT);
            this.uploadResultText = savedInstanceState.getString(STATE_UPLOAD_STATUS);
            this.isCollectingData = savedInstanceState.getBoolean(STATE_COLLECTING_DATA);
        } else {
            this.dataCountText = "Computing...";
            this.uploadResultText = "None.";
            this.isCollectingData = true;
        }

        setContentView(R.layout.main);
        dataCountView = (TextView) findViewById(R.id.dataCountText);
        uploadResultView = (TextView) findViewById(R.id.uploadResult);
        Switch collectDataSwitch = (Switch) findViewById(R.id.switch1);

        ((TextView) findViewById(R.id.instanceIdText)).setText(getString(R.string.instanceIdText, InstanceID.getInstance(this.getApplicationContext()).getId()));
        collectDataSwitch.setChecked(this.isCollectingData);
        collectDataSwitch.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            isCollectingData = true;
                            enablePipelines();
                        } else {
                            isCollectingData = false;
                            disablePipelines();
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
                        String text = "\nUpload requested on " + df.format(new Date()) + "\n";

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
                                errorText += "\n- No WiFi connection detected.";
                            if ((status & Cache.DATABASE_LIMIT_NOT_MET) == Cache.DATABASE_LIMIT_NOT_MET)
                                errorText += "\n- No entries to upload";

                            text += !errorText.isEmpty() ? "Error:" + errorText : "No status to report. Please wait.";
                        }
                        uploadResultText = text;
                        uploadResultView.setText(getString(R.string.uploadResultText, uploadResultText));
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
        dataCountView.setText(getString(R.string.dataCountText, dataCountText));
        uploadResultView.setText(getString(R.string.uploadResultText, uploadResultText));
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
        outState.putBoolean(STATE_COLLECTING_DATA, isCollectingData);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        pipeline.onTrimMemory();
    }

    private AsyncTask<Void, Long, Void> getCacheCountUpdater() {
        if (cacheCountUpdater == null) {
            cacheCountUpdater = new AsyncTask<Void, Long, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    while (!isCancelled()) {
                        try {
                            if (pipeline != null)
                                publishProgress(pipeline.getCacheSize());
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            Log.i("Fraunhofer.CacheCounter", "Cache counter task to update UI thread has been interrupted.");
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
                private static final String pre = "\nLast upload attempt: ";
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
                        String exceptionMessage;
                        if(result.getException().getMessage() != null)
                            exceptionMessage = result.getException().getMessage();
                        else if(result.getException().toString() != null)
                            exceptionMessage = result.getException().toString();
                        else
                            exceptionMessage = "Unspecified error";

                        text += "Result: Upload failed due to " + (exceptionMessage.length() > 20 ? exceptionMessage.substring(0, 19) : exceptionMessage);
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
                        uploadResultView.setText(getString(R.string.uploadResultText, uploadResultText));
                    if (pipeline != null && pipeline.isEnabled())
                        updateDataCount(-1);
                    Log.d(TAG, "Upload result received");
                }

                private final Pattern pattern = Pattern.compile("[0-9]+% completed.");

                @Override
                public void progressUpdate(int value) {
                    Matcher matcher = pattern.matcher(uploadResultText);
                    if (matcher.find()) {
                        uploadResultText = matcher.replaceFirst(value + "% completed.");
                    } else {
                        uploadResultText += " " + value + "% completed.";
                    }

                    if (uploadResultView.isShown())
                        uploadResultView.setText(getString(R.string.uploadResultText, uploadResultText));
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
        dataCountText = count < 0 ? "Computing..." : Long.toString(count);

        if (dataCountView != null && dataCountView.isShown()) {
            dataCountView.setText(getString(R.string.dataCountText, dataCountText));
        }
    }


}