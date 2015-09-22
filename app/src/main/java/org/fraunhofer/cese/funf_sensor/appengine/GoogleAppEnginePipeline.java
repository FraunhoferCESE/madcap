package org.fraunhofer.cese.funf_sensor.appengine;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListAdapter;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.util.DateTime;
import com.google.gson.JsonElement;

import org.fraunhofer.cese.funf_sensor.backend.models.messageApi.MessageApi;

//import org.fraunhofer.cese.funf_sensor.backend.models.messageApi.model.Message;
import org.fraunhofer.cese.funf_sensor.backend.models.sensorDataSetApi.SensorDataSetApi;
import org.fraunhofer.cese.funf_sensor.backend.models.sensorDataSetApi.model.SensorDataSet;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.Date;

import edu.mit.media.funf.FunfManager;
import edu.mit.media.funf.config.RuntimeTypeAdapterFactory;
import edu.mit.media.funf.json.IJsonObject;
import edu.mit.media.funf.pipeline.Pipeline;
import edu.mit.media.funf.probe.Probe;
import edu.mit.media.funf.probe.builtin.ProbeKeys;
import edu.mit.media.funf.util.LogUtil;

public class GoogleAppEnginePipeline implements Pipeline, Probe.DataListener {

    private static final String TAG = GoogleAppEnginePipeline.class.getSimpleName();

    private boolean enabled = false;

    private SensorDataSetApi appEngineApi;

    private class ListOfMessagesAsyncSender extends AsyncTask<SensorDataSet,Void, SensorDataSet> {


        @Override
        protected SensorDataSet doInBackground(final SensorDataSet... sensorDataSets) {
            if (appEngineApi == null) {  // Only do this once
                SensorDataSetApi.Builder builder = new SensorDataSetApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        .setApplicationName("funfSensor")
                        // options for running against local devappserver
                        // - 10.0.2.2 is localhost's IP address in Android emulator
                        // - turn off compression when running against local devappserver
                        .setRootUrl("https://192.168.0.67:8080/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });
                // end options for devappserver
                appEngineApi = builder.build();
            }

                for (SensorDataSet sensorDataSet : sensorDataSets) {
                    try {
                        SensorDataSet sensorDataSet1 = appEngineApi.insertSensorDataSet(sensorDataSet).execute();
                        Log.i(TAG, sensorDataSet1.getSensorData() + "GoogleAppEnginePipeline");
                    } catch (IOException e) {
                        Log.i(TAG, "IOException was caught! GoogleAppEnginePipeline");
                        e.printStackTrace();
                    }
                }

            return sensorDataSets[0];
            }

    }


    /**
     * Called when the probe emits data. Data emitted from probes that
     * extend the Probe class are guaranteed to have the PROBE and TIMESTAMP
     * parameters.
     *
     */
    @Override
    public void onDataReceived(IJsonObject probeConfig, IJsonObject data){
        // This is the method to write data received from a probe. This should probably be handled in a separate thread.

        // The code below is copied from the funf BasicPipline class, which is the default inplementation
        // THIS IS INCOMPLETE EXAMPLE CODE ONLY AND WILL NOT WORK. It is only here for reference.
        // This code shows how to create the data to save, but we want to save to the Google App Engine and not a SQLiteDatabase

        final String key = probeConfig.get(RuntimeTypeAdapterFactory.TYPE).toString();
        final IJsonObject finalData = data;
        if (key == null || data == null)
            return;

        final double timestamp = data.get(ProbeKeys.BaseProbeKeys.TIMESTAMP).getAsDouble();
        final String value = data.toString();
        if (timestamp == 0L || key == null || value == null) {
            Log.e(LogUtil.TAG, "Unable to save data.  Not all required values specified. " + timestamp + " " + key + " - " + value);
            throw new SQLException("Not all required fields specified.");
        }
//        ContentValues cv = new ContentValues();
//        cv.put(NameValueDatabaseHelper.COLUMN_NAME, key);
//        cv.put(NameValueDatabaseHelper.COLUMN_VALUE, value);
//        cv.put(NameValueDatabaseHelper.COLUMN_TIMESTAMP, timestamp);
//        db.insertOrThrow(NameValueDatabaseHelper.DATA_TABLE.name, "", cv);


        //Mockup data
        SensorDataSet sensorData = new SensorDataSet();

        Date date = new Date();
        DateTime dateTime = new DateTime(date);
        sensorData.setTimestamp(dateTime);
        sensorData.setProbeType(key);
        sensorData.setSensorData(value);

        List<SensorDataSet> list = new ArrayList<SensorDataSet>();
        list.add(sensorData);

//        Message message = new Message();
//        message.setListOfSensorData(list);

        //bundle
        //compress
        //send

//        while(true) {
            Log.i(TAG, "GoogleAppEnginePipeline.onDataRecieved was called!!!!!");
            new ListOfMessagesAsyncSender().execute(sensorData);
//           try {
//               Thread.sleep(1000);
//           }catch (InterruptedException e){
//               Log.i(TAG, "INTERRUPTEDeXCEPTION");
//           }
//        }
//        Message message = new Message();
//        Message sent;
//        try {
//            sent = appEngineApi.message().insertMessage(message).execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    /**
     * Called when the probe is finished sending a stream of data. This can
     * be used to know when the probe was run, even if it didn't send data.
     * It can also be used to get a checkpoint of far through the data
     * stream the probe ran. Continuable probes can use this checkpoint to
     * start the data stream where it previously left off.
     *
     * @param completeProbeUri
     * @param checkpoint
     */
    public void onDataCompleted(IJsonObject completeProbeUri, JsonElement checkpoint) {


//        String key = probeConfig.get(RuntimeTypeAdapterFactory.TYPE).toString();
//        Log.d(LogUtil.TAG, "finished writing probe data " + key);
//        setHandler(null); // free system resources as data stream has completed.
    }

    /**
     * Called once when the pipeline is created.  This method can be used
     * to register any scheduled operations.
     *
     * @param manager
     */
    public void onCreate(FunfManager manager) {
        // This is the setup method that's called when the Pipeline is created

        this.enabled = true;
    }

    /**
     * Instructs pipeline to perform an operation.
     *
     * @param action The action to perform.
     * @param config The object to perform the action on.
     */
    public void onRun(String action, JsonElement config) {
        // Method which is called to tell the Pipeline to do something, like save the data locally or upload to the cloud
    }

    /**
     * The teardown method called once when the pipeline should shut down.
     */
    public void onDestroy() {
        // Any closeout or disconnect operations

        this.enabled = false;
    }

    /**
     * Returns true if this pipeline is enabled, meaning onCreate has been called
     * and onDestroy has not yet been called.
     */
    public boolean isEnabled() {
        // Determines whether the pipeline is enabled. The "enabled" flag should be toggled in the OnCreate and OnDestroy operations

        return enabled;
    }
}
