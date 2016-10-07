package org.fraunhofer.cese.madcap.authentification;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;

import java.io.Serializable;

/**
 * Created by MMueller on 9/29/2016.
 */
public class MadcapAuthManager implements OnConnectionFailedListener, Serializable{
    private static MadcapAuthManager instance = null;

    private static final String TAG = "MADCAP Auth Manager";
    public static final int RC_SIGN_IN = 9001;

    private static MadcapAuthEventHandler callbackClass = null;

    @Nullable
    private static GoogleSignInOptions gso = null;
    @Nullable
    private static GoogleApiClient mGoogleApiClient = null;

    @Nullable
    private static GoogleSignInResult lastSignInResult = null;
    private static GoogleSignInApi googleSignInApi = Auth.GoogleSignInApi;

    /**
     * Getter for the API Client.
     * @return Bond Api client.
     */
    public static GoogleApiClient getMGoogleApiClient() {
        return mGoogleApiClient;
    }

    /**
     * Getter for the Sign in Options
     * @return Bond Sign In Options.
     */
    @Nullable
    public static GoogleSignInOptions getGso() {
        return gso;
    }

    /**
     * Getter for the callback class.
     * @return Callback class which needs to implement MadcapAuthEventHandler
     */
    public static MadcapAuthEventHandler getCallbackClass() {
        return callbackClass;
    }

    /**
     * Empty private Constructor making sure that it is a singleton.
     */
    private MadcapAuthManager() {
        super();
    }

    public static void setUp(GoogleSignInOptions gso, GoogleApiClient mGoogleApiClient){
        if(MadcapAuthManager.gso == null && MadcapAuthManager.mGoogleApiClient == null){
            MadcapAuthManager.gso = gso;
            MadcapAuthManager.mGoogleApiClient = mGoogleApiClient;
        }else{
            Log.e(TAG, "Setup failed, because it is already set up");
        }

    }

    /**
     * Sets the callback class. Needed every time, when the interacting
     * class changes.
     * @param callbackClass The class which implments MadcapAuthEventHandler.
     */
    public static void setCallbackClass(MadcapAuthEventHandler callbackClass){
        MadcapAuthManager.callbackClass = callbackClass;
    }

    /**
     * Performs a silent login with cached credentials
     */
    public static void silentLogin(){
        OptionalPendingResult<GoogleSignInResult> opr = googleSignInApi.silentSignIn(mGoogleApiClient);

        Log.d(TAG, "First silent sign in result: "+opr.isDone());

        if (opr.isDone()) {
            // In Case there is a result available intermediately.
            Log.d(TAG, "Immediate result available ");
            lastSignInResult = opr.get();
            callbackClass.onSilentLoginSuccessfull(lastSignInResult);
        } else {
            // In case no immediate result available.
            Log.d(TAG, "Immediate result NOT available ");
            silentLogin(0);
            /*
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult r) {
                    callbackClass.onSilentLoginSuccessfull(lastSignInResult);
                }
            });
            */
        }
    }

    /**
     * Tries the silent login again, recursively maximum of 10 times with 1 second
     * pause in between.
     * @param attempt current attempt number.
     */
    private static void silentLogin(int attempt){
        int maxAttempt = 10;
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        OptionalPendingResult<GoogleSignInResult> opr = googleSignInApi.silentSignIn(mGoogleApiClient);
        if(attempt < maxAttempt){
           if(opr.isDone()){
               Log.d(TAG, "Result in attempt "+attempt+" available ");
               lastSignInResult = opr.get();
               callbackClass.onSilentLoginSuccessfull(lastSignInResult);
           }else{
               Log.d(TAG, "Result in attempt "+attempt+" not available. Trying again ");
               silentLogin(attempt+1);
           }
        }else{
            Log.d(TAG, "Result for Login could not be retrieved. Need to log in manually");
            callbackClass.onSilentLoginFailed(opr);
        }
    }

    /**
     * Sometimes needed for getting the explicit instance of the singleton.
     * @return The singleton instance.
     */
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
        Intent signInIntent = googleSignInApi.getSignInIntent(mGoogleApiClient);
        Log.d(TAG,signInIntent.toString());

        callbackClass.onSignInIntent(signInIntent, RC_SIGN_IN);

    }

    /**
     * Sign out from Google Account. Calls callbackClass.onSignOutResults.
     */
    public static void signOut(){
        googleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status r) {
                        Log.e(TAG, "Logout status "+ r);
                        MadcapAuthManager.lastSignInResult = null;
                        //revokeAccess();
                        callbackClass.onSignOutResults(r);
                    }
                });
    }

    /**
     * Retrieves the currently logged in User Id.
     * @return User ID.
     */
    public static String getUserId(){
        if(lastSignInResult != null){
            return lastSignInResult.getSignInAccount().getId();
        }else{
            return null;
        }

    }

    /**
     * Should be called to disconnect Google Account.
     */
    public static void revokeAccess(){
        googleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status r) {
                callbackClass.onRevokeAccess(r);
            }
        });
    }



    /**
     * Getter for Options
     * @return Scope Accaray
     */
    @Nullable
    public static Scope[] getGsoScopeArray(){
        if (gso != null) {
            return gso.getScopeArray();
        }
        return null;
    }

    public static GoogleSignInAccount getSignInAccount(){
        if(lastSignInResult != null){
            return lastSignInResult.getSignInAccount();
        }else{
            return null;
        }

    }

    /**
     * Makes the Signed in User accessable.
     * @return the last users name.
     */
    @Nullable
    public static String getLastSignedInUsersName(){

        if(lastSignInResult != null){
            String givenName = lastSignInResult.getSignInAccount().getGivenName();
            String lastName = lastSignInResult.getSignInAccount().getFamilyName();
            StringBuilder nameBuilder = new StringBuilder(3);
            nameBuilder.append(givenName);
            nameBuilder.append(' ');
            nameBuilder.append(lastName);
            return nameBuilder.toString();
        }else{
            Log.e(TAG, "No last user cached");
            return null;
        }
    }

    /**
     * Gets the last Sign in Result.
     * For testing purposes only.
     * @deprecated
     * @return The last cached SignInResult.
     */
    @Nullable
    public static GoogleSignInResult getLastSignInResult() {
        return lastSignInResult;
    }

    /**
     * Sets the last Sign in result.
     * For testing purposes only.
     * @deprecated
     * @param googleSignInResult The last SignInResult to be set.
     */
    protected static void setLastSignInResult(@Nullable GoogleSignInResult googleSignInResult){
        lastSignInResult = googleSignInResult;
    }


    /**
     * Handles the sign in result, being called from the activity
     * which is implmenting AdcapAuthEventHandler
     * @param result The result to be parsed.
     */
    public static void handleSignInResult(GoogleSignInResult result){
        lastSignInResult = result;
    }

    /**
     * Connects the Google Api client.
     * Needs to be called whenever the Activity changes.
     */
    public static void connect(){
        mGoogleApiClient.connect();
    }

    /**
     * Sets the google SignInApi to a custom SignIn Api.
     * @deprecated for testing use only
     * @param googleSignInApi
     */
    protected static void setGoogleSignInApi(GoogleSignInApi googleSignInApi){
        MadcapAuthManager.googleSignInApi = googleSignInApi;
    }

    /**
     * For testing purposes only. Should never be called in code,
     * would cause lots of errors otherway, because the objects should
     * only be set once.
     * @deprecated
     */
    @Deprecated
    protected static void reset(){
        gso = null;
        mGoogleApiClient = null;
    }

    @Override
    public final void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Connection to Google Authenticatin failed");
    }
}
