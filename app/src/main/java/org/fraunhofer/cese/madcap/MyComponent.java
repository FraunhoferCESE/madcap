package org.fraunhofer.cese.madcap;

import org.fraunhofer.cese.madcap.Probe.ActivityProbe.ActivityProbe;
import org.fraunhofer.cese.madcap.Probe.BluetoothProbe;
import org.fraunhofer.cese.madcap.boot.OnBootService;
import org.fraunhofer.cese.madcap.services.DataCollectionService;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Interface required by Dagger2 for objects to inject the dependency graph.
 *
 * Created by llayman on 9/23/2016.
 */

@Component(modules = MyApplicationModule.class)
@Singleton
public interface MyComponent {
    void inject(MainActivity activity);
    void inject(BluetoothProbe bluetoothProbe);
    void inject(ActivityProbe activityProbe);
    void inject(DataCollectionService dataCollectionService);
    void inject(StartFragment startFragment);
    void inject(HelpFragment helpFragment);
    void inject(LogoutFragment logoutFragment);
    void inject(QuitFragment quitFragment);
    void inject(SignInActivity signInActivity);
    void inject(WelcomeActivity welcomeActivity);
    void inject(OnBootService onBootService);
}
