package org.fraunhofer.cese.funf_sensor.appengine;

import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import org.fraunhofer.cese.funf_sensor.backend.models.probeDataSetApi.ProbeDataSetApi;
import org.fraunhofer.cese.funf_sensor.backend.models.probeDataSetApi.model.ProbeDataSet;
import org.fraunhofer.cese.funf_sensor.backend.models.probeDataSetApi.model.UploadResult;

import java.io.IOException;

/**
 * Created by llayman on 9/24/2015.
 */
public class ListOfMessagesAsyncSender extends AsyncTask<ProbeDataSet,Void, ProbeDataSet> {
    ProbeDataSetApi appEngineApi;
    private static final String TAG = GoogleAppEnginePipeline.class.getSimpleName();

    @Override
    protected ProbeDataSet doInBackground(final ProbeDataSet... sensorDataSets) {
        if (appEngineApi == null) {  // Only do this once
            ProbeDataSetApi.Builder builder = new ProbeDataSetApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    .setApplicationName("funfSensor")
                            // options for running against local devappserver
                            // - 10.0.2.2 is localhost's IP address in Android emulator
                            // - turn off compression when running against local devappserver
                    .setRootUrl("http://localhost:8080/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end options for devappserver
            appEngineApi = builder.build();
        }

        for (ProbeDataSet sensorDataSet : sensorDataSets) {
            try {
                UploadResult result = appEngineApi.insertSensorDataSet(sensorDataSet).execute();
                Log.i(TAG, result.toString());
            } catch (IOException e) {
                Log.i(TAG, "IOException was caught! GoogleAppEnginePipeline");
                e.printStackTrace();
            }
        }

        return sensorDataSets[0];
    }


}

