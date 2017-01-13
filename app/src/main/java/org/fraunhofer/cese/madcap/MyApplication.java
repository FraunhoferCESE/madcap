package org.fraunhofer.cese.madcap;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import org.fraunhofer.cese.madcap.util.MadcapLogger;

/**
 * Class used to handle lifecycle events for the entire application
 * <p>
 * Created by llayman on 9/23/2016.
 */
//@ReportsCrashes(
//        formUri = "https://madcap.cloudant.com/acra-madcap/_design/acra-storage/_update/report",
//        reportType = org.acra.sender.HttpSender.Type.JSON,
//        httpMethod = org.acra.sender.HttpSender.Method.PUT,
//        formUriBasicAuthLogin="agioneciellacenclichasem",
//        formUriBasicAuthPassword="69f1b42d55cdfb9d7e1dc3f0a9deccb0750a64fe",
//        mode = ReportingInteractionMode.TOAST,
//        resToastText = R.string.crash_toast_text
//)
public class MyApplication extends Application {
    private static final String TAG = "MADCAP.MyApplication";
    private MyComponent component;

    @SuppressWarnings("StaticVariableOfConcreteClass")
    public static final MadcapLogger madcapLogger = new MadcapLogger();
    public static final String MADCAPVERSION = "ALPHA-A";


    @Override
    public final void onCreate() {
        super.onCreate();

        madcapLogger.d(TAG, "on create of My application has been called");

        //Initialize Acra
        //ACRA.init(this);

        //Initializations for the Google Authentification
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


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onTerminate(){
        super.onTerminate();
        Log.d(TAG, "Application terminated");
    }
}
