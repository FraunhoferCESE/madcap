package org.fraunhofer.cese.madcap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.iid.FirebaseInstanceId;

import org.fraunhofer.cese.madcap.authentication.MadcapAuthManager;
import org.fraunhofer.cese.madcap.services.DataCollectionService;

import javax.inject.Inject;

/**
 * Activity to demonstrate basic retrieval of the Google user's ID, email address, and basic
 * profile.
 */
public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    @Inject
    private MadcapAuthManager madcapAuthManager;

    @Inject
    private GoogleApiClient mGoogleApiClient;

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
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.madcapLogger.d(TAG, "pressed sign in");
                mStatusTextView.setText("Attempting to sign in...");
                signin();
            }
        });

        findViewById(R.id.sign_out_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.madcapLogger.d(TAG, "pressed sign out");
                mStatusTextView.setText("Signing out...");
                signout();
            }
        });

        final Context context = this;
        findViewById(R.id.to_control_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.madcapLogger.d(TAG, "pressed to proceed to Control App");
                startActivity(new Intent(context, MainActivity.class));
            }
        });

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
    }

    private boolean checkApiAvailability() {
        int connectionResult = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        boolean isAvailable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS;

        String text;

        if (isAvailable) {
            text = "Google Play Services are online.";
        } else {
            switch (connectionResult) {
                case ConnectionResult.SERVICE_MISSING:
                    text = "Play services not available, please install them and retry.";
                    break;
                case ConnectionResult.SERVICE_UPDATING:
                    text = "Play services are currently updating, please wait and try again.";
                    break;
                case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                    text = "Play services not up to date. Please update your system and try again.";
                    break;
                case ConnectionResult.SERVICE_DISABLED:
                    text = "Play services are disabled. Please enable and try again.";
                    break;
                case ConnectionResult.SERVICE_INVALID:
                    text = "Play services are invalid. Please reinstall them and try again.";
                    break;
                default:
                    text = "Play services connection failed. Error code: " + connectionResult;
            }

            MyApplication.madcapLogger.e(TAG, text);
        }

        mStatusTextView.setText(text);
        return isAvailable;
    }


    private void signin() {
        if (checkApiAvailability()) {
            if (mGoogleApiClient.isConnected()) {
                startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient), RC_SIGN_IN);
            } else {
                mGoogleApiClient.registerConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        mGoogleApiClient.unregisterConnectionFailedListener(this);
                        MyApplication.madcapLogger.w(TAG, "Could not connect to GoogleSignInApi.");
                        mStatusTextView.setText("Could not connect to Google SignIn service. Please try again.");
                    }
                });
                mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        mGoogleApiClient.unregisterConnectionCallbacks(this);
                        MyApplication.madcapLogger.d(TAG, "Connected to Google SignIn services...");
                        mStatusTextView.setText("Connected to Google SignIn services...");
                        startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient), RC_SIGN_IN);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        MyApplication.madcapLogger.w(TAG, "onConnectionSuspended: Unexpected suspension of connection. Error code: " + i);
                        mStatusTextView.setText("Connection to Google SignIn services interrupted. Please try again.");
                    }
                });
                mGoogleApiClient.connect();
            }
        }
    }

    private void signout() {
        if (checkApiAvailability()) {
            final Context context = this;
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status r) {
                            if (r.getStatusCode() == CommonStatusCodes.SUCCESS) {
                                MyApplication.madcapLogger.d(TAG, "Logout succeeded. Status code: " + r.getStatusCode() + ", Message: " + r.getStatusMessage());
                                findViewById(R.id.sign_in_button).setEnabled(false);
                                findViewById(R.id.sign_out_button).setEnabled(true);
                                findViewById(R.id.to_control_button).setEnabled(true);
                                mStatusTextView.setText("You are now signed out of MADCAP.");
                                Toast.makeText(context, "You are now signed out of MADCAP.", Toast.LENGTH_SHORT).show();

                                // TODO: What to do with the user's data?
                                stopService(new Intent(context, DataCollectionService.class));
                                madcapAuthManager.setUser(null);
                            } else {
                                MyApplication.madcapLogger.e(TAG, "Logout failed. Status code: " + r.getStatusCode() + ", Message: " + r.getStatusMessage());
                                mStatusTextView.setText("Logout from MADCAP failed. Error code: " + r.getStatusCode() + ". Please try again.");
                                Toast.makeText(context, "Logout from MADCAP failed. Error code: " + r.getStatusCode() + ". Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status r) {
                            MyApplication.madcapLogger.d(TAG, "Revoke access finished. Status code: " + r.getStatusCode() + ", Message: " + r.getStatusMessage());
                        }
                    });
        }
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Signed in successfully, show authenticated UI.
                GoogleSignInAccount acct = result.getSignInAccount();
                if (acct == null) {
                    MyApplication.madcapLogger.e(TAG, "Could not get SignIn Result for some reason");
                    throw new RuntimeException("SignIn successful, but account is null. Result: " + result);
                } else {
                    madcapAuthManager.setUser(acct);
                    findViewById(R.id.sign_in_button).setEnabled(false);
                    findViewById(R.id.sign_out_button).setEnabled(true);
                    findViewById(R.id.to_control_button).setEnabled(true);
                    mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));

                    if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.data_collection_pref), true)) {
                        startService(new Intent(this, DataCollectionService.class));
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
    protected void onDestroy() {

        super.onDestroy();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
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
