package org.fraunhofer.cese.madcap.authorization;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.fraunhofer.cese.madcap.MainActivity;
import org.fraunhofer.cese.madcap.MyApplication;
import org.fraunhofer.cese.madcap.R;
import org.fraunhofer.cese.madcap.authentication.AuthenticationProvider;
import org.fraunhofer.cese.madcap.services.DataCollectionService;

import javax.inject.Inject;

/**
 * Activity that handles authorization of the currently signed in user to the backend and displays results accordingly.
 */
public class AuthorizationActivity extends Activity {

    private static final String TAG = "AuthorizationActivity";

    @SuppressWarnings({"CanBeFinal", "PackageVisibleField"})
    @Inject
    AuthorizationTaskFactory authorizationTaskFactory;

    @SuppressWarnings({"CanBeFinal", "PackageVisibleField"})
    @Inject
    AuthenticationProvider authenticationProvider;

    private ProgressDialog progress;

    private TextView mAuthorizationMessage;
    private TextView mAuthorizationEmail;
    private TextView mAuthorizationUserid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //noinspection CastToConcreteClass
        ((MyApplication) getApplication()).getComponent().inject(this);

        setContentView(R.layout.activity_authorization);

        mAuthorizationMessage = (TextView) findViewById(R.id.authorization_message);
        mAuthorizationEmail = (TextView) findViewById(R.id.authorization_email);
        mAuthorizationUserid = (TextView) findViewById(R.id.authorization_userid);

        progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.auth_check_text));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.setCancelable(false);
        progress.show();

        final Context context = this;

        authorizationTaskFactory.createAuthorizationTask(context, new AuthorizationHandler() {
            @Override
            public void onAuthorized() {
                MyApplication.madcapLogger.i(TAG, "User authorized");
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                mAuthorizationMessage.setText(getString(R.string.authorization_authorized));
                showUserInfo();
                Toast.makeText(context, getString(R.string.authorization_authorized), Toast.LENGTH_SHORT).show();

                if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(getString(R.string.data_collection_pref), true)) {
                    startService(new Intent(context, DataCollectionService.class).putExtra("callee", TAG));
                }
                startActivity(new Intent(context, MainActivity.class));
                finish();
            }

            @Override
            public void onUnauthorized() {
                MyApplication.madcapLogger.i(TAG, "User not authorized");
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                mAuthorizationMessage.setText(getString(R.string.not_authorized_text));
                showUserInfo();
                Toast.makeText(context, getString(R.string.not_authorized_short), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(AuthorizationException exception) {
                MyApplication.madcapLogger.w(TAG, "User authorization encountered an error: " + exception);
                if (progress.isShowing()) {
                    progress.dismiss();
                }
                mAuthorizationMessage.setText(getString(R.string.authorization_error));
                showUserInfo();
                Toast.makeText(context, getString(R.string.authorization_error), Toast.LENGTH_LONG).show();
            }

            private void showUserInfo() {
                GoogleSignInAccount user = authenticationProvider.getUser();
                if (user != null) {
                    mAuthorizationEmail.setText(getString(R.string.not_authorized_user_email, user.getEmail()));
                    mAuthorizationUserid.setText(getString(R.string.not_authorized_user_email, user.getId()));
                }
            }
        }).execute();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (progress.isShowing()) {
            progress.dismiss();
        }
    }
}
