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
import org.fraunhofer.cese.madcap.authentication.AuthenticationProvider;
import org.fraunhofer.cese.madcap.authentication.SignInActivity;
import org.fraunhofer.cese.madcap.services.DataCollectionService;

import javax.inject.Inject;


/**
 * Provides Logout functionality to the user.
 */
public class LogoutFragment extends Fragment {
    private static final String TAG = "LogoutFragment";

    @SuppressWarnings("PackageVisibleField")
    @Inject
    AuthenticationProvider authenticationProvider;

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
                authenticationProvider.signout(getActivity(), new LogoutResultCallback() {
                    @Override
                    public void onServicesUnavailable(int connectionResult) {
                        String text;

                        switch (connectionResult) {
                            case ConnectionResult.SERVICE_MISSING:
                                text = getString(R.string.play_services_missing);
                                break;
                            case ConnectionResult.SERVICE_UPDATING:
                                text = getString(R.string.play_services_updating);
                                break;
                            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                                text = getString(R.string.play_services_need_updated);
                                break;
                            case ConnectionResult.SERVICE_DISABLED:
                                text = getString(R.string.play_services_disabled);
                                break;
                            case ConnectionResult.SERVICE_INVALID:
                                text = getString(R.string.play_services_invalid);
                                break;
                            default:
                                text = String.format(getString(R.string.play_services_connection_failed), connectionResult);
                        }

                        MyApplication.madcapLogger.e(TAG, text);

                        MyApplication.madcapLogger.w(TAG, "Google SignIn Services are unavailable. Please try to sign out again.");
                        Toast.makeText(getContext(), "Logout from MADCAP failed. " + text + " Please try again.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSignOut(Status result) {
                        if (result.getStatusCode() == CommonStatusCodes.SUCCESS) {
                            MyApplication.madcapLogger.d(TAG, "Logout succeeded. Status code: " + result.getStatusCode() + ", Message: " + result.getStatusMessage());
                            Toast.makeText(getActivity(), R.string.post_sign_out, Toast.LENGTH_SHORT).show();

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
