package org.fraunhofer.cese.madcap.util;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.fraunhofer.cese.madcap.authentication.AuthenticationProvider;
import org.fraunhofer.cese.madcap.backend.probeEndpoint.ProbeEndpoint;
import org.fraunhofer.cese.madcap.backend.probeEndpoint.model.ProbeDataSet;
import org.fraunhofer.cese.madcap.backend.probeEndpoint.model.ProbeEntry;
import org.fraunhofer.cese.madcap.backend.probeEndpoint.model.ProbeSaveResult;
import org.fraunhofer.cese.madcap.cache.CacheFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.UUID;

import javax.inject.Inject;

import edu.umd.fcmd.sensorlisteners.model.Probe;
import timber.log.Timber;


/**
 * A manual probe uploader for probes which are independent from
 * the other probes like DataCollectionProbe.
 */
public class ManualProbeUploadTaskFactory {
    private final String TAG = getClass().getSimpleName();

    private final AuthenticationProvider authenticationProvider;

    private final ProbeEndpoint appEngineApi;

    private final CacheFactory cacheFactory;

    @Inject
    ManualProbeUploadTaskFactory(Context context, CacheFactory cacheFactory, AuthenticationProvider authenticationProvider, EndpointApiBuilder endpointApiBuilder) {
        this.cacheFactory = cacheFactory;
        this.authenticationProvider = authenticationProvider;
        appEngineApi = endpointApiBuilder.build(context);
    }

    public AsyncTask<Probe, Void, Void> create() {
        return new AsyncTask<Probe, Void, Void>() {

            @Nullable
            @Override
            protected Void doInBackground(Probe... params) {
                Probe probe = params[0];

                GoogleSignInAccount user = authenticationProvider.getUser();
                if (user == null) {
                    user = authenticationProvider.getLastLoggedInUser();
                }

                if (user == null) {
                    Timber.e("Attempt to send a manual probe with a null user. Probe: " + probe);
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
                    ProbeSaveResult remoteResult = appEngineApi.insertProbeDataset(dataSet).execute();

                    if ((remoteResult.getSaved() != null) && (remoteResult.getSaved().size() == 1)) {
                        Timber.d("Manual upload succeeded: " + probeEntry);
                    }

                    if (remoteResult.getAlreadyExists() != null) {
                        Timber.i("Manual upload failed. Probe already exits: " + probeEntry);
                    }
                } catch (IOException e) {
                    Timber.w(e.toString());
                    Timber.w("Manual upload failed. Saving to cache: " + probeEntry);
                    cacheFactory.save(probe);
                }

                return null;
            }


        };
    }

}
