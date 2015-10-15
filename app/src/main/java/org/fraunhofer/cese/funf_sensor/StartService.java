package org.fraunhofer.cese.funf_sensor;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 *
 */
public class StartService extends IntentService {

    public StartService(){
        super("");
    }

    public StartService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("StartService: ", "called.");



        Log.i("StartService: ", "finished.");
    }
}
