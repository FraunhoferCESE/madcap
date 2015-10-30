package org.fraunhofer.cese.funf_sensor.cache;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.fraunhofer.cese.funf_sensor.backend.models.probeDataSetApi.ProbeDataSetApi;
import org.fraunhofer.cese.funf_sensor.backend.models.probeDataSetApi.model.ProbeDataSet;
import org.fraunhofer.cese.funf_sensor.backend.models.probeDataSetApi.model.ProbeEntry;
import org.fraunhofer.cese.funf_sensor.backend.models.probeDataSetApi.model.ProbeSaveResult;

import java.io.IOException;
import java.util.ArrayList;
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
    AsyncTask<Void, Void, RemoteUploadResult> createRemoteUploadTask(final Context context, final Cache cache, final ProbeDataSetApi appEngineApi) {
        return new AsyncTask<Void, Void, RemoteUploadResult>() {
            private final String TAG = "Fraunhofer.UploadTask";
            private static final int BUFFER_SIZE = 250;

            @Override
            protected RemoteUploadResult doInBackground(Void... params) {
                if (context == null || cache == null)
                    return new RemoteUploadResult();

                DatabaseOpenHelper databaseHelper = OpenHelperManager.getHelper(context, DatabaseOpenHelper.class);
                if (databaseHelper == null || databaseHelper.getDao() == null)
                    return new RemoteUploadResult();

                RemoteUploadResult result = new RemoteUploadResult();
                List<CacheEntry> entries = databaseHelper.getDao().queryForAll();

                if (!entries.isEmpty()) {
                    Log.i(TAG, "Attempting to upload " + entries.size() + " to " + appEngineApi.getRootUrl());

                    ProbeSaveResult saveResult = new ProbeSaveResult();
                    saveResult.setSaved(new ArrayList<String>());
                    saveResult.setAlreadyExists(new ArrayList<String>());

                    List<ProbeEntry> toUpload = Lists.transform(entries, new Function<CacheEntry, ProbeEntry>() {
                        @Nullable
                        @Override
                        public ProbeEntry apply(CacheEntry cacheEntry) {
                            return CacheEntry.createProbeEntry(cacheEntry);
                        }
                    });

                    int cursor = 0;
                    while (cursor < entries.size() && result.getException() == null) {
                        ProbeDataSet dataSet = new ProbeDataSet();
                        dataSet.setTimestamp(Calendar.getInstance().getTimeInMillis());
                        dataSet.setEntryList(toUpload.subList(cursor, (cursor + BUFFER_SIZE > toUpload.size() ? toUpload.size() : cursor + BUFFER_SIZE)));
                        result.setUploadAttempted(true);

                        try {
                            ProbeSaveResult remoteResult = appEngineApi.insertSensorDataSet(dataSet).execute();
                            if (remoteResult.getSaved() != null) {
                                saveResult.getSaved().addAll(ImmutableList.copyOf(remoteResult.getSaved()));
                            }

                            if (remoteResult.getAlreadyExists() != null) {
                                saveResult.getAlreadyExists().addAll(ImmutableList.copyOf(remoteResult.getAlreadyExists()));
                            }
                            Log.i(TAG, "Uploaded chunk " + ((cursor / BUFFER_SIZE) + 1) + " (" + cursor + "-" + (cursor + BUFFER_SIZE > toUpload.size() ? toUpload.size() : cursor + BUFFER_SIZE) + ") - " +
                                    "Saved: " + (remoteResult.getSaved() == null ? 0 : remoteResult.getSaved().size()) +
                                    ", Already existed: " + (remoteResult.getAlreadyExists() == null ? 0 : remoteResult.getAlreadyExists().size()));
                            cursor += BUFFER_SIZE;
                        } catch (IOException e) {
                            result.setException(e);
                            Log.w(TAG, "Upload failed", e);
                        }
                    }
                    result.setSaveResult(saveResult);
                    Log.i(TAG, "Upload finished. Saved: " + saveResult.getSaved().size() + " entries, Already existed: " + saveResult.getAlreadyExists().size()
                            + ", Exception: " + (result.getException() != null));
                }

                return result;
            }

            @Override
            protected void onPostExecute(RemoteUploadResult result) {
                OpenHelperManager.releaseHelper();
                cache.doPostUpload(result);
            }

            @Override
            protected void onCancelled(RemoteUploadResult result) {
                OpenHelperManager.releaseHelper();
                cache.doPostUpload(result);
            }
        };
    }
}
