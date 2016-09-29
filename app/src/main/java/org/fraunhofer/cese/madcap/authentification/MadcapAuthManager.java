package org.fraunhofer.cese.madcap.authentification;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

/**
 * Created by MMueller on 9/29/2016.
 */
public class MadcapAuthManager implements GoogleApiClient.OnConnectionFailedListener {

    private final String TAG = "MADCAP SignIn Manager";
    private static final int RC_SIGN_IN = 9001;

    private static GoogleApiClient mGoogleApiClient;
    private static GoogleSignInOptions gso;
    private static Context context;
    private static MadcapAuthEventHandler callbackClass;

    /**
     * Configure initial sign-in to request the user's ID, email address, and basic
     * profile. ID and basic profile are included in DEFAULT_SIGN_IN.
     * @param context Context of the interacting activity.
     * @param callbackClass the Class which should execute the callback actions.
     */
    public MadcapAuthManager(Context context, MadcapAuthEventHandler callbackClass){
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                //.enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        this.context = context;
        this.callbackClass = callbackClass;
    }

    public void silentLogin(){
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            callbackClass.onSilentLoginSuccessfull(opr);
        } else {
            callbackClass.onSilentLoginFailed(opr);
        }
    }

    public void signIn(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        Log.d(TAG,signInIntent.toString());

        callbackClass.onSignInIntnet(signInIntent, RC_SIGN_IN);

    }

    public void signOut(){
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        callbackClass.onSignOutResults(status);
                    }
                });
    }

    public void revokeAccess(){
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                    callbackClass.onRevokeAccess(status);
                    }
                });
    }

    public Scope[] getGsoScopeArray(){
        return gso.getScopeArray();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Connection to Google Authenticatin failed");
    }
}
