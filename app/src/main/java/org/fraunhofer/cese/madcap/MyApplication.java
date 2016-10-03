package org.fraunhofer.cese.madcap;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;

import org.fraunhofer.cese.madcap.authentification.MadcapAuthManager;


/**
 * Class used to handle lifecycle events for the entire application
 * <p>
 * Created by llayman on 9/23/2016.
 */
public class MyApplication extends Application {
    private MyComponent component;

    private static GoogleApiClient mGoogleApiClient;
    private static GoogleSignInOptions gso;

    private static Context context;

    @Override
    public final void onCreate() {
        super.onCreate();

        //Initializations for the Google Authentification
        context = getApplicationContext();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                //.enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        MadcapAuthManager.setUp(gso, mGoogleApiClient);

        // Initialize the Component used to inject dependencies.
        component = DaggerMyComponent.builder()
                .myApplicationModule(new MyApplicationModule(this))
                .build();
    }

    /**
     * Get the Dagger2 {@link dagger.Component} that can be used to initialize the dependency injection.
     *
     * @return the Dagger2 {@link dagger.Component} that can be used to initialize the dependency injection.
     */
    public final MyComponent getComponent() {
        return component;
    }

}
