package org.fraunhofer.cese.madcap;

import org.fraunhofer.cese.madcap.Probe.ActivityProbe.ActivityProbe;
import org.fraunhofer.cese.madcap.Probe.BluetoothProbe;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Interface required by Dagger2 for objects (such as {@link MainActivity} inject the dependency graph.
 *
 * Created by llayman on 9/23/2016.
 */

@Component(modules = MyApplicationModule.class)
@Singleton
interface MyComponent {
    void inject(MainActivity activity);
    void inject(BluetoothProbe bluetoothProbe);
    void inject(ActivityProbe activityProbe);
}
