package org.fraunhofer.cese.funf_sensor.appengine;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.JsonElement;

import org.fraunhofer.cese.funf_sensor.backend.MyBean;

import edu.mit.media.funf.FunfManager;
import edu.mit.media.funf.config.RuntimeTypeAdapterFactory;
import edu.mit.media.funf.json.IJsonObject;
import edu.mit.media.funf.pipeline.Pipeline;
import edu.mit.media.funf.probe.Probe;
import edu.mit.media.funf.probe.builtin.ProbeKeys;
import edu.mit.media.funf.storage.NameValueDatabaseHelper;
import edu.mit.media.funf.util.LogUtil;

/**
 * Created by llayman on 9/16/2015.
 */
public class GoogleAppEnginePipeline implements Pipeline, Probe.DataListener {
    private boolean enabled = false;

    /**
     * Called when the probe emits data. Data emitted from probes that
     * extend the Probe class are guaranteed to have the PROBE and TIMESTAMP
     * parameters.
     *
     * @param data
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
        MyBean
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
    @Override
    public void onDataCompleted(IJsonObject iJsonObject, JsonElement jsonElement) {


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
    @Override
    public void onCreate(FunfManager funfManager) {
        // This is the setup method that's called when the Pipeline is created

        this.enabled = true;
    }

    /**
     * Instructs pipeline to perform an operation.
     *
     * @param action The action to perform.
     * @param config The object to perform the action on.
     */
    @Override
    public void onRun(String s, JsonElement jsonElement) {
        // Method which is called to tell the Pipeline to do something, like save the data locally or upload to the cloud
    }

    /**
     * The teardown method called once when the pipeline should shut down.
     */
    @Override
    public void onDestroy() {
        // Any closeout or disconnect operations

        this.enabled = false;
    }

    /**
     * Returns true if this pipeline is enabled, meaning onCreate has been called
     * and onDestroy has not yet been called.
     */
    @Override
    public boolean isEnabled() {
        // Determines whether the pipeline is enabled. The "enabled" flag should be toggled in the OnCreate and OnDestroy operations

        return enabled;
    }
}
