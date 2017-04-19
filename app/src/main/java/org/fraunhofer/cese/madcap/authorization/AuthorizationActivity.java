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
import org.fraunhofer.cese.madcap.services.DataCollectionService;

public class AuthorizationActivity extends Activity {

    public AuthorizationActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

//        ((MyApplication) getApplication()).getComponent().inject(this);
//        GoogleSignInAccount acct = authenticationProvider.getUser();
//
//        setContentView(R.layout.activity_not_authorized);
//        TextView emailText = (TextView) findViewById(R.id.not_authorized_email);
//        TextView userText = (TextView) findViewById(R.id.not_authorized_userid);
//
//        Bundle b = getIntent().getExtras();
//        if (b != null) {
//            emailText.setText(getString(R.string.not_authorized_user_email, b.getString("email")));
//            userText.setText(getString(R.string.not_authorized_user_id, b.getString("userid")));
//        } else {
//            emailText.setText("Unknown");
//            userText.setText("Unknown");
//        }
//
//
//
// progress = new ProgressDialog(this);
//        progress.setMessage("MADCAP is checking your authorization to use the app.");
//        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        progress.setIndeterminate(true);
//        progress.setProgress(0);
//        progress.setCancelable(false);
//        progress.show();
//
//        final Context context = this;
//
//        authorizationTaskFactory.createAuthorizationTask(context, new AuthorizationHandler() {
//            @Override
//            public void onAuthorized() {
//                if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(getString(R.string.data_collection_pref), true)) {
//                    startService(new Intent(context, DataCollectionService.class).putExtra("callee", TAG));
//                }
//                MyApplication.madcapLogger.i(TAG, "User authorized");
//                progress.dismiss();
//                startActivity(new Intent(context, MainActivity.class));
//                finish();
//            }
//
//            @Override
//            public void onUnauthorized() {
//                MyApplication.madcapLogger.i(TAG, "User not authorized");
//                progress.dismiss();
//                Intent notAuthorizedIntent = new Intent(context, NotAuthorizedActivity.class);
//                GoogleSignInAccount user = authenticationProvider.getUser();
//                if(user != null) {
//                    notAuthorizedIntent.putExtra("email", user.getEmail());
//                    notAuthorizedIntent.putExtra("userid", user.getId());
//                }
//
//                authenticationProvider.signout(context, createLogoutResultsCallback(context));
//                finish();
//                startActivity(getIntent());
//                startActivity(notAuthorizedIntent);
//            }
//
//            @Override
//            public void onError(AuthorizationException exception) {
//                MyApplication.madcapLogger.w(TAG, "User authorization encountered an error:");
//                progress.dismiss();
//                Toast.makeText(context, "An error occurred while checking your authorization. Please try signing in again.", Toast.LENGTH_LONG).show();
//                authenticationProvider.signout(context, createLogoutResultsCallback(context));
//            }
//        }).execute();
//    }


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
