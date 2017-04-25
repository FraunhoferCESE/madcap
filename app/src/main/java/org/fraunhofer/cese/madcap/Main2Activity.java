package org.fraunhofer.cese.madcap;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import org.fraunhofer.cese.madcap.cache.Cache;
import org.fraunhofer.cese.madcap.services.DataCollectionService;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DateFormat;
import java.util.Date;

import timber.log.Timber;

public class Main2Activity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String STATE_UPLOAD_STATUS = "uploadStatus";
    private static final String STATE_DATA_COUNT = "dataCount";
    private static final String STATE_COLLECTING_DATA = "isCollectingData";

    private SharedPreferences prefs;

    //This is the data collection service we bind to.
    @Nullable
    private DataCollectionService mDataCollectionService;
    private volatile boolean mBound;

    private long mDataCount;

    //Ui elements
    private TextView nameTextView;
    private TextView collectionDataStatusText;
    private LinearLayout dataCollectionLayout;
    private Switch collectDataSwitch;
    private ProgressBar uploadProgressBar;
    private TextView dataCountView;
    private TextView uploadResultView;
    private TextView uploadDateView;
    private TextView uploadCompletenessView;

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //noinspection CastToConcreteClass
        ((MyApplication) getApplication()).getComponent().inject(this);
        setContentView(R.layout.activity_main2);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Initialize views
        dataCountView = (TextView) findViewById(R.id.dataCountText);
        uploadResultView = (TextView) findViewById(R.id.uploadResult);
        uploadDateView = (TextView) findViewById(R.id.lastUpload);
        uploadCompletenessView = (TextView) findViewById(R.id.uploadCompleteness);
        collectionDataStatusText = (TextView) findViewById(R.id.dataCollectionStatus);

        //Set up upload progress bar
        uploadProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        //Set up the colorable data collection background
        dataCollectionLayout = (LinearLayout) findViewById(R.id.dataCollectionGroup);

        //Set the switch
        collectDataSwitch = (Switch) findViewById(R.id.switch1);
        collectDataSwitch.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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

                            collectionDataStatusText.setText(getString(R.string.datacollectionstatusoff));
                            dataCollectionLayout.setBackgroundColor(getResources().getColor(R.color.madcap_false_color));
                            setIsCollectingData(false);
                        }
                    }
                }
        );

        Button uploadButton = (Button) findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(
                new View.OnClickListener() {
                    private final DateFormat format = DateFormat.getDateTimeInstance();

                    @Override
                    public void onClick(View v) {
                        if (mBound && (mDataCollectionService != null)) {
                            mDataCollectionService.requestUpload();
                            uploadDateView.setText(String.format(getString(R.string.lastUploadDateText), format.format(new Date())));
                            Timber.d("Upload data clicked");
                        } else {
                            Timber.w("Requested manual upload, but DataCollectionService was not bound.");
                        }
                    }
                }
        );

        TextView versionNumberText = (TextView) findViewById(R.id.versionNumberStartFragment);
        versionNumberText.setText(getString(R.string.versionIntro) + ' ' + BuildConfig.VERSION_NAME);


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
                collectionDataStatusText.setText(getString(R.string.datacollectionstatuson));
                dataCollectionLayout.setBackgroundColor(getResources().getColor(R.color.madcap_true_color));
                setIsCollectingData(true);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                // Only invoked when hosting service crashed or is killed.
                collectionDataStatusText.setText(getString(R.string.datacollectionstatusoff));
                dataCollectionLayout.setBackgroundColor(getResources().getColor(R.color.madcap_false_color));

                setIsCollectingData(false);
                mDataCollectionService = null;
                mBound = false;
                Timber.d("onServiceDisconnected");
            }
        };
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
            collectionDataStatusText.setText(getString(R.string.datacollectionstatuson));
            dataCollectionLayout.setBackgroundColor(getResources().getColor(R.color.madcap_true_color));
        } else {
            collectionDataStatusText.setText(getString(R.string.datacollectionstatusoff));
            dataCollectionLayout.setBackgroundColor(getResources().getColor(R.color.madcap_false_color));

        }

        //Set the toggle button on the last set preference configuration
        collectDataSwitch.setChecked(isCollectingData());
        uploadCompletenessView.setText(prefs.getString(getString(R.string.pref_lastUploadComplete), ""));
        //noinspection LocalVariableNamingConvention
        String lastUploadDateDefault = prefs.getString(getString(R.string.pref_lastUploadDate_default), "");
        uploadDateView.setText(String.format(getString(R.string.lastUploadDateText), prefs.getString(getString(R.string.pref_lastUploadDate), lastUploadDateDefault)));
        uploadResultView.setText(prefs.getString(getString(R.string.pref_lastUploadResult), ""));
        dataCountView.setText(String.format(getString(R.string.dataCountText), prefs.getLong(getString(R.string.pref_dataCount), 0L)));
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
        dataCountView.setText(String.format(getString(R.string.dataCountText), event.getCount()));
        mDataCount = event.getCount();
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (getString(R.string.pref_lastUploadDate).equals(key)) {
            String lastUploadDateDefault = prefs.getString(getString(R.string.pref_lastUploadDate_default), "");
            uploadDateView.setText(String.format(getString(R.string.lastUploadDateText), prefs.getString(getString(R.string.pref_lastUploadDate), lastUploadDateDefault)));
        } else if (getString(R.string.pref_lastUploadComplete).equals(key)) {
            uploadCompletenessView.setText(prefs.getString(getString(R.string.pref_lastUploadComplete), ""));
        } else if (getString(R.string.pref_lastUploadResult).equals(key)) {
            uploadResultView.setText(String.format(getString(R.string.uploadResultText), prefs.getString(getString(R.string.pref_lastUploadResult), "")));
        } else if (getString(R.string.pref_uploadProgress).equals(key)) {
            uploadProgressBar.setProgress(prefs.getInt(getString(R.string.pref_uploadProgress), 0));
        }
    }
}
