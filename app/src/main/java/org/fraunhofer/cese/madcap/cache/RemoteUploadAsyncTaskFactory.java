package org.fraunhofer.cese.madcap.cache;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.fraunhofer.cese.madcap.backend.probeEndpoint.ProbeEndpoint;

import org.fraunhofer.cese.madcap.backend.probeEndpoint.model.ProbeDataSet;
import org.fraunhofer.cese.madcap.backend.probeEndpoint.model.ProbeEntry;
import org.fraunhofer.cese.madcap.backend.probeEndpoint.model.ProbeSaveResult;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
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
    AsyncTask<Void, Integer, RemoteUploadResult> createRemoteUploadTask(final Context context, final Cache cache, final ProbeEndpoint appEngineApi, final Collection<UploadStatusListener> listeners) {
        return new AsyncTask<Void, Integer, RemoteUploadResult>() {
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
                long numCachedEntries = databaseHelper.getDao().countOf();
                if (numCachedEntries == 0)
                    return result;

                Log.i(TAG, "Attempting to upload " + numCachedEntries + " to " + appEngineApi.getRootUrl());

                ProbeSaveResult saveResult = new ProbeSaveResult();
                saveResult.setSaved(new ArrayList<String>());
                saveResult.setAlreadyExists(new ArrayList<String>());

                databaseHelper.getDao().iterator();
                long offset = 0;
                while (offset < numCachedEntries && result.getException() == null) {
                    long limit = offset + BUFFER_SIZE > numCachedEntries ? numCachedEntries - offset : BUFFER_SIZE;
                    try {
                        List<ProbeEntry> toUpload = Lists.transform(
                                databaseHelper.getDao().queryBuilder().offset(offset).limit(limit).query(),
                                new Function<CacheEntry, ProbeEntry>() {
                                    @Nullable
                                    @Override
                                    public ProbeEntry apply(CacheEntry cacheEntry) {
                                        return CacheEntry.createProbeEntry(cacheEntry);
                                    }
                                });

                        ProbeDataSet dataSet = new ProbeDataSet();
                        dataSet.setTimestamp(Calendar.getInstance().getTimeInMillis());
                        dataSet.setEntryList(toUpload);
                        result.setUploadAttempted(true);

                        ProbeSaveResult remoteResult = appEngineApi.insertProbeDataset(dataSet).execute();
                        if (remoteResult.getSaved() != null) {
                            saveResult.getSaved().addAll(ImmutableList.copyOf(remoteResult.getSaved()));
                        }

                        if (remoteResult.getAlreadyExists() != null) {
                            saveResult.getAlreadyExists().addAll(ImmutableList.copyOf(remoteResult.getAlreadyExists()));
                        }
                        Log.i(TAG, "Uploaded chunk " + ((offset / BUFFER_SIZE) + 1) + " (" + offset + "-" + (offset + BUFFER_SIZE > numCachedEntries ? numCachedEntries : offset + BUFFER_SIZE) + ") - " +
                                "Saved: " + (remoteResult.getSaved() == null ? 0 : remoteResult.getSaved().size()) +
                                ", Already existed: " + (remoteResult.getAlreadyExists() == null ? 0 : remoteResult.getAlreadyExists().size()));
                        offset += BUFFER_SIZE;
                        publishProgress(offset > numCachedEntries ? 100 : Math.round(((float) offset / (float) numCachedEntries) * 100));

                    } catch (IOException | SQLException e) {
                        result.setException(e);
                        Log.w(TAG, "Upload failed", e);
                    }
                }
                result.setSaveResult(saveResult);
                Log.i(TAG, "Upload finished. Saved: " + saveResult.getSaved().size() + " entries, Already existed: " + saveResult.getAlreadyExists().size()
                        + ", Exception: " + (result.getException() != null));


                // Remove uploaded entries from the database
                if (saveResult.getAlreadyExists().isEmpty() && saveResult.getSaved().isEmpty()) {
                    Log.i(TAG, "No save results to remove from database.");
                } else {
                    Log.d(TAG, "Removing entries from database.");
                    @SuppressWarnings("unchecked") int numRemovedEntries = removeIds(databaseHelper, saveResult.getSaved(), saveResult.getAlreadyExists());
                    Log.d(TAG, "Database entries removed: " + numRemovedEntries);
                }

                return result;
            }

            @SafeVarargs
            private final int removeIds(DatabaseOpenHelper databaseHelper, List<String>... lists) {
                int result = 0;
                for (List<String> ids : lists) {
                    if (ids != null && !ids.isEmpty() && databaseHelper.isOpen()) {
                        int cursor = 0;
                        while (cursor < ids.size()) {
                            result += databaseHelper.getDao().deleteIds(ids.subList(cursor, (cursor + BUFFER_SIZE > ids.size() ? ids.size() : cursor + BUFFER_SIZE)));
                            cursor += BUFFER_SIZE;
                        }
                    }
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

            @Override
            protected void onProgressUpdate(Integer... values) {
                if (listeners == null || listeners.isEmpty())
                    return;

                for (UploadStatusListener listener : listeners) {
                    listener.progressUpdate(values[0]);
                }

            }
        };
    }
}
