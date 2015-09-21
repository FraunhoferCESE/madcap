package org.fraunhofer.cese.funf_sensor.appengine;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListAdapter;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.gson.JsonElement;

import org.fraunhofer.cese.funf_sensor.backend.models.messageApi.MessageApi;
import org.fraunhofer.cese.funf_sensor.backend.models.messageApi.model.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import edu.mit.media.funf.FunfManager;
import edu.mit.media.funf.json.IJsonObject;
import edu.mit.media.funf.pipeline.Pipeline;
import edu.mit.media.funf.probe.Probe;

public class GoogleAppEnginePipeline implements Pipeline, Probe.DataListener {

    private static final String TAG = GoogleAppEnginePipeline.class.getSimpleName();

    private boolean enabled = false;

    private MessageApi appEngineApi;

    private class ListOfMessagesAsyncSender extends AsyncTask<Message,Void, Message> {


        @Override
        protected Message doInBackground(final Message... messages) {
            if (appEngineApi == null) {  // Only do this once
                MessageApi.Builder builder = new MessageApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        // options for running against local devappserver
                        // - 10.0.2.2 is localhost's IP address in Android emulator
                        // - turn off compression when running against local devappserver
                        .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });
                // end options for devappserver
                appEngineApi = builder.build();
            }

                for (Message message : messages) {
                    try {
                        appEngineApi.insert(message).execute();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            return messages[0];
            }

    }


    /**
     * Called when the probe emits data. Data emitted from probes that
     * extend the Probe class are guaranteed to have the PROBE and TIMESTAMP
     * parameters.
     *
     */
    @Override
    public void onDataReceived(IJsonObject iJsonObject, IJsonObject iJsonObject1) {
        // This is the method to write data received from a probe. This should probably be handled in a separate thread.

        // The code below is copied from the funf BasicPipline class, which is the default inplementation
        // THIS IS INCOMPLETE EXAMPLE CODE ONLY AND WILL NOT WORK. It is only here for reference.
        // This code shows how to create the data to save, but we want to save to the Google App Engine and not a SQLiteDatabase

//        final String key = probeConfig.get(RuntimeTypeAdapterFactory.TYPE).toString();
//        final IJsonObject finalData = data;
//        if (key == null || data == null)
//            return;
//
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        final double timestamp = data.get(ProbeKeys.BaseProbeKeys.TIMESTAMP).getAsDouble();
//        final String value = data.toString();
//        if (timestamp == 0L || key == null || value == null) {
//            Log.e(LogUtil.TAG, "Unable to save data.  Not all required values specified. " + timestamp + " " + key + " - " + value);
//            throw new SQLException("Not all required fields specified.");
//        }
//        ContentValues cv = new ContentValues();
//        cv.put(NameValueDatabaseHelper.COLUMN_NAME, key);
//        cv.put(NameValueDatabaseHelper.COLUMN_VALUE, value);
//        cv.put(NameValueDatabaseHelper.COLUMN_TIMESTAMP, timestamp);
//        db.insertOrThrow(NameValueDatabaseHelper.DATA_TABLE.name, "", cv);

        //bundle
        //compress
        //send
        Log.i(TAG, "GoogleAppEnginePipeline.onDataRecieved was called!!!!!");
        new ListOfMessagesAsyncSender().execute(new Message());

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
