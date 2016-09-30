package org.fraunhofer.cese.madcap.authentification;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

import org.fraunhofer.cese.madcap.App;

import java.io.Serializable;

import javax.inject.Singleton;

import static android.os.Build.VERSION_CODES.N;

/**
 * Created by MMueller on 9/29/2016.
 * @Singleton due to Google requirments
 */
public class MadcapAuthManager implements GoogleApiClient.OnConnectionFailedListener, Serializable{
    private static MadcapAuthManager instance = null;

    private static final String TAG = "MADCAP Auth Manager";
    private static final int RC_SIGN_IN = 9001;

    private static Context context;
    private static MadcapAuthEventHandler callbackClass;

    private static GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build();

    private static GoogleApiClient mGoogleApiClient = App.getmGoogleApiClient();
    private static GoogleSignInResult lastSignInResult;


    public static Context getContext() {
        return context;
    }

    private MadcapAuthManager(){
    }

    /**
     * Sets to contexts from an activity. Needs to be called
     * every time the activity/service acessing the class changes.
     * @param context
     */
    public static void setContext(Context context){
        MadcapAuthManager.context = context;

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                //.enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    /**
     * Sets the callback class. Needed every time, when the interacting
     * class changes.
     * @param callbackClass
     */
    public static void setCallbackClass(MadcapAuthEventHandler callbackClass){
        MadcapAuthManager.callbackClass = callbackClass;
    }

    /**
     * Performs a silent login with cached credentials
     */
    public static void silentLogin(){
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            lastSignInResult = opr.get();
            callbackClass.onSilentLoginSuccessfull(lastSignInResult);
        } else {
            callbackClass.onSilentLoginFailed(opr);
        }
    }

    public static MadcapAuthManager getInstance(){
        if(instance == null){
            instance = new MadcapAuthManager();
        }
        return instance;
    }

    /**
     * Performs a regualr login with an intent.
     */
    public static void signIn(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        Log.d(TAG,signInIntent.toString());

        callbackClass.onSignInIntnet(signInIntent, RC_SIGN_IN);

    }

    /**
     * Sign out from Google Account. Calls callbackClass.onSignOutResults.
     */
    public static void signOut(){
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        callbackClass.onSignOutResults(status);
                    }
                });
    }

    /**
     * Should be called to disconnect Google Account.
     */
    public static void revokeAccess(){
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                    callbackClass.onRevokeAccess(status);
                    }
                });
    }

    /**
     * Getter for Options
     * @return Scope Accaray
     */
    public static Scope[] getGsoScopeArray(){
        return gso.getScopeArray();
    }

    public static GoogleSignInAccount getSignInAccount(){
        return lastSignInResult.getSignInAccount();
    }

    /**
     * Makes the Signed in User accessable.
     * @return Given an last name
     */
    public static String getSignedInUsersLastName(){
        if(lastSignInResult != null){
            String givenName = lastSignInResult.getSignInAccount().getGivenName();
            String lastName = lastSignInResult.getSignInAccount().getFamilyName();
            StringBuilder nameBuilder = new StringBuilder();
            nameBuilder.append(givenName);
            nameBuilder.append(" ");
            nameBuilder.append(lastName);
            return nameBuilder.toString();
        }else return null;

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Connection to Google Authenticatin failed");
    }
}
