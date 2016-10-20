package org.fraunhofer.cese.madcap.appengine;

import android.content.Context;
import org.fraunhofer.cese.madcap.MyApplication;

import com.google.android.gms.iid.InstanceID;
import com.google.gson.JsonElement;

import org.fraunhofer.cese.madcap.MyApplication;
import org.fraunhofer.cese.madcap.cache.Cache;
import org.fraunhofer.cese.madcap.cache.CacheEntry;
import org.fraunhofer.cese.madcap.cache.UploadStatusListener;
import org.fraunhofer.cese.madcap.util.MadcapLogger;

import java.util.UUID;

import javax.inject.Inject;

import edu.mit.media.funf.FunfManager;
import edu.mit.media.funf.config.RuntimeTypeAdapterFactory;
import edu.mit.media.funf.json.IJsonObject;
import edu.mit.media.funf.pipeline.Pipeline;
import edu.mit.media.funf.probe.Probe;
import edu.mit.media.funf.probe.builtin.ProbeKeys;


public class GoogleAppEnginePipeline implements Pipeline, Probe.DataListener {

    private static final String TAG = "Fraunhofer." + GoogleAppEnginePipeline.class.getSimpleName();

    private boolean enabled;

    private final Cache cache;
    private final String instanceId;



    /**
     * The @Inject annotation tells Dagger2 to use this constructor to create new instances of this class.
     */
    @Inject
    public GoogleAppEnginePipeline(Cache cache, Context context) {
        this.cache = cache;
        instanceId = InstanceID.getInstance(context).getId();
    }

    /**
     * Called when the probe emits data. Data emitted from probes that
     * extend the Probe class are guaranteed to have the PROBE and TIMESTAMP
     * parameters.
     */
    @Override
    public void onDataReceived(IJsonObject probeConfig, IJsonObject data) {
        // This is the method to write data received from a probe. This should probably be handled in a separate thread.

        String key = probeConfig.get(RuntimeTypeAdapterFactory.TYPE).toString();


        MyApplication.madcapLogger.d(TAG, "(onDataReceived) key: " + key + ", data: " + data);

        if (key == null || data == null) {
            MyApplication.madcapLogger.d(TAG, "(onDataReceived) Exiting due to null key or data.");
            return;
        }

//        Getting millis as timestamp. If you change that, change it as well within the webapp.
        Double timestampDouble = data.get(ProbeKeys.BaseProbeKeys.TIMESTAMP).getAsDouble() * 1000;
        Long timestamp = timestampDouble.longValue();

        if (timestamp == 0L) {
            MyApplication.madcapLogger.d(TAG, "Invalid timestamp for probe data: " + 0L);
            return;
        }

        CacheEntry probeEntry = new CacheEntry();
        probeEntry.setId(UUID.randomUUID().toString());
        probeEntry.setTimestamp(timestamp);
        probeEntry.setProbeType(key);
        probeEntry.setSensorData(data.toString());
        probeEntry.setUserID(instanceId);

        cache.add(probeEntry);
    }

    /**
     * Called when the probe is finished sending a stream of data. This can
     * be used to know when the probe was run, even if it didn't send data.
     * It can also be used to get a checkpoint of far through the data
     * stream the probe ran. Continuable probes can use this checkpoint to
     * start the data stream where it previously left off.
     *
     * @param completeProbeUri the probe which has finished sending data
     * @param checkpoint       a checkpoint indicating the leave off point for probes that support it
     */
    @Override
    public void onDataCompleted(IJsonObject completeProbeUri, JsonElement checkpoint) {
        MyApplication.madcapLogger.d(TAG, "(onDataCompleted) completeProbeUri: " + completeProbeUri + ", checkpoint: " + checkpoint);
        cache.flush(Cache.UploadStrategy.NORMAL);
    }

    /**
     * Called once when the pipeline is created.  This method can be used
     * to register any scheduled operations.
     *
     * @param manager the FunfManager the pipeline is attached to
     */
    @Override
    public void onCreate(FunfManager manager) {
        MyApplication.madcapLogger.d(TAG, "(onCreate)");
        enabled = true;
    }

    /**
     * Instructs pipeline to perform an operation. This is a requirement of the Pipeline interface, but we don't use it.
     *
     * @param action The action to perform.
     * @param config The object to perform the action on.
     */
    @Override
    public void onRun(String action, JsonElement config) {
        // Method which is called to tell the Pipeline to do something, like save the data locally or upload to the cloud
        MyApplication.madcapLogger.d(TAG, "(onRun)");
    }

    /**
     * Requests an on-demand upload of cached data.
     *
     * @return a status code reflecting whether or not the upload request could be completed. {@link Cache#INTERNAL_ERROR} {@link Cache#UPLOAD_READY} {@link Cache#DATABASE_LIMIT_NOT_MET} {@link Cache#NO_INTERNET_CONNECTION} {@link Cache#UPLOAD_ALREADY_IN_PROGRESS} {@link Cache#UPLOAD_INTERVAL_NOT_MET}
     */
    public int requestUpload() {
        int status = cache.checkUploadConditions(Cache.UploadStrategy.IMMEDIATE);
        if (status == Cache.UPLOAD_READY)
            cache.flush(Cache.UploadStrategy.IMMEDIATE);
        return status;
    }

    /**
     * Sets flag indicating if the pipeline is enabled or not.
     *
     * @param isEnabled the boolean value to set
     */
    public void setEnabled(boolean isEnabled) {
        enabled = isEnabled;
    }

    /**
     * The teardown method called once when the pipeline should shut down.
     */
    @Override
    public void onDestroy() {
        // Any closeout or disconnect operations
        MyApplication.madcapLogger.d(TAG, "onDestroy");
        enabled = false;
        cache.close();
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

    /**
     * Attempts to add an upload status listener to the cache.
     *
     * @param listener the listener to add
     * @see Cache#addUploadListener(UploadStatusListener)
     */
    public void addUploadListener(UploadStatusListener listener) {
        cache.addUploadListener(listener);
    }

    /**
     * Attempts to remove an upload status listener from the cache.
     *
     * @param listener the listener to remove
     * @return {@code true} if the listener was removed, {@code false} otherwise.
     */
    public boolean removeUploadListener(UploadStatusListener listener) {
        return cache.removeUploadListener(listener);
    }

    /**
     * Returns the number of entities currently held in the cache.
     *
     * @return the number of entities in the cache.
     * @see Cache#getSize()
     */
    public long getCacheSize() {
        return cache.getSize();
    }

    /**
     * Should be called when the OS triggers onTrimMemory in the app
     */
    public void onTrimMemory() {
        cache.flush(Cache.UploadStrategy.NORMAL);
    }
}
