package org.fraunhofer.cese.madcap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.Status;
import com.google.firebase.iid.FirebaseInstanceId;

import org.fraunhofer.cese.madcap.authentication.MadcapAuthEventHandler;
import org.fraunhofer.cese.madcap.authentication.MadcapAuthManager;
import org.fraunhofer.cese.madcap.services.DataCollectionService;

import javax.inject.Inject;

/**
 * Activity to demonstrate basic retrieval of the Google user's ID, email address, and basic
 * profile.
 */
public class SignInActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener, MadcapAuthEventHandler {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    @Inject
    private MadcapAuthManager madcapAuthManager;


    //private GoogleApiClient mGoogleApiClient;
    private TextView mStatusTextView;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MyApplication) getApplication()).getComponent().inject(this);

        setContentView(R.layout.signinactivity);

        //Debug
        MyApplication.madcapLogger.d(TAG, "Firebase token "+ FirebaseInstanceId.getInstance().getToken());

        madcapAuthManager.setCallbackClass(this);

        MyApplication.madcapLogger.d(TAG, "CREATED");
        //MyApplication.madcapLogger.d(TAG, "Context of Auth Manager is "+MadcapAuthManager.getContext());

        // Views
        mStatusTextView = (TextView) findViewById(R.id.status);

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.to_control_button).setOnClickListener(this);


        // [END build_client]

        // [START customize_button]
        // Customize sign-in button. The sign-in button can be displayed in
        // multiple sizes and color schemes. It can also be contextually
        // rendered based on the requested scopes. For example. a red button may
        // be displayed when Google+ scopes are requested, but a white button
        // may be displayed when only basic profile is requested. Try adding the
        // Scopes.PLUS_LOGIN scope to the GoogleSignInOptions to see the
        // difference.
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(madcapAuthManager.getGsoScopeArray());
        // [END customize_button]
    }

    @Override
    public void onStart() {
        super.onStart();

        MyApplication.madcapLogger.d(TAG, "On start being called.");
    }

    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    public void handleSignInResult(GoogleSignInResult result) {
        //From the result we can retrieve some credentials

        MyApplication.madcapLogger.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null) {
                mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            }else{
                MyApplication.madcapLogger.e(TAG, "Could not get SignIn Result for some reason");
            }
            madcapAuthManager.handleSignInResult(result);
            updateUI(true);

            Intent intent = new Intent(this, DataCollectionService.class);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            if(prefs.getBoolean(getString(R.string.data_collection_pref), true)){
                startService(intent);
            }

            proceedToMainActivity();
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        MyApplication.madcapLogger.d(TAG, "onConnectionFailed:" + connectionResult);
        mStatusTextView.setText("Login failed, please try again");
        Toast.makeText(this, "Login failed, pleas try again", Toast.LENGTH_SHORT);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setText(R.string.signed_out);
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }

    /**
     * Starts the transition to the main_old activity
     */
    public void proceedToMainActivity(){
        MyApplication.madcapLogger.d(TAG, "Now going to the MainActivity");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                MyApplication.madcapLogger.d(TAG, "pressed sign in");
                mStatusTextView.setText("Attempting now to Sign in");
                madcapAuthManager.signIn();
                break;
            case R.id.sign_out_button:
                MyApplication.madcapLogger.d(TAG, "pressed sign ou");
                madcapAuthManager.signOut();
                break;
            case R.id.to_control_button:
                MyApplication.madcapLogger.d(TAG, "pressed to proceed to Control App");
                proceedToMainActivity();
                break;
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    /**
     * Specifies what the class is expected to do, when the silent login was sucessfull.
     */
    @Override
    public void onSilentLoginSuccessfull(GoogleSignInResult result) {
        // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
        // and the GoogleSignInResult will be available instantly.
        MyApplication.madcapLogger.d(TAG, "Got cached sign-in");
        handleSignInResult(result);
    }

    /**
     * Specifies what the clas is expected to do, when the silent login was not successfull.
     */
    @Override
    public void onSilentLoginFailed(OptionalPendingResult<GoogleSignInResult> opr) {
        // If the user has not previously signed in on this device or the sign-in has expired,
        // this asynchronous branch will attempt to sign in the user silently.  Cross-device
        // single sign-on will occur in this branch.
//        showProgressDialog();
//        opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
//            @Override
//            public void onResult(GoogleSignInResult googleSignInResult) {
//                hideProgressDialog();
//                handleSignInResult((GoogleSignInResult)googleSignInResult);
//            }
//        });
    }

    /**
     * Specifies what the class is expected to do, when the regular sign in was successful.
     */
    @Override
    public void onSignInSucessfull() {
        MyApplication.madcapLogger.d(TAG, "onSignIn successfull");

        Intent intent = new Intent(this, DataCollectionService.class);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getBoolean(getString(R.string.data_collection_pref), true)){
            startService(intent);
        }

        proceedToMainActivity();
    }

    @Override
    public void onSignInIntent(Intent intent, int requestCode) {
        MyApplication.madcapLogger.d(TAG,"Now starting the signing procedure with intnet");
        startActivityForResult(intent, requestCode);
    }

    /**
     * Specifies what the app is expected to do when the Signout was sucessfull.
     */
    @Override
    public void onSignOutResults(Status status) {
        updateUI(false);
    }

    /**
     * Specifies what the class is expected to do, when disconnected.
     * @param status
     */
    @Override
    public void onRevokeAccess(Status status) {
        updateUI(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }


}
