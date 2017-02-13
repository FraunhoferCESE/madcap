package org.fraunhofer.cese.madcap;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
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

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection;
    private DataCollectionService mDataCollectionService;
    boolean mBound = false;


    private void bindConnection(Intent intent){
        MyApplication.madcapLogger.d(TAG, "Attempt to bind self. Current bound status is "+mBound);
        if(!mBound){
            getActivity().getApplicationContext().bindService(intent , mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private void unbindConnection(){
        MyApplication.madcapLogger.d(TAG, "Attempt to unbind self. Current bound status is "+mBound);
        //mDataCollectionService.removeUploadListener(uploadStatusListener);
        getActivity().getApplicationContext().unbindService(mConnection);
        mBound = false;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //noinspection CastToConcreteClass
        ((MyApplication) getActivity().getApplication()).getComponent().inject(this);

        mConnection = new ServiceConnection() {
            private final String TAG = getClass().getSimpleName();

            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                // We've bound to DataCollectionService, cast the IBinder and get DataCollectionService instance
                Log.d(TAG, "New service connection service "+service.toString());
                DataCollectionService.DataCollectionServiceBinder binder = (DataCollectionService.DataCollectionServiceBinder) service;
                mDataCollectionService = binder.getService();
                //mDataCollectionService.addUploadListener(getUploadStatusListener());
                mBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                // Only invoked when hosting service crashed or is killed.
                mBound = false;
            }
        };

        Intent intent = new Intent(getActivity().getApplicationContext(), DataCollectionService.class);
        bindConnection(intent);
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

                            mDataCollectionService.sendLogOutProbe();
                            getActivity().stopService(new Intent(getActivity(), DataCollectionService.class));
                            MyApplication.madcapLogger.d(TAG, "Now going back to SignInActivity");
                            Intent intent = new Intent(getActivity(), SignInActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

    /**
     * Called when the fragment is no longer in use.  This is called
     * after {@link #onStop()} and before {@link #onDetach()}.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        unbindConnection();
    }
}
