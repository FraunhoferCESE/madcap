package org.fraunhofer.cese.madcap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import org.fraunhofer.cese.madcap.authentification.MadcapAuthManager;
import org.fraunhofer.cese.madcap.services.DataCollectionService;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;
import static com.pathsense.locationengine.lib.detectionLogic.b.u;
import static org.fraunhofer.cese.madcap.R.id.usernameTextview;


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

    private MadcapAuthManager madcapAuthManager = MadcapAuthManager.getInstance();
    private boolean isCollectingData;

    private OnFragmentInteractionListener mListener;
    private TextView nameTextView;
    private TextView collectionDataStatusText;
    private LinearLayout dataCollectionLayout;
    private ProgressBar collectionProgressBar;
    private Switch collectDataSwitch;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_start, container, false);

        //Parse the greeting information
        nameTextView = (TextView) view.findViewById(R.id.usernameTextview);
        if(madcapAuthManager.getLastSignedInUsersName() != null){
            nameTextView.setText(madcapAuthManager.getLastSignedInUsersName());
        }

        collectionDataStatusText = (TextView) view.findViewById(R.id.collectionDataStatusText);

        //Set the toggle button on the last set preference configuration
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        isCollectingData = prefs.getBoolean(getString(R.string.data_collection_pref), true);

        //Set up collection progress bar
        collectionProgressBar = (ProgressBar) view.findViewById(R.id.collectionProgressBar);

        //Set up the colorable data collection background
        dataCollectionLayout = (LinearLayout) view.findViewById(R.id.dataCollectionLayout);

        if(isCollectingData){
            collectionDataStatusText.setText(getString(R.string.datacollectionstatuson));
            collectionProgressBar.setVisibility(View.VISIBLE);
            dataCollectionLayout.setBackgroundColor(getResources().getColor(R.color.madcap_true_color));
        }else{
            collectionDataStatusText.setText(getString(R.string.datacollectionstatusoff));
            collectionProgressBar.setVisibility(View.INVISIBLE);
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
                            collectionDataStatusText.setText(getString(R.string.datacollectionstatuson));
                            collectionProgressBar.setVisibility(View.VISIBLE);
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
                            getActivity().stopService(intent);
                            collectionDataStatusText.setText(getString(R.string.datacollectionstatusoff));
                            collectionProgressBar.setVisibility(View.INVISIBLE);
                            dataCollectionLayout.setBackgroundColor(getResources().getColor(R.color.madcap_false_color));

                            //TODO: disable pipelines
                            //disablePipelines();
                        }
                    }
                }
        );

        return view;
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
        mListener = null;
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
