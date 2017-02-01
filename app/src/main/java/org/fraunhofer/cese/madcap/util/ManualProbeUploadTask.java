package org.fraunhofer.cese.madcap.util;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.google.common.collect.ImmutableList;
import com.google.firebase.crash.FirebaseCrash;

import org.fraunhofer.cese.madcap.MyApplication;
import org.fraunhofer.cese.madcap.authentication.AuthenticationProvider;
import org.fraunhofer.cese.madcap.backend.probeEndpoint.ProbeEndpoint;
import org.fraunhofer.cese.madcap.backend.probeEndpoint.model.ProbeDataSet;
import org.fraunhofer.cese.madcap.backend.probeEndpoint.model.ProbeEntry;
import org.fraunhofer.cese.madcap.backend.probeEndpoint.model.ProbeSaveResult;
import org.fraunhofer.cese.madcap.cache.Cache;
import org.fraunhofer.cese.madcap.cache.CacheFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import edu.umd.fcmd.sensorlisteners.model.Probe;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE;


/**
 * Created by MMueller on 12/13/2016.
 *
 * A manual probe uploader for probes which are independent from
 * the other probes like DataCollectionProbe.
 */
public class ManualProbeUploadTask extends AsyncTask<Probe, Void, Void> {
    private final String TAG = getClass().getSimpleName();

    private Application application;
    private Cache cache;
    private CacheFactory cacheFactory;

    @Inject
    ProbeEndpoint appEngineApi;

    @Inject
    AuthenticationProvider authenticationProvider;

    public ManualProbeUploadTask(Application application, Cache cache){

        ((MyApplication) application).getComponent().inject(this);
        this.cache = cache;

        cacheFactory = new CacheFactory(cache, authenticationProvider);
    }

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param params The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected Void doInBackground(Probe... params) {
        Probe probe = params[0];

        ProbeSaveResult saveResult = new ProbeSaveResult();
        saveResult.setSaved(new ArrayList<String>(1));
        saveResult.setAlreadyExists(new ArrayList<String>(1));

        List<ProbeEntry> toUpload = new LinkedList<>();

        ProbeEntry probeEntry = new ProbeEntry();
        probeEntry.setId(UUID.randomUUID().toString());
        probeEntry.setTimestamp(probe.getDate());
        probeEntry.setUserID(authenticationProvider.getUserId());
        probeEntry.setProbeType(probe.getType());
        probeEntry.setSensorData(probe.toString());

        toUpload.add(probeEntry);

        ProbeDataSet dataSet = new ProbeDataSet();
        dataSet.setTimestamp(Calendar.getInstance().getTimeInMillis());
        dataSet.setEntryList(toUpload);

        ProbeSaveResult remoteResult;

        if(probeEntry.getUserID() != null){
            try {
                remoteResult = appEngineApi.insertProbeDataset(dataSet).execute();

                Log.d(TAG, "Manual upload succeeded");

                if (remoteResult.getSaved() != null) {
                    saveResult.getSaved().addAll(ImmutableList.copyOf(remoteResult.getSaved()));
                }

                if (remoteResult.getAlreadyExists() != null) {
                    saveResult.getAlreadyExists().addAll(ImmutableList.copyOf(remoteResult.getAlreadyExists()));
                }
            } catch (IOException e) {
                Log.d(TAG, "Manual DataCollectionProbe upload failed. Save now to cache.");
                cacheFactory.save(probe);
            }
        }else{
            FirebaseCrash.report(new Exception("Attempt to save an DataCollectionEnty with a null user"));
        }



        return null;
    }
}
