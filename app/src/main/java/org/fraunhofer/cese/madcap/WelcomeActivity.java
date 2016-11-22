package org.fraunhofer.cese.madcap;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;

import org.fraunhofer.cese.madcap.authentication.ApiUtils;
import org.fraunhofer.cese.madcap.authentication.MadcapAuthManager;

import javax.inject.Inject;

public class WelcomeActivity extends AppCompatActivity {
    private static final String TAG = "WelcomeActivity";

    @Inject
    private MadcapAuthManager madcapAuthManager;
    private TextView errorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //noinspection CastToConcreteClass
        ((MyApplication) getApplication()).getComponent().inject(this);
        MyApplication.madcapLogger.d(TAG, "Welcome created");

        setContentView(R.layout.activity_welcome);
        errorTextView = (TextView) findViewById(R.id.welcomeErrorTextView);
        errorTextView.setMovementMethod(LinkMovementMethod.getInstance());

        Button helpButton = (Button) findViewById(R.id.helpButton);
        helpButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                MyApplication.madcapLogger.d(TAG, "Help toggled");

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.pocket-security.org/app-help/"));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        MyApplication.madcapLogger.d(TAG, "onStart");

        if (madcapAuthManager.getUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            final Context context = this;
            madcapAuthManager.silentLogin(this, new MadcapAuthManager.LoginResultCallback() {
                @Override
                public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    errorTextView.setText("Unable to connect to Google Signin services. Error code: " + connectionResult);
                    MyApplication.madcapLogger.e(TAG, "onStart.onConnectionFailed: Unable to connect to Google Play services. Error code: " + connectionResult);
                    // TODO: Unregister this listener from mGoogleClientApi in MadcapAuthManager?
                }

                @Override
                public void onConnected(@Nullable Bundle bundle) {
                    MyApplication.madcapLogger.d(TAG, "onStart.onConnected: successfully connected to Google Play Services.");
                    errorTextView.setText("Connected to Google Signin services...");
                }

                @Override
                public void onConnectionSuspended(int i) {
                    MyApplication.madcapLogger.w(TAG, "onStart.onConnectionSuspended: Connection suspended. Error code: " + i);
                    errorTextView.setText("Lost connection to Google Signin services. Please try again.");
                }

                @Override
                public void onServicesUnavailable(int connectionResult) {
                    String text = ApiUtils.getTextFromConnectionResult(connectionResult);
                    MyApplication.madcapLogger.e(TAG, text);
                    errorTextView.setText(text);
                }

                @Override
                public void onLoginResult(GoogleSignInResult signInResult) {
                    if (signInResult.isSuccess()) {
                        startActivity(new Intent(context, MainActivity.class));
                        finish();
                    }
                }
            });

        }
    }

}
