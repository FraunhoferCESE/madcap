package org.fraunhofer.cese.madcap;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by MMueller on 9/30/2016.
 */

public class App extends Application {

    private static GoogleApiClient mGoogleApiClient;
    private static GoogleSignInOptions gso;
    private static Context context;

    @Override
    public void onCreate()
    {
        super.onCreate();
        context = getApplicationContext();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                //.enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onTerminate()
    {
        super.onTerminate();
    }


    public static GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    public static GoogleSignInOptions getGso() {
        return gso;
    }

    public static Context getContext(){
        return context;
    }
}
