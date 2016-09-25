package org.fraunhofer.cese.madcap;

import android.app.Application;


/**
 * Class used to handle lifecycle events for the entire application
 * <p>
 * Created by llayman on 9/23/2016.
 */
public class MyApplication extends Application {
    private MyComponent component;

    @Override
    public final void onCreate() {
        super.onCreate();

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
    final MyComponent getComponent() {
        return component;
    }

}
