package org.fraunhofer.cese.madcap.util;

import org.fraunhofer.cese.madcap.BuildConfig;

import javax.inject.Inject;

import edu.umd.fcmd.sensorlisteners.model.system.BuildVersionProvider;

/**
 * Provides current build version. Needed for SystemListener.
 */
public class MadcapBuildVersionProvider implements BuildVersionProvider {

    @Inject
    MadcapBuildVersionProvider() {}

    @Override
    public String getBuildVersion() {
        return BuildConfig.VERSION_NAME;
    }
}
