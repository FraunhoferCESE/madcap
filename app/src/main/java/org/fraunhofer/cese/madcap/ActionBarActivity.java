package org.fraunhofer.cese.madcap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import org.fraunhofer.cese.madcap.authentication.SignInActivity;

import timber.log.Timber;

public abstract class ActionBarActivity extends AppCompatActivity {

    private BroadcastReceiver logoutReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(getString(R.string.madcap_action_logout));
        logoutReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onSignOut();
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(logoutReceiver, intentFilter);

    }

    protected abstract void onSignOut();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (logoutReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(logoutReceiver);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        TextView versionNumberText = (TextView) findViewById(R.id.versionNumber);
        versionNumberText.setText(getString(R.string.versionIntro) + ' ' + BuildConfig.VERSION_NAME + ", Build " + BuildConfig.VERSION_CODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_home:
                startActivity(new Intent(this, MainActivity.class));
                return true;
            case R.id.action_signin:
                startActivity(new Intent(this, SignInActivity.class));
                return true;
            case R.id.action_help:
                startActivity(new Intent(this, HelpActivity.class));
                return true;
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
