package org.fraunhofer.cese.madcap.authentication;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import org.fraunhofer.cese.madcap.AboutActivity;
import org.fraunhofer.cese.madcap.ChildActivity;
import org.fraunhofer.cese.madcap.HelpActivity;
import org.fraunhofer.cese.madcap.MainActivity;
import org.fraunhofer.cese.madcap.MyApplication;
import org.fraunhofer.cese.madcap.R;
import org.fraunhofer.cese.madcap.authorization.AuthorizationActivity;
import org.fraunhofer.cese.madcap.services.DataCollectionService;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Activity to demonstrate basic retrieval of the Google user's ID, email address, and basic
 * profile.
 */
public class SignInActivity extends ChildActivity {

    private static final int RC_SIGN_IN = 9001;

    @SuppressWarnings("PackageVisibleField")
    @Inject
    AuthenticationProvider authenticationProvider;

    private TextView mStatusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //noinspection CastToConcreteClass
        ((MyApplication) getApplication()).getComponent().inject(this);

        // Views
        setContentView(R.layout.activity_signin);
        mStatusTextView = (TextView) findViewById(R.id.signin_status);

        // Button listeners
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        final SignInActivity activity = this;

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Timber.d("pressed sign in");
                mStatusTextView.setText(R.string.checking_EULA);

                boolean bAlreadyAccepted = PreferenceManager
                        .getDefaultSharedPreferences(activity).getBoolean(activity.getString(R.string.EULA_key), false);

                if (bAlreadyAccepted) {
                    // User has already accepted the EULA. Proceed with sign in.
                    startInteractiveSignin();
                } else {
                    // Show and retrieve approval for the EULA.
                    new EulaProvider(activity).show(new EULAListener() {
                        @Override
                        public void onAccept() {
                            // User has accepted the EULA. Proceed with sign in.
                            startInteractiveSignin();
                        }

                        @Override
                        public void onCancel() {
                            // User has declined the EULA. Display SignInActivity until they do.
                            mStatusTextView.setText(R.string.must_accept_EULA);
                        }
                    });
                }
            }
        });


        findViewById(R.id.sign_out_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Timber.d("pressed sign out");
                        mStatusTextView.setText(R.string.signing_out);
                        authenticationProvider.signout(activity,
                                new LogoutResultCallback() {
                                    @Override
                                    public void onServicesUnavailable(int connectionResult) {
                                        String text;

                                        switch (connectionResult) {
                                            case ConnectionResult.SERVICE_MISSING:
                                                text = activity.getString(R.string.play_services_missing);
                                                break;
                                            case ConnectionResult.SERVICE_UPDATING:
                                                text = activity.getString(R.string.play_services_updating);
                                                break;
                                            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                                                text = activity.getString(R.string.play_services_need_updated);
                                                break;
                                            case ConnectionResult.SERVICE_DISABLED:
                                                text = activity.getString(R.string.play_services_disabled);
                                                break;
                                            case ConnectionResult.SERVICE_INVALID:
                                                text = activity.getString(R.string.play_services_invalid);
                                                break;
                                            default:
                                                text = String.format(activity.getString(R.string.play_services_connection_failed), connectionResult);
                                        }

                                        Timber.w(text);
                                        mStatusTextView.setText(text);
                                    }

                                    @Override
                                    public void onSignOut(Status result) {
                                        if (result.getStatusCode() == CommonStatusCodes.SUCCESS) {
                                            Timber.d("Logout succeeded. Status code: " + result.getStatusCode() + ", Message: " + result.getStatusMessage());
                                            updateUiState(false);

                                            mStatusTextView.setText(R.string.post_sign_out);
                                            Toast.makeText(activity, R.string.post_sign_out, Toast.LENGTH_SHORT).show();

                                            //Invalidate EULA acceptance when user logs out
                                            SharedPreferences.Editor editor = PreferenceManager
                                                    .getDefaultSharedPreferences(activity).edit();
                                            editor.putBoolean(activity.getString(R.string.EULA_key), false);
                                            editor.apply();

                                            stopService(new Intent(activity, DataCollectionService.class));
                                        } else {

                                            Timber.w("Sign out failed. Status code: " + result.getStatusCode() + ", Message: " + result.getStatusMessage());
                                            mStatusTextView.setText(String.format(getString(R.string.logout_failed), result.getStatusCode()));
                                            Toast.makeText(activity, String.format(getString(R.string.logout_failed), result.getStatusCode()), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onRevokeAccess(Status result) {
                                        Timber.d("Revoke access finished. Status code: " + result.getStatusCode() + ", Message: " + result.getStatusMessage());
                                    }

                                    @Override
                                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                                        Timber.w("Could not connect to GoogleSignInApi.");
                                        mStatusTextView.setText(R.string.signin_service_connection_failed);
                                    }
                                });
                    }
                }
        );

        TextView subtitle = (TextView) findViewById(R.id.subtitle_text);
        subtitle.setMovementMethod(LinkMovementMethod.getInstance());
    }


    @Override
    protected void onResume() {
        super.onResume();

        GoogleSignInAccount acct = authenticationProvider.getUser();
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        if (acct != null) {
            updateUiState(true);
            mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getEmail()));
        } else {
            updateUiState(false);
        }
    }

    private void startInteractiveSignin() {
        mStatusTextView.setText(R.string.signing_in);
        authenticationProvider.interactiveSignIn(this, RC_SIGN_IN, new LoginResultCallback() {

            @Override
            public void onServicesUnavailable(int connectionResult) {
                Timber.w("Google SignIn services are unavailable.");
                mStatusTextView.setText(R.string.signin_service_unavailable);
            }

            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Timber.w("Could not connect to GoogleSignInApi.");
                mStatusTextView.setText(R.string.signin_service_connection_failed);
            }
        });
    }

    private void updateUiState(boolean isSignedIn) {
        if (isSignedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
            findViewById(R.id.my_toolbar).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
            findViewById(R.id.my_toolbar).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess() && (result.getSignInAccount() != null)) {
                GoogleSignInAccount acct = result.getSignInAccount();
                authenticationProvider.setUser(acct);
                updateUiState(true);
                mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
                startActivity(new Intent(this, AuthorizationActivity.class));
            } else {
                updateUiState(false);
                Timber.w("SignIn failed. Status code: " + result.getStatus().getStatusCode() + ", Status message: " + result.getStatus().getStatusMessage());
                mStatusTextView.setText(getString(R.string.login_failed));
                Toast.makeText(this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_home:
                startActivity(new Intent(this, MainActivity.class));
                return true;
            case R.id.action_signin:
                startActivity(new Intent(this, SignInActivity.class));
                return true;
            case R.id.action_help:
                startActivity(new Intent(this, HelpActivity.class));
                return true;
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override

    protected void onSignOut() {

    }

}
