package org.fraunhofer.cese.madcap;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import timber.log.Timber;

public class HelpActivity extends ChildActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //noinspection CastToConcreteClass
        setContentView(R.layout.activity_help);

        Button contactMadcapButton = (Button) findViewById(R.id.contactMadcapButton);
        contactMadcapButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.contactEmail)});
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.contactSubject));
                try {
                    startActivity(Intent.createChooser(intent, "Send mail..."));
                } catch (ActivityNotFoundException exception) {
                    Timber.i(exception);
                    Toast.makeText(HelpActivity.this, "No email client installed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button onlineHelpButton = (Button) findViewById(R.id.onlineHelpButton);
        onlineHelpButton.setOnClickListener(new View.OnClickListener() {

            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(getString(R.string.onlineHelpURL)));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onSignOut() {
        finish();
    }
}
