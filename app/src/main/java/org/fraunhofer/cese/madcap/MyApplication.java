package org.fraunhofer.cese.madcap;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;

import org.fraunhofer.cese.madcap.authentification.MadcapAuthManager;
import org.acra.*;
import org.acra.annotation.*;

/**
 * Class used to handle lifecycle events for the entire application
 * <p>
 * Created by llayman on 9/23/2016.
 */
@ReportsCrashes(
        formUri = "https://mmueller.cloudant.com/acra-madcap/_design/acra-storage/_update/report",
        reportType = org.acra.sender.HttpSender.Type.JSON,
        httpMethod = org.acra.sender.HttpSender.Method.PUT,
        formUriBasicAuthLogin="gentytookildsolodyincenc",
        formUriBasicAuthPassword="1ae5f5f9e483e4b1beffda898b5a5ef02ffdfd65",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text
)
public class MyApplication extends Application {
    public static final String TAG = "Madcap My Application";
    private MyComponent component;

    private static GoogleApiClient mGoogleApiClient;
    private static GoogleSignInOptions gso;

    private static Context context;

    @Override
    public final void onCreate() {
        super.onCreate();

        Log.e(TAG, "on create of My application has been called");

        //Initialize Acra
        ACRA.init(this);

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
