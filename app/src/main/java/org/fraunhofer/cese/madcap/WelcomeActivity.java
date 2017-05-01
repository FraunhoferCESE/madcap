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

import org.fraunhofer.cese.madcap.authentication.AuthenticationProvider;
import org.fraunhofer.cese.madcap.authentication.SignInActivity;
import org.fraunhofer.cese.madcap.authentication.SilentLoginResultCallback;
import org.fraunhofer.cese.madcap.services.DataCollectionService;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Create the welcome activity
 */
public class WelcomeActivity extends AppCompatActivity {

    @SuppressWarnings("PackageVisibleField")
    @Inject
    AuthenticationProvider authenticationProvider;
    private TextView welcomeMessageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //noinspection CastToConcreteClass
        ((MyApplication) getApplication()).getComponent().inject(this);
        Timber.d("Welcome created");

        setContentView(R.layout.activity_welcome);
        welcomeMessageView = (TextView) findViewById(R.id.welcomeMessage);
        welcomeMessageView.setMovementMethod(LinkMovementMethod.getInstance());

        Button helpButton = (Button) findViewById(R.id.helpButton);
        helpButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                Timber.d("Help toggled");

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(getString(R.string.onlineHelpURL)));
                startActivity(intent);
            }
        });

        // Set the version number at the bottom of the activity
        // Set the version number at the bottom of the activity
        TextView buildVersion = (TextView) findViewById(R.id.buildVersion);
        buildVersion.setText(String.format(getString(R.string.buildVersion), BuildConfig.VERSION_NAME));
        TextView buildNumber = (TextView) findViewById(R.id.buildNumber);
        buildNumber.setText(String.format(getString(R.string.buildNumber), BuildConfig.VERSION_CODE));
    }

    @Override
    public void onStart() {
        super.onStart();
        Timber.d("onStart");

        GoogleSignInAccount user = authenticationProvider.getUser();
        if (user != null) {
            Timber.d("User already signed in. Starting MainActivity.");
            welcomeMessageView.setText(String.format(getString(R.string.welcome_signin_success), user.getEmail()));
            startActivity(new Intent(this, MainActivity.class));
        } else {
            final Context context = this;
            authenticationProvider.silentLogin(this, new SilentLoginResultCallback() {
                @Override
                public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    welcomeMessageView.setText(String.format(getString(R.string.play_services_connection_failed), connectionResult));
                    Timber.e("onStart.onConnectionFailed: Unable to connect to Google Play services. Error code: " + connectionResult);
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

                    Timber.w("SilentLogin unavailable", text, "Starting SignInActivity");
                    welcomeMessageView.setText(text);
                    startActivity(new Intent(context, SignInActivity.class));
                    finish();
                }

                @Override
                public void onLoginResult(GoogleSignInResult signInResult) {
                    GoogleSignInAccount acct = signInResult.getSignInAccount();
                    if (signInResult.isSuccess() && (acct != null)) {
                        Timber.d("User successfully signed in and authenticated to MADCAP.");
                        welcomeMessageView.setText(String.format(getString(R.string.welcome_signin_success), acct.getEmail()));
                        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(getString(R.string.pref_dataCollection), true)) {
                            Timber.d("Data Collection is on");
                            Intent intent = new Intent(context, DataCollectionService.class);
                            startService(intent);
                        }
                        startActivity(new Intent(context, MainActivity.class));
                    } else {
                        Timber.d("User could not be authenticated to MADCAP. Starting SignInActivity.");
                        welcomeMessageView.setText(getString(R.string.weclome_signin_failed));
                        startActivity(new Intent(context, SignInActivity.class));
                    }
                    finish();
                }
            });

        }
    }
}
