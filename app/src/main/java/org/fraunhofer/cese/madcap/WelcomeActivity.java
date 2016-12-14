package org.fraunhofer.cese.madcap;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;

import org.fraunhofer.cese.madcap.authentication.SignInActivity;
import org.fraunhofer.cese.madcap.authentication.SilentLoginResultCallback;
import org.fraunhofer.cese.madcap.authentication.AuthenticationProvider;
import org.fraunhofer.cese.madcap.services.DataCollectionService;

import javax.inject.Inject;

public class WelcomeActivity extends AppCompatActivity {
    private static final String TAG = "WelcomeActivity";

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
    public void onStart() {
        super.onStart();
        MyApplication.madcapLogger.d(TAG, "onStart");

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
