package org.fraunhofer.cese.madcap.authentication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.fraunhofer.cese.madcap.MyApplication;
import org.fraunhofer.cese.madcap.R;

import javax.inject.Inject;

public class NotAuthorizedActivity extends AppCompatActivity {

    @SuppressWarnings({"WeakerAccess", "unused", "PackageVisibleField"})
    @Inject
    AuthenticationProvider authenticationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MyApplication) getApplication()).getComponent().inject(this);
        GoogleSignInAccount acct = authenticationProvider.getUser();

        setContentView(R.layout.activity_not_authorized);
        TextView emailText = (TextView) findViewById(R.id.not_authorized_email);
        TextView userText = (TextView) findViewById(R.id.not_authorized_userid);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            emailText.setText(getString(R.string.not_authorized_user_email, b.getString("email")));
            userText.setText(getString(R.string.not_authorized_user_id, b.getString("userid")));
        } else {
            emailText.setText("Unknown");
            userText.setText("Unknown");
        }
    }
}
