package org.fraunhofer.cese.madcap.util;

import android.app.Application;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.UUID;

import javax.inject.Inject;

import edu.umd.fcmd.sensorlisteners.model.Probe;


/**
 * Created by MMueller on 12/13/2016.
 * <p>
 * A manual probe uploader for probes which are independent from
 * the other probes like DataCollectionProbe.
 */
public class ManualProbeUploadTask extends AsyncTask<Probe, Void, Void> {
    private final String TAG = getClass().getSimpleName();

    private final Application application;
    private final CacheFactory cacheFactory;

    @Inject
    AuthenticationProvider authenticationProvider;

    @Inject
    EndpointApiBuilder endpointApiBuilder;

    public ManualProbeUploadTask(Application application, Cache cache) {
        this.application = application;
        //noinspection CastToConcreteClass
        ((MyApplication) application).getComponent().inject(this);

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
    @Nullable
    @Override
    protected Void doInBackground(Probe... params) {
        Probe probe = params[0];

        GoogleSignInAccount user = authenticationProvider.getUser();
        if (user == null) {
            user = authenticationProvider.getLastLoggedInUser();
        }

        if (user == null) {
            MyApplication.madcapLogger.e(TAG, "No user available for sending manual probe.");
            FirebaseCrash.report(new Exception("Attempt to send a manual probe with a null user. Probe: " + probe));
            return null;
        }

        ProbeEntry probeEntry = new ProbeEntry();
        probeEntry.setId(UUID.randomUUID().toString());
        probeEntry.setTimestamp(probe.getDate());
        probeEntry.setUserID(user.getId());
        probeEntry.setProbeType(probe.getType());
        probeEntry.setSensorData(probe.toString());

        ProbeDataSet dataSet = new ProbeDataSet();
        dataSet.setTimestamp(Calendar.getInstance().getTimeInMillis());
        dataSet.setEntryList(new ArrayList<>(Collections.singletonList(probeEntry)));


        try {
            ProbeEndpoint appEngineApi = endpointApiBuilder.build(application);
            ProbeSaveResult remoteResult = appEngineApi.insertProbeDataset(dataSet).execute();

            if ((remoteResult.getSaved() != null) && (remoteResult.getSaved().size() == 1)) {
                MyApplication.madcapLogger.d(TAG, "Manual upload succeeded: " + probeEntry);
            }

            if (remoteResult.getAlreadyExists() != null) {
                MyApplication.madcapLogger.i(TAG, "Manual upload failed. Probe already exits: " + probeEntry);
            }
        } catch (IOException e) {
            MyApplication.madcapLogger.w(TAG, e.toString());
            MyApplication.madcapLogger.w(TAG, "Manual upload failed. Saving to cache: " + probeEntry);
            cacheFactory.save(probe);
        }

        return null;
    }
}
