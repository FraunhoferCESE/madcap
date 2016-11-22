package org.fraunhofer.cese.madcap;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.fraunhofer.cese.madcap.authentication.MadcapAuthManager;

import javax.inject.Inject;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link QuitFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link QuitFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuitFragment extends Fragment {
    @Inject
    private MadcapAuthManager madcapAuthManager;
    private Button quitButton;

    private final String TAG = this.getClass().getSimpleName();

    private OnFragmentInteractionListener mListener;

    public QuitFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MyApplication) getActivity().getApplication()).getComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_quit, container, false);

        quitButton = (Button) view.findViewById(R.id.quitButton);
        quitButton.setOnClickListener(new View.OnClickListener(){
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{getString(R.string.contactEmail1), getString(R.string.contactEmail2)});
                i.putExtra(Intent.EXTRA_SUBJECT, "QUIT Pocket Security ");
                i.putExtra(Intent.EXTRA_TEXT   , "Hello Pocket Security Team, \n \n I want to quit Pocket Security. \n(You may write your reasons here.) \n" +
                        " \n \n " +madcapAuthManager.getLastSignedInUsersName()+" \n" +
                        " \n Reference User ID: "+ madcapAuthManager.getUserId()+" (please do not remove this)");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    MyApplication.madcapLogger.e(TAG, "No email client installed");
                }
            }
        });

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
        void onFragmentInteraction(Uri uri);
    }
}
