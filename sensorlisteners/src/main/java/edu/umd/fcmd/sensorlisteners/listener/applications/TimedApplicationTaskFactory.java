package edu.umd.fcmd.sensorlisteners.listener.applications;

import android.content.Context;

import javax.inject.Inject;

import edu.umd.fcmd.sensorlisteners.issuehandling.PermissionsManager;

/**
 * Created by MMueller on 11/17/2016.
 *
 * Factory for TimedApplication Tasks
 */

public class TimedApplicationTaskFactory {

    @Inject
    public TimedApplicationTaskFactory() {}

    public TimedApplicationTask create(ApplicationsListener applicationsListener, Context context, PermissionsManager permissionsManager){
        return new TimedApplicationTask(applicationsListener, context, permissionsManager);
    }
}
