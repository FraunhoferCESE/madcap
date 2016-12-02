package org.fraunhofer.cese.madcap;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;

import org.fraunhofer.cese.madcap.authentication.LoginResultCallback;
import org.fraunhofer.cese.madcap.authentication.MadcapAuthManager;
import org.fraunhofer.cese.madcap.services.DataCollectionService;

import javax.inject.Inject;

public class WelcomeActivity extends AppCompatActivity {
    private static final String TAG = "WelcomeActivity";

    @Inject
    MadcapAuthManager madcapAuthManager;
    private TextView errorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //noinspection CastToConcreteClass
        ((MyApplication) getApplication()).getComponent().inject(this);
        MyApplication.madcapLogger.d(TAG, "Welcome created");

        setContentView(R.layout.activity_welcome);
        errorTextView = (TextView) findViewById(R.id.welcomeErrorTextView);
        errorTextView.setMovementMethod(LinkMovementMethod.getInstance());

        Button helpButton = (Button) findViewById(R.id.helpButton);
        helpButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                MyApplication.madcapLogger.d(TAG, "Help toggled");

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.pocket-security.org/app-help/"));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        MyApplication.madcapLogger.d(TAG, "onStart");

        GoogleSignInAccount user = madcapAuthManager.getUser();
        if (user != null) {
            MyApplication.madcapLogger.d(TAG, "User already signed in. Starting MainActivity.");
            errorTextView.setText("Welcome " + user.getGivenName() + ' ' + user.getFamilyName());
            startActivity(new Intent(this, MainActivity.class));
        } else {
            final Context context = this;
            madcapAuthManager.silentLogin(this, new LoginResultCallback() {
                @Override
                public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    errorTextView.setText("Unable to connect to Google Signin services. Error code: " + connectionResult);
                    MyApplication.madcapLogger.e(TAG, "onStart.onConnectionFailed: Unable to connect to Google Play services. Error code: " + connectionResult);
                    // TODO: Unregister this listener from mGoogleClientApi in MadcapAuthManager?
                }

                @Override
                public void onConnected(@Nullable Bundle bundle) {
                    MyApplication.madcapLogger.d(TAG, "onStart.onConnected: successfully connected to Google Play Services.");
                    errorTextView.setText("Connected to Google Signin services...");
                }

                @Override
                public void onConnectionSuspended(int i) {
                    MyApplication.madcapLogger.w(TAG, "onStart.onConnectionSuspended: Connection suspended. Error code: " + i);
                    errorTextView.setText("Lost connection to Google Signin services. Please sign in manually.");
                    startActivity(new Intent(context, SignInActivity.class));
                    finish();
                }

                @Override
                public void onServicesUnavailable(int connectionResult) {
                    String text;

                    switch(connectionResult) {
                        case ConnectionResult.SERVICE_MISSING:
                            text =  "Play services not available, please install them.";
                            break;
                        case ConnectionResult.SERVICE_UPDATING:
                            text =   "Play services are currently updating, please wait.";
                            break;
                        case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                            text =   "Play services not up to date. Please update.";
                            break;
                        case ConnectionResult.SERVICE_DISABLED:
                            text =   "Play services are disabled. Please enable.";
                            break;
                        case ConnectionResult.SERVICE_INVALID:
                            text =   "Play services are invalid. Please reinstall them";
                            break;
                        default:
                            text =   "Unknown Play Services return code.";
                    }

                    MyApplication.madcapLogger.e(TAG, text);
                    errorTextView.setText(text);
                    startActivity(new Intent(context, SignInActivity.class));
                    finish();
                }

                @Override
                public void onLoginResult(GoogleSignInResult signInResult) {
                    if (signInResult.isSuccess()) {
                        MyApplication.madcapLogger.d(TAG, "User successfully signed in and authenticated to MADCAP.");
                        errorTextView.setText("Welcome");
                        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(getString(R.string.data_collection_pref), true)) {
                            //TODO start only if not already started
                            startService(new Intent(context, DataCollectionService.class));
                        }
                        startActivity(new Intent(context, MainActivity.class));
                    } else {
                        MyApplication.madcapLogger.d(TAG, "User could not be authenticated to MADCAP. Starting SignInActivity.");
                        startActivity(new Intent(context, SignInActivity.class));
                    }
                    finish();
                }
            });

        }
    }
}
