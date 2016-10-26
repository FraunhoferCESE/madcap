package org.fraunhofer.cese.madcap;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;

import org.fraunhofer.cese.madcap.authentification.MadcapAuthManager;
import org.fraunhofer.cese.madcap.services.LoginService;

import static com.pathsense.locationengine.lib.detectionLogic.b.o;
import static com.pathsense.locationengine.lib.detectionLogic.b.t;

public class WelcomeActivity extends AppCompatActivity {
    private final String TAG = "Welcome Activity";
    private MadcapAuthManager madcapAuthManager = MadcapAuthManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApplication.madcapLogger.d(TAG, "Welcome created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

    }

    @Override
    public void onStart(){
        super.onStart();
        MyApplication.madcapLogger.d(TAG, "onStart Welcome");

        if(madcapAuthManager.getLastSignedInUsersName() == null){
            new AsyncTask<Void, Void, String>() {
                protected String doInBackground(Void... params) {
                    // Background Code
                    Intent intent = new Intent(WelcomeActivity.this, LoginService.class);

                    //Sets a flag that the main activity should be shown never mind if
                    //the silent login failed or succeeded.
                    intent.putExtra("ShowMainAnyway", true);
                    MyApplication.madcapLogger.d(TAG, "Start now Login Service");
                    startService(intent);
                    //WelcomeActivity.this.finish();
                    return "message";
                }
                protected void onPostExecute(String msg) {
                    // Post Code
                    // Use `msg` in code
                }
            }.execute();
        }else{
            Intent mainActivityIntent = new Intent(this, MainActivity.class);
            startActivity(mainActivityIntent);
            finish();
        }
    }

}
