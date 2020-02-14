package org.fraunhofer.cese.madcap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import org.fraunhofer.cese.madcap.authentication.SignInActivity;

/**
 * Base class for MADCAP activities that should include an Action Bar
 */
@SuppressWarnings("AbstractClassExtendsConcreteClass")
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

    /**
     * Called when the user signs out of MADCAP. Activities may want to finish(). Activities can also no-op.
     */
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

        // Create the action bar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // Set the version number at the bottom of the activity
        TextView buildVersion = (TextView) findViewById(R.id.buildVersion);
        buildVersion.setText(String.format(getString(R.string.buildVersion), BuildConfig.VERSION_NAME));
        TextView buildNumber = (TextView) findViewById(R.id.buildNumber);
        buildNumber.setText(String.format(getString(R.string.buildNumber), BuildConfig.VERSION_CODE));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);
        return true;
    }

    @SuppressWarnings("ClassReferencesSubclass")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_signin:
                startActivity(new Intent(this, SignInActivity.class));
                return true;
            case R.id.action_permissions:
                startActivity(new Intent(this, PermissionsActivity.class));
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
