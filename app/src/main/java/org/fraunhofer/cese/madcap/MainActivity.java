package org.fraunhofer.cese.madcap;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import org.fraunhofer.cese.madcap.cache.Cache;
import org.fraunhofer.cese.madcap.cache.RemoteUploadResult;
import org.fraunhofer.cese.madcap.services.DataCollectionService;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DateFormat;
import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Creates the main activity for MADCAP.
 */
@SuppressWarnings("PackageVisibleField")
public class MainActivity extends ActionBarActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final DateFormat DATE_FORMAT = DateFormat.getDateTimeInstance();

    private static final float ALPHA_DISABLED = 0.5f;
    private static final float ALPHA_ENABLED = 1.0f;

    @Inject FirebaseRemoteConfig firebaseRemoteConfig;
    @Inject SharedPreferences prefs;

    //This is the data collection service we bind to.
    @Nullable
    private DataCollectionService mDataCollectionService;
    private volatile boolean mBound;

    private long mDataCount;

    //Ui elements
    @BindView(R.id.dataCollectionStatus) TextView collectionDataStatusText;
    @BindView(R.id.dataCollectionSwitch) Switch collectDataSwitch;
    @BindView(R.id.progressText) TextView uploadProgressText;
    @BindView(R.id.progressBar) ProgressBar uploadProgressBar;
    @BindView(R.id.dataCountText) TextView dataCountView;
    @BindView(R.id.uploadResultHeader) TextView uploadResultView;
    @BindView(R.id.lastUploadDate) TextView uploadDateView;
    @BindView(R.id.lastUploadStatus) TextView uploadStatusView;
    @BindView(R.id.lastUploadMessage) TextView uploadMessageView;
    @BindView(R.id.uploadButton) Button uploadButton;
    @BindView(R.id.capacity_warning_icon) ImageView capacityWarningIcon;
    @BindView(R.id.capacity_warning_text) TextView capacityWarningText;

    @BindView(R.id.warningBlock) ConstraintLayout warningBlock;

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //noinspection CastToConcreteClass
        ((MyApplication) getApplication()).getComponent().inject(this);
        ButterKnife.bind(this);

        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name,
                                           IBinder service) {
                // We've bound to DataCollectionService, cast the IBinder and get DataCollectionService instance
                Timber.d("New connection service: " + service);
                //noinspection CastToConcreteClass
                mDataCollectionService = ((DataCollectionService.DataCollectionServiceBinder) service).getService();
                mBound = true;

                // Update UI elements
                updateUiElements(true);
                setIsCollectingData(true);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                // Only invoked when hosting service crashed or is killed.

                updateUiElements(false);
                setIsCollectingData(false);

                mDataCollectionService = null;
                mBound = false;
                Timber.d("onServiceDisconnected");
            }
        };
    }

    @OnClick(R.id.uploadButton)
    void onClickUploadButton() {
        if (mBound && (mDataCollectionService != null)) {
            mDataCollectionService.requestUpload();
            Timber.d("Upload data clicked");
        } else {
            Timber.w("Requested manual upload, but DataCollectionService was not bound.");
        }
    }

    @OnCheckedChanged(R.id.dataCollectionSwitch)
    void onDataCollectionToggle(boolean isChecked) {
        if (isChecked) {
            // Start the data collection service
            Intent intent = new Intent(getApplicationContext(), DataCollectionService.class);
            getApplicationContext().startService(intent);
            bindConnection(intent);
        } else {
            // Stop the data collection service
            unbindConnection();
            Intent intent = new Intent(getApplicationContext(), DataCollectionService.class);
            getApplicationContext().stopService(intent);

            updateUiElements(false);
            setIsCollectingData(false);
        }
    }

    private void updateUiElements(boolean isCollectingData) {
        if (isCollectingData) {
            collectionDataStatusText.setText(getString(R.string.dataCollection_on));
            collectionDataStatusText.setBackgroundColor(ContextCompat.getColor(this, R.color.light_green));
            uploadButton.setEnabled(true);
            uploadButton.setAlpha(ALPHA_ENABLED);
        } else {
            collectionDataStatusText.setText(getString(R.string.dataCollection_off));
            collectionDataStatusText.setBackgroundColor(ContextCompat.getColor(this, R.color.light_red));
            uploadButton.setEnabled(false);
            uploadButton.setAlpha(ALPHA_DISABLED);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        EventBus.getDefault().register(this);

        if (isCollectingData()) {
            Intent intent = new Intent(getApplicationContext(), DataCollectionService.class);
            if (!mBound) {
                bindConnection(intent);
            }
        }
        updateUiElements(isCollectingData());

        //Set the toggle button on the last set preference configuration
        collectDataSwitch.setChecked(isCollectingData());
        dataCountView.setText(String.format(getString(R.string.dataCountText), prefs.getLong(getString(R.string.pref_dataCount), 0L)));
        uploadDateView.setText(String.format(getString(R.string.lastUploadDateText), formatDate()));
        uploadStatusView.setText(prefs.getString(getString(R.string.pref_lastUploadStatus), ""));
        uploadMessageView.setText(String.format(getString(R.string.lastUploadMessage), prefs.getString(getString(R.string.pref_lastUploadMessage), "")));
        if (uploadStatusView.getText().length() != 0) {
            uploadResultView.setText(getString(R.string.uploadResultHeader));
        }
        uploadProgressBar.setProgress(prefs.getInt(getString(R.string.pref_uploadProgress), 0));
        uploadProgressText.setText(String.format(getString(R.string.uploadPercentText), prefs.getInt(getString(R.string.pref_uploadProgress), 0)));
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        if (mBound) {
            unbindConnection();
        }

        prefs.edit().putLong(getString(R.string.pref_dataCount), mDataCount).apply();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.d("onDestroy");

        if (mBound) {
            unbindConnection();
        }
    }

    /**
     * Convenience method for getting the preference representing whether the user has turned data collection on/off
     *
     * @return @code{true} if data collection is turned on or not specified, @code{false} otherwise
     */
    private boolean isCollectingData() {
        return prefs.getBoolean(getString(R.string.pref_dataCollection), true);
    }

    /**
     * Convenience method for writing the data collection on/off state to preferences
     *
     * @param isCollectingData boolean value to indicate whether data is being collected or not
     */
    @SuppressLint("ApplySharedPref")
    private void setIsCollectingData(boolean isCollectingData) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(getString(R.string.pref_dataCollection), isCollectingData);
        editor.commit();
        Timber.d("Current data collection preference is now " + isCollectingData);
    }

    private void bindConnection(Intent intent) {
        Timber.d("Attempt to bind to service. Current bound status is " + mBound);
        if (!mBound) {
            getApplicationContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private void unbindConnection() {
        Timber.d("Attempt to unbind service connection. Current bound status is " + mBound);
        getApplicationContext().unbindService(mConnection);
        mBound = false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCacheCountUpdate(Cache.CacheCountUpdate event) {
        if (event == null) {
            return;
        }

        dataCountView.setText(String.format(getString(R.string.dataCountText), event.getCount()));
        mDataCount = event.getCount();

        //noinspection MagicNumber
        double percentage = ((double) event.getCount() * 100.0d) / (double) firebaseRemoteConfig.getLong(getString(R.string.DB_FORCED_CLEANUP_LIMIT_KEY));
        double limit = firebaseRemoteConfig.getDouble(getString(R.string.CLEANUP_WARNING_LIMIT_KEY));

        if (percentage >= limit) {
            if (warningBlock.getVisibility() == View.GONE) {
                warningBlock.setVisibility(View.VISIBLE);
            }
            capacityWarningText.setText(String.format(getString(R.string.capacity_warning), percentage));

        } else if (warningBlock.getVisibility() != View.GONE) {
            warningBlock.setVisibility(View.GONE);
        }
    }

    private String formatDate() {
        String result = prefs.getString(getString(R.string.pref_lastUploadDate_default), "");
        long date = prefs.getLong(getString(R.string.pref_lastUploadDate), 0L);
        if (date != 0L) {
            result = DATE_FORMAT.format(new Date(date));
        }
        return result;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        //noinspection IfStatementWithTooManyBranches
        if (getString(R.string.pref_lastUploadDate).equals(key)) {
            uploadDateView.setText(String.format(getString(R.string.lastUploadDateText), formatDate()));
        } else if (getString(R.string.pref_lastUploadStatus).equals(key)) {
            uploadResultView.setText(getString(R.string.uploadResultHeader));
            uploadStatusView.setText(prefs.getString(getString(R.string.pref_lastUploadStatus), ""));
        } else if (getString(R.string.pref_lastUploadMessage).equals(key)) {
            uploadResultView.setText(getString(R.string.uploadResultHeader));
            uploadMessageView.setText(String.format(getString(R.string.lastUploadMessage), prefs.getString(getString(R.string.pref_lastUploadMessage), "")));
        } else if (getString(R.string.pref_uploadProgress).equals(key)) {
            uploadProgressBar.setProgress(prefs.getInt(getString(R.string.pref_uploadProgress), 0));
            uploadProgressText.setText(String.format(getString(R.string.uploadPercentText), prefs.getInt(getString(R.string.pref_uploadProgress), 0)));
        }
    }

    @Override
    protected void onSignOut() {
        finish();
    }

    @Subscribe
    public void uploadFinished(RemoteUploadResult result) {

        if(mBound && mDataCollectionService != null) {
            long mDataCount = mDataCollectionService.getCount();
            dataCountView.setText(String.format(getString(R.string.dataCountText),mDataCount));
        }

        if (warningBlock.getVisibility() == View.GONE) {
            return;
        }

        if ((result != null) && (result.getSaveResult() != null) && !result.getSaveResult().getSaved().isEmpty()) {
            warningBlock.setVisibility(View.GONE);
        }
    }
}
