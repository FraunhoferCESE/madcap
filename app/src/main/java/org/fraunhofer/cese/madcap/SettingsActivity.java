package org.fraunhofer.cese.madcap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends ChildActivity {

    private static final float ALPHA_DISABLED = 0.5f;
    private static final float ALPHA_ENABLED = 1.0f;

    @SuppressWarnings("PackageVisibleField")
    @Inject
    FirebaseRemoteConfig mFirebaseRemoteConfig;

    @SuppressWarnings("PackageVisibleField")
    @Inject
    SharedPreferences prefs;

    @SuppressWarnings("PackageVisibleField")
    @BindView(R.id.uploadOnWifiText)
    TextView uploadOnWifiTextView;

    @SuppressWarnings("PackageVisibleField")
    @BindView(R.id.uploadOnWifiOption)
    CheckBox uploadOnWifiCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //noinspection CastToConcreteClass
        ((MyApplication) getApplication()).getComponent().inject(this);

        setContentView(R.layout.activity_settings);

        ButterKnife.bind(this);


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mFirebaseRemoteConfig.getBoolean(getString(R.string.UPLOAD_ON_WIFI_ONLY_KEY))) {
            uploadOnWifiCheckBox.setChecked(false);
            uploadOnWifiCheckBox.setEnabled(false);
            prefs.edit().putBoolean(getString(R.string.pref_upload_on_wifi), false).apply();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uploadOnWifiTextView.setText(Html.fromHtml(getString(R.string.upload_on_wifi_text_disabled), Html.FROM_HTML_MODE_LEGACY));
            } else {
                //noinspection deprecation
                uploadOnWifiTextView.setText(Html.fromHtml(getString(R.string.upload_on_wifi_text_disabled)));
            }
            uploadOnWifiTextView.setAlpha(ALPHA_DISABLED);
        } else {
            uploadOnWifiCheckBox.setEnabled(true);
            uploadOnWifiCheckBox.setChecked(prefs.getBoolean(getString(R.string.pref_upload_on_wifi), false));
            uploadOnWifiTextView.setText(R.string.upload_on_wifi_text);
            uploadOnWifiTextView.setAlpha(ALPHA_ENABLED);
        }
    }

    @OnClick(R.id.uploadOnWifiOption)
    public void uploadOnWifiSelected(@SuppressWarnings("TypeMayBeWeakened") final CheckBox checkBox) {

        if (checkBox.isChecked()) {
            //noinspection AnonymousInnerClassMayBeStatic
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Are you sure?")
                    .setMessage("Checking this option will allow MADCAP to use your cell phone's data plan to upload data. This may count against your monthly limits.")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            prefs.edit().putBoolean(getString(R.string.pref_upload_on_wifi), checkBox.isChecked()).apply();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            checkBox.setChecked(false);
                        }
                    })
                    .show();
        } else {
            prefs.edit().putBoolean(getString(R.string.pref_upload_on_wifi), checkBox.isChecked()).apply();
        }
    }

    @Override
    protected void onSignOut() {
        finish();
    }
}
