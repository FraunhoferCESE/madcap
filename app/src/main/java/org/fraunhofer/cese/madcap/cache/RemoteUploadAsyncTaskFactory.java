package org.fraunhofer.cese.madcap.cache;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.fraunhofer.cese.madcap.MyApplication;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.fraunhofer.cese.madcap.backend.probeEndpoint.ProbeEndpoint;

import org.fraunhofer.cese.madcap.backend.probeEndpoint.model.ProbeDataSet;
import org.fraunhofer.cese.madcap.backend.probeEndpoint.model.ProbeEntry;
import org.fraunhofer.cese.madcap.backend.probeEndpoint.model.ProbeSaveResult;
import org.fraunhofer.cese.madcap.util.EndpointApiBuilder;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

/**
 * Factory for creating asynchronous remote data storage tasks.
 *
 * @author llayman
 * @see Cache
 */
public class RemoteUploadAsyncTaskFactory {

    /**
     * Default no-arg constructor.
     * The @Inject annotation tells Dagger2 to use this constructor to create new instances of this class.
     */
    @Inject
    RemoteUploadAsyncTaskFactory() {

    }

    /**
     * Creates an asynchronous task for uploading entries from the local database to the remote store. The results of the task
     * are stored in a RemoteUploadResult object.
     *
     * @return a new instance of an asynchronous remote upload task
     * @see RemoteUploadResult
     */
      AsyncTask<Void, Integer, RemoteUploadResult> createRemoteUploadTask(final Context context, final Cache cache, final EndpointApiBuilder endpointApiBuilder, final Collection<UploadStatusListener> listeners) {
        return new AsyncTask<Void, Integer, RemoteUploadResult>() {
            private static final String TAG = "Fraunhofer.UploadTask";
            private static final int BUFFER_SIZE = 250;

            @SuppressWarnings("OverloadedVarargsMethod")
            @Override
            protected RemoteUploadResult doInBackground(Void... params) {
                Log.d(TAG, "DoInBackground started");
                if ((context == null) || (cache == null)){
                    Log.e(TAG, "Cache or context null");
                    return new RemoteUploadResult();
                }


                DatabaseOpenHelper databaseHelper = OpenHelperManager.getHelper(context, DatabaseOpenHelper.class);
                if ((databaseHelper == null) || (databaseHelper.getDao() == null)){
                    Log.e(TAG, "DatabaseHelper or Dao null");
                    return new RemoteUploadResult();
                }

                RemoteUploadResult result = new RemoteUploadResult();
                long numCachedEntries = databaseHelper.getDao().countOf();
                if (numCachedEntries == 0L){
                    Log.e(TAG, "numCache Entries is 0");
                    return result;
                }

                ProbeEndpoint appEngineApi = endpointApiBuilder.build(context);
                MyApplication.madcapLogger.i(TAG, "Attempting to upload " + numCachedEntries + " to " + appEngineApi.getRootUrl());

                ProbeSaveResult saveResult = new ProbeSaveResult();
                saveResult.setSaved(new ArrayList<String>(250));
                saveResult.setAlreadyExists(new ArrayList<String>(100));

                long offset = 0L;
                while ((offset < numCachedEntries) && (result.getException() == null)) {
                    long limit = ((offset + (long) BUFFER_SIZE) > numCachedEntries) ? (numCachedEntries - offset) : (long) BUFFER_SIZE;
                    try {
                        @SuppressWarnings("ParameterNameDiffersFromOverriddenParameter") List<ProbeEntry> toUpload = Lists.transform(
                                databaseHelper.getDao().queryBuilder().offset(offset).limit(limit).query(),
                                new Function<CacheEntry, ProbeEntry>() {
                                    @SuppressWarnings("ParameterNameDiffersFromOverriddenParameter")
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
                        MyApplication.madcapLogger.i(TAG, "Uploaded chunk " + ((offset / (long) BUFFER_SIZE) + 1L) + " (" + offset + '-' + (offset + (long) BUFFER_SIZE > numCachedEntries ? numCachedEntries : offset + (long) BUFFER_SIZE) + ") - " +
                                "Saved: " + (remoteResult.getSaved() == null ? 0 : remoteResult.getSaved().size()) +
                                ", Already existed: " + (remoteResult.getAlreadyExists() == null ? 0 : remoteResult.getAlreadyExists().size()));
                        offset += (long) BUFFER_SIZE;
                        publishProgress((offset > numCachedEntries) ? 100 : Math.round(((float) offset / (float) numCachedEntries) * 100.0F));

                    } catch (IOException | SQLException e) {
                        result.setException(e);
                        MyApplication.madcapLogger.w(TAG, "Upload failed", e);
                    }
                }
                result.setSaveResult(saveResult);
                MyApplication.madcapLogger.i(TAG, "Upload finished. Saved: " + saveResult.getSaved().size() + " entries, Already existed: " + saveResult.getAlreadyExists().size()
                        + ", Exception: " + (result.getException() != null));


                // Remove uploaded entries from the database
                if (saveResult.getAlreadyExists().isEmpty() && saveResult.getSaved().isEmpty()) {
                    MyApplication.madcapLogger.i(TAG, "No save results to remove from database.");
                } else {
                    MyApplication.madcapLogger.d(TAG, "Removing entries from database.");
                    @SuppressWarnings("unchecked") int numRemovedEntries = removeIds(databaseHelper, saveResult.getSaved(), saveResult.getAlreadyExists());
                    MyApplication.madcapLogger.d(TAG, "Database entries removed: " + numRemovedEntries);
                }

                return result;
            }

            @SuppressWarnings("NonBooleanMethodNameMayNotStartWithQuestion")
            @SafeVarargs
            private final int removeIds(DatabaseOpenHelper databaseHelper, List<String>... lists) {
                int result = 0;
                for (List<String> ids : lists) {
                    if ((ids != null) && !ids.isEmpty() && databaseHelper.isOpen()) {
                        int cursor = 0;
                        while (cursor < ids.size()) {
                            result += databaseHelper.getDao().deleteIds(ids.subList(cursor, ((cursor + BUFFER_SIZE) > ids.size()) ? ids.size() : (cursor + BUFFER_SIZE)));
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

            @SuppressWarnings("OverloadedVarargsMethod")
            @Override
            protected void onProgressUpdate(Integer... values) {
                if ((listeners == null) || listeners.isEmpty()) {
                    return;
                }

                for (UploadStatusListener listener : listeners) {
                    listener.progressUpdate(values[0]);
                }

            }
        };
    }
}
