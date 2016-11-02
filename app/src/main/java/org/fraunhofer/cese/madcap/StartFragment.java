package org.fraunhofer.cese.madcap;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import org.fraunhofer.cese.madcap.authentification.MadcapAuthManager;
import org.fraunhofer.cese.madcap.cache.UploadStatusListener;
import org.fraunhofer.cese.madcap.services.DataCollectionService;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StartFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StartFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();
    private static final String STATE_UPLOAD_STATUS = "uploadStatus";
    private static final String STATE_DATA_COUNT = "dataCount";
    private static final String STATE_COLLECTING_DATA = "isCollectingData";

    private static final long CACHE_UPDATE_UI_DELAY = 5000;
    private static final int MAX_EXCEPTION_MESSAGE_LENGTH = 20;

    private UploadStatusListener uploadStatusListener;
    private AsyncTask<Void, Long, Void> cacheCountUpdater;

    //This is the data collection service we bind to.
    private DataCollectionService mDataCollectionService;
    boolean mBound;

    private MadcapAuthManager madcapAuthManager = MadcapAuthManager.getInstance();
    private boolean isCollectingData;

    private OnFragmentInteractionListener mListener;
    private TextView nameTextView;
    private TextView collectionDataStatusText;
    private LinearLayout dataCollectionLayout;
    private Switch collectDataSwitch;
    private TextView dataCountView;
    private TextView uploadResultView;

    private String uploadResultText;
    private String dataCountText;

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to DataCollectionService, cast the IBinder and get DataCollectionService instance
            DataCollectionService.DataCollectionServiceBinder binder = (DataCollectionService.DataCollectionServiceBinder) service;
            mDataCollectionService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_UPLOAD_STATUS, uploadResultText);
        outState.putString(STATE_DATA_COUNT, dataCountText);
        outState.putBoolean(STATE_COLLECTING_DATA, isCollectingData);

        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Intent intent = new Intent(getContext(), DataCollectionService.class);
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_start, container, false);

        dataCountView = (TextView) view.findViewById(R.id.dataCountText);
        uploadResultView = (TextView) view.findViewById(R.id.uploadResult);

        if (savedInstanceState != null) {
            dataCountText = savedInstanceState.getString(STATE_DATA_COUNT);
            uploadResultText = savedInstanceState.getString(STATE_UPLOAD_STATUS);
        } else {
            dataCountText = "Computing...";
            uploadResultText = "None.";
            //isCollectingData = true;
        }


        //Parse the greeting information
        nameTextView = (TextView) view.findViewById(R.id.usernameTextview);
        if(madcapAuthManager.getLastSignedInUsersName() != null){
            nameTextView.setText(madcapAuthManager.getLastSignedInUsersName());
        }

        collectionDataStatusText = (TextView) view.findViewById(R.id.collectionDataStatusText);

        //Set the toggle button on the last set preference configuration
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        isCollectingData = prefs.getBoolean(getString(R.string.data_collection_pref), true);

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
                            Intent intent = new Intent(getContext(), DataCollectionService.class);
                            getActivity().startService(intent);
                            getActivity().bindService(intent, StartFragment.this.getmConnection(), Context.BIND_AUTO_CREATE);
                            collectionDataStatusText.setText(getString(R.string.datacollectionstatuson));
                            dataCollectionLayout.setBackgroundColor(getResources().getColor(R.color.madcap_true_color));

                            //TODO: enable pipelines
                            //enablePipelines();
                        } else {
                            isCollectingData = false;
                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean(getString(R.string.data_collection_pref), false);
                            editor.commit();
                            boolean currentCollectionState = prefs.getBoolean(getString(R.string.data_collection_pref), true);
                            MyApplication.madcapLogger.d(TAG, "Current data collection preference is now "+currentCollectionState);
                            Intent intent = new Intent(getContext(), DataCollectionService.class);
                            getActivity().unbindService(mConnection);
                            mBound = false;
                            getActivity().stopService(intent);
                            collectionDataStatusText.setText(getString(R.string.datacollectionstatusoff));
                            dataCollectionLayout.setBackgroundColor(getResources().getColor(R.color.madcap_false_color));

                            //TODO: disable pipelines
                            //disablePipelines();
                        }
                    }
                }
        );

        getCacheCountUpdater().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return view;
    }

    private AsyncTask<Void, Long, Void> getCacheCountUpdater() {
        if (cacheCountUpdater == null) {
            cacheCountUpdater = new AsyncTask<Void, Long, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    while (!isCancelled()) {
                        if(mBound){
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
        }
        return cacheCountUpdater;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
    public void onDetach() {
        super.onDetach();

        AsyncTask.Status status = getCacheCountUpdater().getStatus();
        if (!getCacheCountUpdater().isCancelled() && (status == AsyncTask.Status.PENDING || status == AsyncTask.Status.RUNNING)) {
            getCacheCountUpdater().cancel(true);
        }

        if (mBound) {
            getActivity().unbindService(mConnection);
            mBound = false;
        }
        mListener = null;
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
