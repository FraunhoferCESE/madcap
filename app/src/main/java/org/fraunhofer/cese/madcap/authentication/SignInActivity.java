package org.fraunhofer.cese.madcap.authentication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.firebase.iid.FirebaseInstanceId;

import org.fraunhofer.cese.madcap.MainActivity;
import org.fraunhofer.cese.madcap.MyApplication;
import org.fraunhofer.cese.madcap.R;
import org.fraunhofer.cese.madcap.authorization.AuthorizationException;
import org.fraunhofer.cese.madcap.authorization.AuthorizationHandler;
import org.fraunhofer.cese.madcap.authorization.AuthorizationTaskFactory;
import org.fraunhofer.cese.madcap.services.DataCollectionService;

import javax.inject.Inject;

/**
 * Activity to demonstrate basic retrieval of the Google user's ID, email address, and basic
 * profile.
 */
public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    @SuppressWarnings({"WeakerAccess", "unused", "PackageVisibleField"})
    @Inject
    AuthenticationProvider authenticationProvider;

    @SuppressWarnings({"WeakerAccess", "unused", "PackageVisibleField"})
    @Inject
    AuthorizationTaskFactory authorizationTaskFactory;

    private TextView mStatusTextView;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //noinspection CastToConcreteClass
        ((MyApplication) getApplication()).getComponent().inject(this);

        //Debug
        MyApplication.madcapLogger.d(TAG, "Firebase token " + FirebaseInstanceId.getInstance().getToken());
        MyApplication.madcapLogger.d(TAG, "onCreate");

        // Views
        setContentView(R.layout.signinactivity);
        mStatusTextView = (TextView) findViewById(R.id.status);

        // Button listeners
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        final SignInActivity activity = this;
        final Context context = this;

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.madcapLogger.d(TAG, "pressed sign in");
                mStatusTextView.setText(R.string.checking_EULA);

                boolean bAlreadyAccepted = PreferenceManager
                        .getDefaultSharedPreferences(context).getBoolean(context.getString(R.string.EULA_key), false);

                if (bAlreadyAccepted) {
                    // User has already accepted the EULA. Proceed with sign in.
                    startInteractiveSignin();
                } else {
                    // Show and retrieve approval for the EULA.
                    new AppEULA(activity).show(new EULAListener() {
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
                        MyApplication.madcapLogger.d(TAG, "pressed sign out");
                        mStatusTextView.setText(R.string.signing_out);
                        authenticationProvider.signout(context, createLogoutResultsCallback(context));
                    }
                }
        );

        findViewById(R.id.to_control_button).setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        MyApplication.madcapLogger.d(TAG, "pressed to proceed to Control App");
                                                                        startActivity(new Intent(context, MainActivity.class));
                                                                    }
                                                                }

        );


    }

    private void startInteractiveSignin() {
        mStatusTextView.setText(R.string.signing_in);
        authenticationProvider.interactiveSignIn(this, RC_SIGN_IN, new LoginResultCallback() {

            @Override
            public void onServicesUnavailable(int connectionResult) {
                MyApplication.madcapLogger.w(TAG, "Google SignIn services are unavailable.");
                mStatusTextView.setText(R.string.signin_service_unavailable);
            }

            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                MyApplication.madcapLogger.w(TAG, "Could not connect to GoogleSignInApi.");
                mStatusTextView.setText(R.string.signin_service_connection_failed);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                if (acct == null) {
                    MyApplication.madcapLogger.e(TAG, "Could not get SignIn Result for some reason");
                    throw new NullPointerException("SignIn successful, but account is null. Result: " + result);
                } else {
                    authenticationProvider.setUser(acct);
                    findViewById(R.id.sign_in_button).setEnabled(false);
                    findViewById(R.id.sign_out_button).setEnabled(true);
                    findViewById(R.id.to_control_button).setEnabled(true);
                    mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));

                    progress = new ProgressDialog(this);
                    progress.setMessage("MADCAP is checking your authorization to use the app.");
                    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progress.setIndeterminate(true);
                    progress.setProgress(0);
                    progress.setCancelable(false);
                    progress.show();

                    final Context context = this;

                    authorizationTaskFactory.createAuthorizationTask(context, new AuthorizationHandler() {
                        @Override
                        public void onAuthorized() {
                            if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(getString(R.string.data_collection_pref), true)) {
                                startService(new Intent(context, DataCollectionService.class).putExtra("callee", TAG));
                            }
                            MyApplication.madcapLogger.i(TAG, "User authorized");
                            progress.dismiss();
                            startActivity(new Intent(context, MainActivity.class));
                            finish();
                        }

                        @Override
                        public void onUnauthorized() {
                            MyApplication.madcapLogger.i(TAG, "User not authorized");
                            progress.dismiss();
                            Intent notAuthorizedIntent = new Intent(context, NotAuthorizedActivity.class);
                            GoogleSignInAccount user = authenticationProvider.getUser();
                            if(user != null) {
                                notAuthorizedIntent.putExtra("email", user.getEmail());
                                notAuthorizedIntent.putExtra("userid", user.getId());
                            }

                            authenticationProvider.signout(context, createLogoutResultsCallback(context));
                            finish();
                            startActivity(getIntent());
                            startActivity(notAuthorizedIntent);
                        }

                        @Override
                        public void onError(AuthorizationException exception) {
                            MyApplication.madcapLogger.w(TAG, "User authorization encountered an error:");
                            progress.dismiss();
                            Toast.makeText(context, "An error occurred while checking your authorization. Please try signing in again.", Toast.LENGTH_LONG).show();
                            authenticationProvider.signout(context, createLogoutResultsCallback(context));
                        }
                    }).execute();
                }
            } else {
                findViewById(R.id.sign_in_button).setEnabled(true);
                findViewById(R.id.sign_out_button).setEnabled(false);
                findViewById(R.id.to_control_button).setEnabled(false);
                if (result.getStatus().getStatusCode() == GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
                    MyApplication.madcapLogger.w(TAG, "SignIn cancelled. Status code: " + result.getStatus().getStatusCode() + ", Status message: " + result.getStatus().getStatusMessage());
                    mStatusTextView.setText(getString(R.string.login_cancelled));
                    Toast.makeText(this, "Login cancelled.", Toast.LENGTH_SHORT).show();
                } else {
                    MyApplication.madcapLogger.w(TAG, "SignIn failed. Status code: " + result.getStatus().getStatusCode() + ", Status message: " + result.getStatus().getStatusMessage());
                    mStatusTextView.setText("Login failed. Error code: " + result.getStatus().getStatusCode() + ". Please try again.");
                    Toast.makeText(this, "Login failed, pleas try again", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private LogoutResultCallback createLogoutResultsCallback(final Context context) {
        return new LogoutResultCallback() {
            @Override
            public void onServicesUnavailable(int connectionResult) {
                String text;

                switch (connectionResult) {
                    case ConnectionResult.SERVICE_MISSING:
                        text = context.getString(R.string.play_services_missing);
                        break;
                    case ConnectionResult.SERVICE_UPDATING:
                        text = context.getString(R.string.play_services_updating);
                        break;
                    case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                        text = context.getString(R.string.play_services_need_updated);
                        break;
                    case ConnectionResult.SERVICE_DISABLED:
                        text = context.getString(R.string.play_services_disabled);
                        break;
                    case ConnectionResult.SERVICE_INVALID:
                        text = context.getString(R.string.play_services_invalid);
                        break;
                    default:
                        text = String.format(context.getString(R.string.play_services_connection_failed), connectionResult);
                }

                MyApplication.madcapLogger.e(TAG, text);
                mStatusTextView.setText(text);
            }

            @Override
            public void onSignOut(Status result) {
                if (result.getStatusCode() == CommonStatusCodes.SUCCESS) {
                    MyApplication.madcapLogger.d(TAG, "Logout succeeded. Status code: " + result.getStatusCode() + ", Message: " + result.getStatusMessage());
                    findViewById(R.id.sign_in_button).setEnabled(false);
                    findViewById(R.id.sign_out_button).setEnabled(true);
                    findViewById(R.id.to_control_button).setEnabled(true);
                    mStatusTextView.setText(R.string.post_sign_out);
                    Toast.makeText(context, R.string.post_sign_out, Toast.LENGTH_SHORT).show();

                    //Invalidate EULA acceptance when user logs out
                    SharedPreferences.Editor editor = PreferenceManager
                            .getDefaultSharedPreferences(context).edit();
                    editor.putBoolean(context.getString(R.string.EULA_key), false);
                    editor.apply();

                    stopService(new Intent(context, DataCollectionService.class));
                } else {

                    MyApplication.madcapLogger.e(TAG, "Logout failed. Status code: " + result.getStatusCode() + ", Message: " + result.getStatusMessage());
                    mStatusTextView.setText(String.format(getString(R.string.logout_failed), result.getStatusCode()));
                    Toast.makeText(context, String.format(getString(R.string.logout_failed), result.getStatusCode()), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onRevokeAccess(Status result) {
                MyApplication.madcapLogger.d(TAG, "Revoke access finished. Status code: " + result.getStatusCode() + ", Message: " + result.getStatusMessage());
            }

            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                MyApplication.madcapLogger.w(TAG, "Could not connect to GoogleSignInApi.");
                mStatusTextView.setText(R.string.signin_service_connection_failed);
            }
        };
    }

}
