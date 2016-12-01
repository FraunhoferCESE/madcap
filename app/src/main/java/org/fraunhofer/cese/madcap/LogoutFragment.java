package org.fraunhofer.cese.madcap;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.fraunhofer.cese.madcap.authentication.MadcapAuthManager;
import org.fraunhofer.cese.madcap.services.DataCollectionService;

import javax.inject.Inject;
import javax.inject.Named;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LogoutFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class LogoutFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();

    @Inject
    MadcapAuthManager madcapAuthManager;

    @Inject
    @Named("SigninApi")
    GoogleApiClient mGoogleApiClient;

    private OnFragmentInteractionListener mListener;

    private Button logoutButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MyApplication) getActivity().getApplication()).getComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_logout, container, false);

        logoutButton = (Button) view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                MyApplication.madcapLogger.d(TAG, "Logout clicked");
                if (checkApiAvailability()) {
                    if(mGoogleApiClient.isConnected()) {
                        signout();
                    }
                    else {
                        mGoogleApiClient.registerConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                                mGoogleApiClient.unregisterConnectionFailedListener(this);
                                MyApplication.madcapLogger.w(TAG, "Could not connect to GoogleSignInApi.");
                                Toast.makeText(getContext(), "Logout from MADCAP failed. Could not access Google Services. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(@Nullable Bundle bundle) {
                                mGoogleApiClient.unregisterConnectionCallbacks(this);
                                MyApplication.madcapLogger.d(TAG, "Connected to Google SignIn services...");
                                signout();
                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                MyApplication.madcapLogger.w(TAG, "onConnectionSuspended: Unexpected suspension of connection. Error code: " + i);
                                Toast.makeText(getContext(), "Logout from MADCAP failed. Connection lost to Google Services. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
                        mGoogleApiClient.connect();
                    }
                }
            }
        });

        return view;
    }

    private void signout() {
        final Context context = getContext();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status r) {
                        if (r.getStatusCode() == CommonStatusCodes.SUCCESS) {
                            MyApplication.madcapLogger.d(TAG, "Logout succeeded. Status code: " + r.getStatusCode() + ", Message: " + r.getStatusMessage());
                            Toast.makeText(context, "You are now signed out of MADCAP.", Toast.LENGTH_SHORT).show();

                            // TODO: What to do with the user's data?
                            getActivity().stopService(new Intent(context, DataCollectionService.class));
                            madcapAuthManager.setUser(null);
                        } else {
                            MyApplication.madcapLogger.e(TAG, "Logout failed. Status code: " + r.getStatusCode() + ", Message: " + r.getStatusMessage());
                            Toast.makeText(context, "Logout from MADCAP failed. Error code: " + r.getStatusCode() + ". Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status r) {
                        MyApplication.madcapLogger.d(TAG, "Revoke access finished. Status code: " + r.getStatusCode() + ", Message: " + r.getStatusMessage());
                    }
                });
        MyApplication.madcapLogger.d(TAG, "Now going back to SignInActivity");
        Intent intent = new Intent(getActivity(), SignInActivity.class);
        intent.putExtra("distractfromsilentlogin", true);
        startActivity(intent);
    }


    private boolean checkApiAvailability() {
        int connectionResult = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());
        boolean isAvailable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext()) == ConnectionResult.SUCCESS;

        String text;

        if (!isAvailable) {

            switch (connectionResult) {
                case ConnectionResult.SERVICE_MISSING:
                    text = "Play services not available, please install them and retry.";
                    break;
                case ConnectionResult.SERVICE_UPDATING:
                    text = "Play services are currently updating, please wait and try again.";
                    break;
                case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                    text = "Play services not up to date. Please update your system and try again.";
                    break;
                case ConnectionResult.SERVICE_DISABLED:
                    text = "Play services are disabled. Please enable and try again.";
                    break;
                case ConnectionResult.SERVICE_INVALID:
                    text = "Play services are invalid. Please reinstall them and try again.";
                    break;
                default:
                    text = "Play services connection failed. Error code: " + connectionResult;
            }

            MyApplication.madcapLogger.d(TAG, text);
        }

        return isAvailable;
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
