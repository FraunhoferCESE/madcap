package org.fraunhofer.cese.madcap.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


/**
 * Created by MMueller on 10/7/2016.
 */
public class MadcapOnBootService extends Service {
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
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onStart(Intent intent, int startid)
    {
//        Intent intents = new Intent(getBaseContext(),MainActivity.class);
//        intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intents);
        Toast.makeText(this, "On Boot service Started", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStart");
    }
}