package org.fraunhofer.cese.madcap;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Creates the Help activity
 */
public class HelpActivity extends ChildActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //noinspection CastToConcreteClass
        setContentView(R.layout.activity_help);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.onlineHelpButton)
    void onClickHelpButton() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getString(R.string.onlineHelpURL)));
        startActivity(intent);
    }

    @OnClick(R.id.contactMadcapButton)
    void onClickMadcapButton() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.contactEmail)});
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.contactSubject));
        try {
            startActivity(Intent.createChooser(intent, "Send mail..."));
        } catch (ActivityNotFoundException exception) {
            Timber.i(exception);
            Toast.makeText(this, "No email client installed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSignOut() {
        finish();
    }
}
