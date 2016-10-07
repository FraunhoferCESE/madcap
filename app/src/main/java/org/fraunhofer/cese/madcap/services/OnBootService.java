package org.fraunhofer.cese.madcap.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.fraunhofer.cese.madcap.authentification.MadcapAuthManager;


/**
 * Created by MMueller on 10/7/2016.
 */
public class OnBootService extends Service {
    private static final String TAG = "Madcap On Boot Service";
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onDestroy() {
        Toast.makeText(this, "On Boot Service Stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public void onCreate(){
        Log.d(TAG, "onCreate Boot Service");
        Intent intent = new Intent(OnBootService.this, LoginService.class);
        startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Intent intents = new Intent(getBaseContext(),MainActivity.class);
//        intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intents);
        Toast.makeText(this, "Madcap Service is now running in background. Thank you for your participation.", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStart");
        return startId;
    }
}