package org.fraunhofer.cese.madcap;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import org.fraunhofer.cese.madcap.issuehandling.MadcapPermissionsManager;
import org.fraunhofer.cese.madcap.services.DataCollectionService;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Create the welcome activity
 */
@SuppressWarnings({"PackageVisibleField", "WeakerAccess", "CanBeFinal"})
public class WelcomeActivity extends AppCompatActivity {
    private static final String TAG = "WelcomeActivity";
    private static final int PERMISSION_RQST_CODE = 995;

    @Inject
    AuthenticationProvider authenticationProvider;

    @Inject
    MadcapPermissionsManager permissionsManager;
    @BindView(R.id.welcomeMessage) TextView welcomeMessageView;

    @BindView(R.id.buildVersion) TextView buildVersion;

    @BindView(R.id.buildNumber) TextView buildNumber;

    @BindView(R.id.wa_permissionRationale) TextView permissionRationaleTV;

    @BindView(R.id.wa_grantButton) Button grantButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //noinspection CastToConcreteClass
        ((MyApplication) getApplication()).getComponent().inject(this);
        ButterKnife.bind(this);

        welcomeMessageView.setMovementMethod(LinkMovementMethod.getInstance());


        // Set the version number at the bottom of the activity
        buildVersion.setText(String.format(getString(R.string.buildVersion), BuildConfig.VERSION_NAME));
        buildNumber.setText(String.format(getString(R.string.buildNumber), BuildConfig.VERSION_CODE));
    }

    @OnClick(R.id.helpButton)
    void onClickHelpButton() {
        Timber.d("Help toggled");

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getString(R.string.onlineHelpURL)));
        startActivity(intent);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_RQST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionRationaleTV.setVisibility(View.GONE);
                grantButton.setVisibility(View.GONE);
                login();
            } else {

                permissionRationaleTV.setVisibility(View.VISIBLE);

                grantButton.setVisibility(View.VISIBLE);
                grantButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(WelcomeActivity.this, new String[]{Manifest.permission.GET_ACCOUNTS},
                                PERMISSION_RQST_CODE);
                    }
                });

            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Timber.d("onStart");
        if (!permissionsManager.isContactPermitted()) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setTitle("MADCAP permissions")
                    .setMessage(getString(R.string.contacts_rationale))
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(WelcomeActivity.this, new String[]{Manifest.permission.GET_ACCOUNTS},
                                            PERMISSION_RQST_CODE);
                                    dialog.cancel();
                                }
                            })
                    .setCancelable(false);
            AlertDialog dialog = dialogBuilder.create();
            dialog.show();
        } else login();
    }

    protected void login() {
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