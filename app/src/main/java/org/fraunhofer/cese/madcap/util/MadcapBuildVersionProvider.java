package org.fraunhofer.cese.madcap.util;

import org.fraunhofer.cese.madcap.BuildConfig;

import javax.inject.Inject;

import edu.umd.fcmd.sensorlisteners.model.system.BuildVersionProvider;

/**
 * Created by llayman on 4/12/2017.
 */

public class MadcapBuildVersionProvider implements BuildVersionProvider {

    @Inject
    MadcapBuildVersionProvider() {}

    @Override
    public String getBuildVersion() {
        return BuildConfig.VERSION_NAME;
    }
}
