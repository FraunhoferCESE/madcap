package org.fraunhofer.cese.madcap.authentication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.fraunhofer.cese.madcap.MainActivity;
import org.fraunhofer.cese.madcap.MyApplication;
import org.fraunhofer.cese.madcap.R;
import org.fraunhofer.cese.madcap.services.DataCollectionService;

import javax.inject.Named;

/**
 * Facade for interacting with the {@link com.google.android.gms.auth.api.signin.GoogleSignInApi} that helps with handling certain preconditions. Handling of these events is specified through callback parameters.
 * <p>
 * Created by llayman on 12/4/2016.
 */

public class GoogleSignInFacade implements GoogleSigninProvider {

    private static final String TAG = "GoogleSignInFacade";

    private final GoogleApiClient mGoogleApiClient;

    public GoogleSignInFacade(@Named("SigninApi") GoogleApiClient googleApiClient) {
        mGoogleApiClient = googleApiClient;
    }


    @Override
    public void silentSignIn(@NonNull Context context, @NonNull final LoginResultCallback callback) {
        MyApplication.madcapLogger.d(TAG, "silentSignIn initiated");

        int connectionResult = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        if (connectionResult != ConnectionResult.SUCCESS) {
            callback.onServicesUnavailable(connectionResult);
            return;
        }

        if (mGoogleApiClient.isConnected()) {
            doSignin(callback);
        } else {
            mGoogleApiClient.registerConnectionFailedListener(callback);
            mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(@Nullable Bundle bundle) {
                    mGoogleApiClient.unregisterConnectionCallbacks(this);
                    doSignin(callback);
                }

                @Override
                public void onConnectionSuspended(int i) {
                    MyApplication.madcapLogger.w(TAG, "onConnectionSuspended: Unexpected suspension of connection. Error code: " + i);
                }
            });
            mGoogleApiClient.connect();
        }
    }

    /**
     * private method to handle actual google sign in events
     *
     * @param callback the callback to handle sign in events
     */
    private void doSignin(@NonNull LoginResultCallback callback) {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);

        if (opr.isDone()) {
            // In Case there is a result available intermediately. This should happen if they signed in before.
            GoogleSignInResult result = opr.get();
            MyApplication.madcapLogger.d(TAG, "Immediate result available: " + result);
            callback.onLoginResult(result);
        } else {
            MyApplication.madcapLogger.d(TAG, "Immediate results are not evailable.");
            opr.setResultCallback(new GoogleSignInResultResultCallback(callback));
        }
    }

    @Override
    public void signout(@NonNull Context context, @NonNull final LogoutResultCallback callback) {
        int connectionResult = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        if (connectionResult != ConnectionResult.SUCCESS) {
            callback.onServicesUnavailable(connectionResult);
            return;
        }

        if (mGoogleApiClient.isConnected()) {
            doSignout(callback);
        } else {
            mGoogleApiClient.registerConnectionFailedListener(callback);
            mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(@Nullable Bundle bundle) {
                    mGoogleApiClient.unregisterConnectionCallbacks(this);
                    doSignout(callback);
                }

                @Override
                public void onConnectionSuspended(int i) {
                    MyApplication.madcapLogger.w(TAG, "onConnectionSuspended: Unexpected suspension of connection. Error code: " + i);
                }

            });
            mGoogleApiClient.connect();
        }
    }

    /**
     * Private method to handle actual calls to google signout
     *
     * @param callback specifies how to handle various signout events
     */
    private void doSignout(@NonNull final LogoutResultCallback callback) {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status r) {
                        callback.onSignOut(r);
                        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                                new SignoutResultCallback(callback));
                    }
                });
    }

    private static class SignoutResultCallback implements ResultCallback<Status> {
        private final LogoutResultCallback callback;

        private SignoutResultCallback(LogoutResultCallback callback) {
            this.callback = callback;
        }

        @Override
        public void onResult(@NonNull Status r) {
            callback.onRevokeAccess(r);
        }
    }

    private static class GoogleSignInResultResultCallback implements ResultCallback<GoogleSignInResult> {
        private final LoginResultCallback callback;

        private GoogleSignInResultResultCallback(LoginResultCallback callback) {
            this.callback = callback;
        }

        @Override
        public void onResult(@NonNull GoogleSignInResult r) {
            MyApplication.madcapLogger.d(TAG, "Received asynchronous login result. Code: " + r.getStatus().getStatusCode() + ", message: " + r.getStatus().getStatusMessage());
            callback.onLoginResult(r);
        }
    }

    private class GoogleSignInActivityWrapper extends Activity {
        private static final int RC_SIGN_IN = 9001;
        @Override
        protected void onStart() {
            super.onStart();
            startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient), RC_SIGN_IN);
        }

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
                            Intent intent = new Intent(this, DataCollectionService.class);
                            intent.putExtra("callee", TAG);
                            startService(intent);
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
}
