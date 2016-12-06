package org.fraunhofer.cese.madcap;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import org.fraunhofer.cese.madcap.authentication.LogoutResultCallback;
import org.fraunhofer.cese.madcap.authentication.MadcapAuthManager;
import org.fraunhofer.cese.madcap.services.DataCollectionService;

import javax.inject.Inject;


/**
 * Provides Logout functionality to the user.
 */
public class LogoutFragment extends Fragment {
    private static final String TAG = "LogoutFragment";

    @SuppressWarnings("PackageVisibleField")
    @Inject
    MadcapAuthManager madcapAuthManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //noinspection CastToConcreteClass
        ((MyApplication) getActivity().getApplication()).getComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_logout, container, false);

        Button logoutButton = (Button) view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Called when a view has been clicked.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v) {
                MyApplication.madcapLogger.d(TAG, "Logout clicked");
                madcapAuthManager.signout(getActivity(), new LogoutResultCallback() {
                    @Override
                    public void onServicesUnavailable(int connectionResult) {
                        String text;

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

                        MyApplication.madcapLogger.e(TAG, text);

                        MyApplication.madcapLogger.w(TAG, "Google SignIn Services are unavailable. Please try to sign out again.");
                        Toast.makeText(getContext(), "Logout from MADCAP failed. " + text + " Please try again.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSignOut(Status result) {
                        if (result.getStatusCode() == CommonStatusCodes.SUCCESS) {
                            MyApplication.madcapLogger.d(TAG, "Logout succeeded. Status code: " + result.getStatusCode() + ", Message: " + result.getStatusMessage());
                            Toast.makeText(getActivity(), "You are now signed out of MADCAP.", Toast.LENGTH_SHORT).show();

                            // TODO: What to do with the user's data?
                            getActivity().stopService(new Intent(getActivity(), DataCollectionService.class));
                            MyApplication.madcapLogger.d(TAG, "Now going back to SignInActivity");
                            Intent intent = new Intent(getActivity(), SignInActivity.class);
                            intent.putExtra("distractfromsilentlogin", true);
                            startActivity(intent);
                        } else {
                            MyApplication.madcapLogger.e(TAG, "Logout failed. Status code: " + result.getStatusCode() + ", Message: " + result.getStatusMessage());
                            Toast.makeText(getActivity(), "Logout from MADCAP failed. Error code: " + result.getStatusCode() + ". Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onRevokeAccess(Status result) {
                        if (result.getStatusCode() == CommonStatusCodes.SUCCESS) {
                            MyApplication.madcapLogger.d(TAG, "Revoke access succeeded. Status code: " + result.getStatusCode() + ", Message: " + result.getStatusMessage());
                        } else {
                            MyApplication.madcapLogger.e(TAG, "Revoke access failed. Status code: " + result.getStatusCode() + ", Message: " + result.getStatusMessage());
                        }
                    }

                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        MyApplication.madcapLogger.w(TAG, "Could not connect to GoogleSignInApi.");
                        Toast.makeText(getContext(), "Logout from MADCAP failed. Could not access Google Services. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        return view;
    }
}
