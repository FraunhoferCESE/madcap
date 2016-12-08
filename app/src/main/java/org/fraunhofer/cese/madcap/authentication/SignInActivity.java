package org.fraunhofer.cese.madcap.authentication;

import android.content.Context;
import android.content.Intent;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.firebase.iid.FirebaseInstanceId;

import org.fraunhofer.cese.madcap.MainActivity;
import org.fraunhofer.cese.madcap.MyApplication;
import org.fraunhofer.cese.madcap.R;
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
    AuthenticationManager authenticationManager;

    private TextView mStatusTextView;

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
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.madcapLogger.d(TAG, "pressed sign in");
                mStatusTextView.setText(R.string.signing_in);
                authenticationManager.interactiveSignIn(activity, RC_SIGN_IN, new LoginResultCallback() {
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
        });

        final Context context = this;
        findViewById(R.id.sign_out_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyApplication.madcapLogger.d(TAG, "pressed sign out");
                        mStatusTextView.setText(R.string.signing_out);

                        authenticationManager.signout(context, new LogoutResultCallback() {
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

                                    // TODO: What to do with the user's data?
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
                        });
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
                    authenticationManager.setUser(acct);
                    findViewById(R.id.sign_in_button).setEnabled(false);
                    findViewById(R.id.sign_out_button).setEnabled(true);
                    findViewById(R.id.to_control_button).setEnabled(true);
                    mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));

                    if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.data_collection_pref), true)) {
                        startService(new Intent(this, DataCollectionService.class).putExtra("callee", TAG));
                    }

                    startActivity(new Intent(this, MainActivity.class));
                }
            } else {
                MyApplication.madcapLogger.w(TAG, "SignIn failed. Status code: " + result.getStatus().getStatusCode() + ", Status message: " + result.getStatus().getStatusMessage());
                findViewById(R.id.sign_in_button).setEnabled(true);
                findViewById(R.id.sign_out_button).setEnabled(false);
                findViewById(R.id.to_control_button).setEnabled(false);
                mStatusTextView.setText("Login failed. Error code: " + result.getStatus().getStatusCode() + ". Please try again.");
                Toast.makeText(this, "Login failed, pleas try again", Toast.LENGTH_SHORT).show();
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


}
