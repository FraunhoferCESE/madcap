package org.fraunhofer.cese.funf_sensor.cache;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.inject.Inject;

import org.fraunhofer.cese.funf_sensor.backend.models.probeDataSetApi.ProbeDataSetApi;
import org.fraunhofer.cese.funf_sensor.backend.models.probeDataSetApi.model.ProbeDataSet;
import org.fraunhofer.cese.funf_sensor.backend.models.probeDataSetApi.model.ProbeUploadResult;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by llayman on 9/24/2015.
 */
public class ListOfMessagesAsyncSender extends AsyncTask<ProbeDataSet, Void, Collection<ProbeUploadResult>> {
    private static ProbeDataSetApi appEngineApi;
    private static final String TAG = "Fraunhofer." + ListOfMessagesAsyncSender.class.getSimpleName();

    private final ProbeCache probeCache;

    public ListOfMessagesAsyncSender(ProbeCache cache) {
        this.probeCache = cache;
    }

    @Override
    protected Collection<ProbeUploadResult> doInBackground(final ProbeDataSet... dataSets) {
        if (appEngineApi == null) {  // Only do this once
            ProbeDataSetApi.Builder builder = new ProbeDataSetApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    .setApplicationName("funfSensor")
                            // options for running against local devappserver
                            // - 10.0.2.2 is localhost's IP address in Android emulator
                            // - turn off compression when running against local devappserver
                    .setRootUrl("http://192.168.0.100:8080/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end options for devappserver
            appEngineApi = builder.build();
        }

        Collection<ProbeUploadResult> results = new ArrayList<>();
        for (ProbeDataSet dataSet : dataSets) {
            try {
                ProbeUploadResult result = appEngineApi.insertSensorDataSet(dataSet).execute();
                results.add(result);
                int numSaved = result.getSaved() == null ? 0 : result.getSaved().size();
                int numAlreadyExists = result.getAlreadyExists() == null ? 0 : result.getAlreadyExists().size();

                Log.i(TAG, "Upload successful. Saved: " + numSaved + ", Already exists: " + numAlreadyExists);
            } catch (IOException e) {
                Log.e(TAG, "Error uploading data set to endpoint",e);
            }
        }

        return results;
    }

    @Override
    protected void onPostExecute(Collection<ProbeUploadResult> uploadResults) {
        for (ProbeUploadResult result: uploadResults) {
            try {
                probeCache.removeEntries(result.getAlreadyExists());
                probeCache.removeEntries(result.getSaved());
            } catch (SQLException e) {
                Log.e(TAG, "Error attempting to clean entries from cache", e);
            }

        }
    }
}

