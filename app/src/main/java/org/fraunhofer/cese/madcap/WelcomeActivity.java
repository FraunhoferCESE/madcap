package org.fraunhofer.cese.madcap;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;

import org.fraunhofer.cese.madcap.authentication.SignInActivity;
import org.fraunhofer.cese.madcap.authentication.SilentLoginResultCallback;
import org.fraunhofer.cese.madcap.authentication.AuthenticationProvider;
import org.fraunhofer.cese.madcap.issuehandling.MadcapPermissionDeniedHandler;
import org.fraunhofer.cese.madcap.services.DataCollectionService;

import javax.inject.Inject;

public class WelcomeActivity extends AppCompatActivity {
    private static final String TAG = "WelcomeActivity";
    private static final int PERMISSION_RQST_CODE = 995;

    @SuppressWarnings("PackageVisibleField")
    @Inject
    AuthenticationProvider authenticationProvider;
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_RQST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) login();
            else finish();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        MyApplication.madcapLogger.d(TAG, "onStart");

        if (ActivityCompat.checkSelfPermission(WelcomeActivity.this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(WelcomeActivity.this, new String[]{Manifest.permission.GET_ACCOUNTS}, PERMISSION_RQST_CODE);
        } else login();
    }

    protected void login(){
        GoogleSignInAccount user = authenticationProvider.getUser();
        if (user != null) {
            MyApplication.madcapLogger.d(TAG, "User already signed in. Starting MainActivity.");
            errorTextView.setText("Welcome " + user.getGivenName() + ' ' + user.getFamilyName());
            startActivity(new Intent(this, MainActivity.class));
        } else {
            final Context context = this;
            authenticationProvider.silentLogin(this, new SilentLoginResultCallback() {
                @Override
                public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    errorTextView.setText(String.format(getString(R.string.play_services_connection_failed), connectionResult));
                    MyApplication.madcapLogger.e(TAG, "onStart.onConnectionFailed: Unable to connect to Google Play services. Error code: " + connectionResult);
                    // TODO: Unregister this listener from mGoogleClientApi in AuthenticationProvider?
                }

                @Override
                public void onServicesUnavailable(int connectionResult) {
                    String text;

                    switch (connectionResult) {
                        case ConnectionResult.SERVICE_MISSING:
                            text = getString(R.string.play_services_missing);
                            break;
                        case ConnectionResult.SERVICE_UPDATING:
                            text = getString(R.string.play_services_updating);
                            break;
                        case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                            text = getString(R.string.play_services_need_updated);
                            break;
                        case ConnectionResult.SERVICE_DISABLED:
                            text = getString(R.string.play_services_disabled);
                            break;
                        case ConnectionResult.SERVICE_INVALID:
                            text = getString(R.string.play_services_invalid);
                            break;
                        default:
                            text = String.format(getString(R.string.play_services_connection_failed), connectionResult);
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
                            Intent intent = new Intent(context, DataCollectionService.class);
                            intent.putExtra("callee", TAG);
                            startService(intent);
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