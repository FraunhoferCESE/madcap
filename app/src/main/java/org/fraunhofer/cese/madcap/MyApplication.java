package org.fraunhofer.cese.madcap;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import org.fraunhofer.cese.madcap.logging.CrashReportingTree;

import timber.log.Timber;

/**
 * Class used to handle lifecycle events for the entire application
 * <p>
 * Created by llayman on 9/23/2016.
 */

public class MyApplication extends Application {
    private static final String TAG = "MADCAP.MyApplication";
    private MyComponent component;

    @Override
    public final void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }

        Timber.d(TAG, "on create of My application has been called");

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
        Timber.d(TAG, "Application terminated");
    }
}
