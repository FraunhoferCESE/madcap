package org.fraunhofer.cese.madcap;

import android.os.Bundle;

public class AboutActivity extends ChildActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    @Override
    protected void onSignOut() {
        finish();
    }
}
