package org.fraunhofer.cese.funf_sensor.cache;

import android.os.AsyncTask;
import android.util.Log;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import org.fraunhofer.cese.funf_sensor.backend.models.probeDataSetApi.ProbeDataSetApi;
import org.fraunhofer.cese.funf_sensor.backend.models.probeDataSetApi.model.ProbeDataSet;
import org.fraunhofer.cese.funf_sensor.backend.models.probeDataSetApi.model.ProbeEntry;
import org.fraunhofer.cese.funf_sensor.backend.models.probeDataSetApi.model.ProbeSaveResult;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Nullable;

/**
 * Factory for creating asynchronous remote data storage tasks.
 *
 * @author Lucas
 * @see Cache
 */
public class RemoteUploadAsyncTaskFactory {

    /**
     * Creates an asynchronous task for uploading entries from the local database to the remote store. The results of the task
     * are stored in a RemoteUploadResult object.
     *
     * @param cache        the cache object handling the request. Needed for callbacks on write completion.
     * @param appEngineApi the appengine api to which the entries should be posted
     * @return a new instance of an asynchronous remote upload task
     * @see RemoteUploadResult
     */
    AsyncTask<Void, Void, RemoteUploadResult> createRemoteUploadTask(final Cache cache, final ProbeDataSetApi appEngineApi) {
        return new AsyncTask<Void, Void, RemoteUploadResult>() {
            private final String TAG = "Fraunhofer.UploadTask";

            @Override
            protected RemoteUploadResult doInBackground(Void... params) {
                RemoteUploadResult result = null;

                List<CacheEntry> entries = cache.getHelper().getDao().queryForAll();
                if (!entries.isEmpty()) {
                    Log.i(TAG, "Attempting to upload " + entries.size() + " to " + appEngineApi.getRootUrl());
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

                        result = RemoteUploadResult.create(saveResult);
                        Log.i(TAG, "Upload successful. Saved: " + numSaved + " entries, Already existed: " + numAlreadyExists);
                    } catch (IOException e) {
                        result = RemoteUploadResult.create(e);
                    }
                }

                return result == null ? RemoteUploadResult.noop() : result;
            }

            @Override
            protected void onPostExecute(RemoteUploadResult result) {
                cache.doPostUpload(result);
            }
        };
    }
}
