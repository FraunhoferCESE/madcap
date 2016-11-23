package org.fraunhofer.cese.madcap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import org.fraunhofer.cese.madcap.authentification.MadcapAuthManager;
import org.fraunhofer.cese.madcap.cache.Cache;
import org.fraunhofer.cese.madcap.cache.RemoteUploadResult;
import org.fraunhofer.cese.madcap.cache.UploadStatusGuiListener;
import org.fraunhofer.cese.madcap.cache.UploadStatusListener;
import org.fraunhofer.cese.madcap.services.DataCollectionService;

import java.text.DateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.fraunhofer.cese.madcap.cache.UploadStatusGuiListener.Completeness.COMPLETE;
import static org.fraunhofer.cese.madcap.cache.UploadStatusGuiListener.Completeness.INCOMPLETE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StartFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StartFragment extends Fragment implements UploadStatusGuiListener {
    private final String TAG = getClass().getSimpleName();
    private static final String STATE_UPLOAD_STATUS = "uploadStatus";
    private static final String STATE_DATA_COUNT = "dataCount";
    private static final String STATE_COLLECTING_DATA = "isCollectingData";

    private static final long CACHE_UPDATE_UI_DELAY = 5000;
    private static final int MAX_EXCEPTION_MESSAGE_LENGTH = 20;

    //private UploadStatusListener uploadStatusListener;
    private AsyncTask<Void, Long, Void> cacheCountUpdater;

    //This is the data collection service we bind to.
    private DataCollectionService mDataCollectionService;
    boolean mBound;

    private MadcapAuthManager madcapAuthManager = MadcapAuthManager.getInstance();
    private boolean isCollectingData;

    //Ui elements
    private OnFragmentInteractionListener mListener;
    private TextView nameTextView;
    private TextView collectionDataStatusText;
    private LinearLayout dataCollectionLayout;
    private Switch collectDataSwitch;
    private ProgressBar uploadProgressBar;
    private TextView dataCountView;
    private TextView uploadResultView;
    private TextView uploadDateView;
    private TextView uploadCompletenessView;
    private Button uploadButton;

    //Models for Ui elements
    private String uploadResultText;
    private String dataCountText;

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection;

    private void bindConnection(Intent intent){
        MyApplication.madcapLogger.d(TAG, "Attempt to bind self. Current bound status is "+mBound);
        if(!mBound){
            getActivity().getApplicationContext().bindService(intent , mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private void unbindConnection(){
        MyApplication.madcapLogger.d(TAG, "Attempt to unbind self. Current bound status is "+mBound);
        //mDataCollectionService.removeUploadListener(uploadStatusListener);
        mDataCollectionService.setUploadStatusGuiListener(null);
        getActivity().getApplicationContext().unbindService(mConnection);
        Log.d(TAG, "removed UploadListener");
        mBound = false;
    }

    public StartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment StartFragment.
     */

    public static StartFragment newInstance() {
        StartFragment fragment = new StartFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                Log.d(TAG, "onServiceConnected");
                // We've bound to DataCollectionService, cast the IBinder and get DataCollectionService instance
                Log.e(TAG, "New service connection service "+service.toString());
                DataCollectionService.DataCollectionServiceBinder binder = (DataCollectionService.DataCollectionServiceBinder) service;
                mDataCollectionService = binder.getService();
                //mDataCollectionService.addUploadListener(getUploadStatusListener());
                mDataCollectionService.setUploadStatusGuiListener(getStatusGuiListener());
                mBound = true;
                Log.d(TAG, "added UploadListener");
                getCacheCountUpdater().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                // Only invoked when hosting service crashed or is killed.
                //mDataCollectionService.removeUploadListener(getUploadStatusListener());
                mDataCollectionService.setUploadStatusGuiListener(null);
                mBound = false;
                Log.d(TAG, "removed UploadListener");
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_start, container, false);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        isCollectingData = prefs.getBoolean(getString(R.string.data_collection_pref), true);

        Intent intent = new Intent(getActivity().getApplicationContext(), DataCollectionService.class);
        if(prefs.getBoolean(getString(R.string.data_collection_pref), true)){
            Log.d(TAG, "now binding connection");
            bindConnection(intent);
        }else {
            mBound =false;
        }

        dataCountView = (TextView) view.findViewById(R.id.dataCountText);
        dataCountView.setText(dataCountText);
        uploadResultView = (TextView) view.findViewById(R.id.uploadResult);
        uploadDateView = (TextView) view.findViewById(R.id.uploadDate);
        uploadCompletenessView = (TextView) view.findViewById(R.id.uploadCompleteness);

        if (savedInstanceState != null) {
            dataCountText = savedInstanceState.getString(STATE_DATA_COUNT);
            uploadResultText = savedInstanceState.getString(STATE_UPLOAD_STATUS);
        } else {
            dataCountText = "Computing...";
            //isCollectingData = true;
        }

        //Parse the greeting information
        nameTextView = (TextView) view.findViewById(R.id.usernameTextview);
        if(madcapAuthManager.getLastSignedInUsersName() != null){
            nameTextView.setText(madcapAuthManager.getLastSignedInUsersName());
        }

        collectionDataStatusText = (TextView) view.findViewById(R.id.collectionDataStatusText);

        //Set the toggle button on the last set preference configuration

        //Set up upload progress bar
        uploadProgressBar = (ProgressBar) view.findViewById(R.id.uploadProgressBar);

        //Set up the colorable data collection background
        dataCollectionLayout = (LinearLayout) view.findViewById(R.id.dataCollectionLayout);

        if(isCollectingData){
            collectionDataStatusText.setText(getString(R.string.datacollectionstatuson));
            dataCollectionLayout.setBackgroundColor(getResources().getColor(R.color.madcap_true_color));
        }else{
            collectionDataStatusText.setText(getString(R.string.datacollectionstatusoff));
            dataCollectionLayout.setBackgroundColor(getResources().getColor(R.color.madcap_false_color));
        }

        //Set the switch
        collectDataSwitch = (Switch) view.findViewById(R.id.switch1);
        collectDataSwitch.setChecked(isCollectingData);

        collectDataSwitch.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            isCollectingData = true;
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean(getString(R.string.data_collection_pref), true);
                            editor.commit();
                            boolean currentCollectionState = prefs.getBoolean(getString(R.string.data_collection_pref), true);
                            MyApplication.madcapLogger.d(TAG, "Current data collection preference is now "+currentCollectionState);
                            Intent intent = new Intent(getActivity().getApplicationContext(), DataCollectionService.class);
                            getActivity().getApplicationContext().startService(intent);
                            bindConnection(intent);
                            collectionDataStatusText.setText(getString(R.string.datacollectionstatuson));
                            dataCollectionLayout.setBackgroundColor(getResources().getColor(R.color.madcap_true_color));
                        } else {
                            isCollectingData = false;
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean(getString(R.string.data_collection_pref), false);
                            editor.commit();
                            boolean currentCollectionState = prefs.getBoolean(getString(R.string.data_collection_pref), true);
                            MyApplication.madcapLogger.d(TAG, "Current data collection preference is now "+currentCollectionState);
                            Intent intent = new Intent(getActivity().getApplicationContext(), DataCollectionService.class);
                            if(mBound){
                                unbindConnection();
                            }
                            getActivity().getApplicationContext().stopService(intent);
                            collectionDataStatusText.setText(getString(R.string.datacollectionstatusoff));
                            dataCollectionLayout.setBackgroundColor(getResources().getColor(R.color.madcap_false_color));
                        }
                    }
                }
        );

        uploadButton = (Button) view.findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(
                new View.OnClickListener() {
                    private final DateFormat df = DateFormat.getDateTimeInstance();

                    @Override
                    public void onClick(View view) {
                        mDataCollectionService.requestUpload();

                        String text = "\nUpload requested on " + df.format(new Date()) + "\n";
                        MyApplication.madcapLogger.d(TAG, "Upload data clicked");
                        onUploadStatusCompletenessUpdate(Completeness.INCOMPLETE);

//                        int status = mDataCollectionService.requestUpload();
//                        if (status == Cache.UPLOAD_READY)
//                            text += "Upload started...";
//                        else if (status == Cache.UPLOAD_ALREADY_IN_PROGRESS)
//                            text += "Upload in progress...";
//                        else {
//                            String errorText = "";
//                            if ((status & Cache.INTERNAL_ERROR) == Cache.INTERNAL_ERROR)
//                                errorText += "\n- An internal error occurred and data could not be uploaded.";
//                            if ((status & Cache.UPLOAD_INTERVAL_NOT_MET) == Cache.UPLOAD_INTERVAL_NOT_MET)
//                                errorText += "\n- An upload was just requested; please wait a few seconds.";
//                            if ((status & Cache.NO_INTERNET_CONNECTION) == Cache.NO_INTERNET_CONNECTION)
//                                errorText += "\n- No WiFi connection detected.";
//                            if ((status & Cache.DATABASE_LIMIT_NOT_MET) == Cache.DATABASE_LIMIT_NOT_MET)
//                                errorText += "\n- No entries to upload";
//
//                            text += !errorText.isEmpty() ? "Error:" + errorText : "No status to report. Please wait.";
//                        }
//                        uploadResultText = text;
//                        uploadResultView.setText(uploadResultText);
                    }
                }
        );

        restoreLastUpload();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            MyApplication.madcapLogger.d(TAG, "NOT NULL");
            dataCountText = savedInstanceState.getString(STATE_DATA_COUNT);
            uploadResultText = savedInstanceState.getString(STATE_UPLOAD_STATUS);
        } else {
            dataCountText = "Computing...";
            uploadResultText = "None.";
            //isCollectingData = true;
        }
        dataCountView.setText(dataCountText);
        //uploadResultView.setText(uploadResultText);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_UPLOAD_STATUS, uploadResultText);
        outState.putString(STATE_DATA_COUNT, dataCountText);
        outState.putBoolean(STATE_COLLECTING_DATA, isCollectingData);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onResume(){
        super.onResume();

        Intent intent = new Intent(getActivity().getApplicationContext(), DataCollectionService.class);
        bindConnection(intent);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mBound){
            unbindConnection();
        }
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        MyApplication.madcapLogger.d(TAG, "onDestroy Fragment");

        AsyncTask.Status status = getCacheCountUpdater().getStatus();
        if (!getCacheCountUpdater().isCancelled() && (status == AsyncTask.Status.PENDING || status == AsyncTask.Status.RUNNING)) {
            getCacheCountUpdater().cancel(true);
        }

        mListener = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private AsyncTask<Void, Long, Void> getCacheCountUpdater() {
        cacheCountUpdater = new AsyncTask<Void, Long, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                while (!isCancelled()) {
                    if(mBound){
                       // MyApplication.madcapLogger.d(TAG, "cache size "+mDataCollectionService.getCacheSize());
                        publishProgress(mDataCollectionService.getCacheSize());
                    }
                    try {
                        Thread.sleep(CACHE_UPDATE_UI_DELAY);
                    } catch (InterruptedException e) {
                        MyApplication.madcapLogger.i("Fraunhofer.CacheCounter", "Cache counter task to update UI thread has been interrupted.");
                    }
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Long... values) {
                updateDataCount(values[0]);
            }
        };

        return cacheCountUpdater;
    }

    private UploadStatusGuiListener getStatusGuiListener(){
        return (UploadStatusGuiListener) this;
    }

    private void restoreLastUpload(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());

        // Restore completeness
        switch(prefs.getString(getString(R.string.last_upload_completeness), "")){
            case "Status: Complete":
                onUploadStatusCompletenessUpdate(Completeness.COMPLETE);
                break;
            case "Status: Inomplete":
                onUploadStatusCompletenessUpdate(Completeness.INCOMPLETE);
                break;
            default:
                MyApplication.madcapLogger.e(TAG, "Wrong completeness status saved");
                break;
        }

        //Restore date
        onUploadStatusDateUpdate(prefs.getString(getString(R.string.last_upload_date), "You have not uploaded any data yet."));

        //Restore result
        onUploadStatusResultUpdate(prefs.getString(getString(R.string.last_upload_result), ""));

    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    protected ServiceConnection getmConnection() {
        return mConnection;
    }

    private void updateDataCount(long count) {
        dataCountText = count < 0 ? "Computing..." : Long.toString(count);

        if (dataCountView != null && dataCountView.isShown()) {
            dataCountView.setText(getString(R.string.dataCountText)+" "+dataCountText);
        }
    }

    /**
     * Getting called when there is an update on the date of the upload status.
     *
     * @param date the date to update.
     */
    @Override
    public void onUploadStatusDateUpdate(String date) {
        uploadDateView.setText(date);
    }

    /**
     * Getting called when there is an update on the completeness is available.
     *
     * @param completeness the new completeness status.
     */
    @Override
    public void onUploadStatusCompletenessUpdate(Completeness completeness) {
        switch (completeness){
            case COMPLETE:
                uploadCompletenessView.setText(getString(R.string.status_complete));
                break;
            case INCOMPLETE:
                uploadCompletenessView.setText(getString(R.string.status_incomplete));
                break;
            default:
                break;
        }

    }

    /**
     * Getting called when there is an update on the result available.
     *
     * @param result the new result.
     */
    @Override
    public void onUploadStatusResultUpdate(String result) {
        uploadResultView.setText(result);
    }

    /**
     * Getting called when there is an update on the progress of the upload
     * process available.
     *
     * @param percent the percentage of the upload.
     */
    @Override
    public void onUploadStatusProgressUpdate(int percent) {
        uploadProgressBar.setProgress(percent);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
