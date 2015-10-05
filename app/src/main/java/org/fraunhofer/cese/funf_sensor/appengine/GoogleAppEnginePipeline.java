package org.fraunhofer.cese.funf_sensor.appengine;

import android.util.Log;

import com.google.gson.JsonElement;
import com.google.inject.Inject;


import org.fraunhofer.cese.funf_sensor.backend.models.probeDataSetApi.model.ProbeEntry;
import org.fraunhofer.cese.funf_sensor.cache.CacheEntry;
import org.fraunhofer.cese.funf_sensor.cache.ProbeCache;

import java.sql.SQLException;
import java.util.UUID;


import edu.mit.media.funf.FunfManager;
import edu.mit.media.funf.config.RuntimeTypeAdapterFactory;
import edu.mit.media.funf.json.IJsonObject;
import edu.mit.media.funf.pipeline.Pipeline;
import edu.mit.media.funf.probe.Probe;
import edu.mit.media.funf.probe.builtin.ProbeKeys;

public class GoogleAppEnginePipeline implements Pipeline, Probe.DataListener {

    private static final String TAG = "Fraunhofer."+GoogleAppEnginePipeline.class.getSimpleName();

    private boolean enabled = false;

    @Inject
    ProbeCache probeCache;


    /**
     * Called when the probe emits data. Data emitted from probes that
     * extend the Probe class are guaranteed to have the PROBE and TIMESTAMP
     * parameters.
     *
     */
    @Override
    public void onDataReceived(IJsonObject probeConfig, IJsonObject data){
        // This is the method to write data received from a probe. This should probably be handled in a separate thread.

        final String key = probeConfig.get(RuntimeTypeAdapterFactory.TYPE).toString();
        final IJsonObject finalData = data;

        Log.d(TAG,"(onDataReceived) key: " +key+ ", data: " + finalData);

        if (key == null || data == null) {
            Log.d(TAG, "(onDataReceived) Exiting due to null key or data.");
            return;
        }

        final Long timestamp = data.get(ProbeKeys.BaseProbeKeys.TIMESTAMP).getAsLong();
        if (timestamp == 0L) {
            Log.d(TAG, "Invalid timestamp for probe data: "+timestamp);
            return;
        }

        CacheEntry probeEntry = new CacheEntry();
        probeEntry.setId(UUID.randomUUID().toString());
        probeEntry.setTimestamp(timestamp);
        probeEntry.setProbeType(key);
        probeEntry.setSensorData(finalData.toString());

        try {
            probeCache.add(probeEntry);
        } catch (SQLException e) {
            Log.e(TAG,"Error adding to cache", e);
        }
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
       Log.d(TAG, "(onDataCompleted) completeProbeUri: " + completeProbeUri + ", checkpoint: " + checkpoint);
        probeCache.flush();
    }

    /**
     * Called once when the pipeline is created.  This method can be used
     * to register any scheduled operations.
     *
     * @param manager
     */
    public void onCreate(FunfManager manager) {
        // This is the setup method that's called when the Pipeline is created
        Log.d(TAG, "(onCreate)");
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
        Log.d(TAG, "(onRun)");
    }

    /**
     * The teardown method called once when the pipeline should shut down.
     */
    public void onDestroy() {
        // Any closeout or disconnect operations
        Log.d(TAG, "onDestroy");
        probeCache.close();
        this.enabled = false;
    }

    /**
     * Returns true if this pipeline is enabled, meaning onCreate has been called
     * and onDestroy has not yet been called.
     */
    public boolean isEnabled() {
        // Determines whether the pipeline is enabled. The "enabled" flag should be toggled in the OnCreate and OnDestroy operations
        Log.d(TAG, "(isEnabled:" +enabled+")");
        return enabled;
    }
}
