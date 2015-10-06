package org.fraunhofer.cese.funf_sensor.cache;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

import org.fraunhofer.cese.funf_sensor.backend.models.probeDataSetApi.ProbeDataSetApi;
import org.fraunhofer.cese.funf_sensor.backend.models.probeDataSetApi.model.ProbeDataSet;
import org.fraunhofer.cese.funf_sensor.backend.models.probeDataSetApi.model.ProbeEntry;
import org.fraunhofer.cese.funf_sensor.backend.models.probeDataSetApi.model.ProbeSaveResult;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Nullable;

/**
 * Created by llayman on 9/24/2015.
 */
public class RemoteUploadAsyncTask extends AsyncTask<Void, Void, UploadResult> {
    private static ProbeDataSetApi appEngineApi;
    private static final String TAG = "Fraunhofer." + RemoteUploadAsyncTask.class.getSimpleName();

    private static final String ADDRESS = "http://192.168.0.100:8080/_ah/api/";

    private final Cache cache;

    @Inject
    public RemoteUploadAsyncTask(Cache cache) {
        this.cache = cache;
    }


    @Override
    protected UploadResult doInBackground(Void... params) {
        if (appEngineApi == null) {  // Only do this once
            ProbeDataSetApi.Builder builder = new ProbeDataSetApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    .setApplicationName("funfSensor")
                            // options for running against local devappserver
                            // - 10.0.2.2 is localhost's IP address in Android emulator
                            // - turn off compression when running against local devappserver
                    .setRootUrl(ADDRESS)
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end options for devappserver
            appEngineApi = builder.build();
        }

        UploadResult result = UploadResult.noop();

        List<CacheEntry> entries = cache.getHelper().getDao().queryForAll();
        Log.i(TAG, "Attempting to upload " + entries.size() + " to " + ADDRESS);
        if (entries != null && !entries.isEmpty()) {
            List<ProbeEntry> toUpload = Lists.transform(entries, new Function<CacheEntry, ProbeEntry>() {
                @Nullable
                @Override
                public ProbeEntry apply(CacheEntry cacheEntry) {
                    return CacheEntry.createProbeEntry(cacheEntry);
                }
            });
            ProbeDataSet dataSet = new ProbeDataSet();
            dataSet.setTimestamp(Calendar.getInstance().getTimeInMillis());
            dataSet.setEntryList(toUpload);

            try {
                ProbeSaveResult saveResult = appEngineApi.insertSensorDataSet(dataSet).execute();
                int numSaved = saveResult.getSaved() == null ? 0 : saveResult.getSaved().size();
                int numAlreadyExists = saveResult.getAlreadyExists() == null ? 0 : saveResult.getAlreadyExists().size();

                result = UploadResult.create(saveResult);
                Log.i(TAG, "Upload successful. Saved: " + numSaved + ", Already existed: " + numAlreadyExists);
            } catch (IOException e) {
                result = UploadResult.create(e);
            }
        }

        return result;
    }

    @Override
    protected void onPostExecute(UploadResult result) {
        cache.doPostUpload(result);
    }
}

