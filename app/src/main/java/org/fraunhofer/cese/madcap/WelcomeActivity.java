package org.fraunhofer.cese.madcap;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.fraunhofer.cese.madcap.authentification.MadcapAuthManager;
import org.fraunhofer.cese.madcap.services.LoginService;

public class WelcomeActivity extends AppCompatActivity {
    private final String TAG = "Welcome Activity";
    private MadcapAuthManager madcapAuthManager = MadcapAuthManager.getInstance();
    private TextView errorTextView;
    private Button helpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApplication.madcapLogger.d(TAG, "Welcome created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        errorTextView = (TextView) findViewById(R.id.welcomeErrorTextView);
        errorTextView.setMovementMethod(LinkMovementMethod.getInstance());

        Button helpButton = (Button) findViewById(R.id.helpButton);
        helpButton.setOnClickListener(new View.OnClickListener(){
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                MyApplication.madcapLogger.d(TAG, "Help toggled");

                String url = "https://www.pocket-security.org/app-help/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        MyApplication.madcapLogger.d(TAG, "onStart Welcome");

        if(checkGooglePlayServices()){
            if(madcapAuthManager.getLastSignedInUsersName() == null){
                new AsyncTask<Void, Void, String>() {
                    protected String doInBackground(Void... params) {
                        // Background Code
                        Intent intent = new Intent(WelcomeActivity.this, LoginService.class);

                        //Sets a flag that the main_old activity should be shown never mind if
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

    /**
     * Verifies that Google Play services is installed and enabled on this device,
     * and that the version installed on this device is no older than the one
     * required by this client.
     * @return
     */
    private boolean checkGooglePlayServices(){
        boolean checkSucceeded = false;

        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        switch(resultCode) {
            case ConnectionResult.SUCCESS:
                MyApplication.madcapLogger.d(TAG, "Google Play services checked and o.k.");
                checkSucceeded = true;
                break;
            case ConnectionResult.SERVICE_MISSING:
                MyApplication.madcapLogger.d(TAG, "Play services not available, please install them.");
                errorTextView.setText("Play services not available, please install them.");
                break;
            case ConnectionResult.SERVICE_UPDATING:
                MyApplication.madcapLogger.d(TAG, "Play services are currently updating, please wait.");
                errorTextView.setText("Play services are currently updating, please wait.");
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                MyApplication.madcapLogger.d(TAG, "Play services not up to date. Please update.");
                errorTextView.setText("Play services not up to date. Please update.");
                break;
            case ConnectionResult.SERVICE_DISABLED:
                MyApplication.madcapLogger.d(TAG, "Play services are disabled. Please enable.");
                errorTextView.setText("Play services are disabled. Please enable them.");
                break;
            case ConnectionResult.SERVICE_INVALID:
                MyApplication.madcapLogger.d(TAG, "Play services are invalid");
                errorTextView.setText("Play services are invalid. Please reinstall them.");
                break;
            default:
                MyApplication.madcapLogger.d(TAG, "Other error");
                break;
        }

        return checkSucceeded;
    }

}
