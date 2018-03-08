package org.fraunhofer.cese.madcap;

import org.fraunhofer.cese.madcap.authentication.SignInActivity;
import org.fraunhofer.cese.madcap.authorization.AuthorizationActivity;
import org.fraunhofer.cese.madcap.boot.OnBootService;
import org.fraunhofer.cese.madcap.services.DataCollectionService;
import org.fraunhofer.cese.madcap.services.WifiService;
import org.fraunhofer.cese.madcap.util.ManualProbeUploadTaskFactory;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Interface required by Dagger2 for objects to inject the dependency graph.
 * <p>
 * Created by llayman on 9/23/2016.
 */

@SuppressWarnings("InterfaceNeverImplemented")
@Component(modules = MyApplicationModule.class)
@Singleton
public interface MyComponent {
    void inject(MainActivity activity);

    void inject(SettingsActivity settingsActivity);

    void inject(WifiService wifiService);

    void inject(DataCollectionService dataCollectionService);

    void inject(SignInActivity signInActivity);

    void inject(WelcomeActivity welcomeActivity);

    void inject(OnBootService onBootService);

    void inject(ManualProbeUploadTaskFactory manualProbeUploadTaskFactory);

    void inject(AuthorizationActivity authorizationActivity);

    void inject(PermissionsActivity permissionsActivity);

   // void inject(WifiService wifiService);
}
