package edu.umd.fcmd.sensorlisteners.listener.applications;

import android.content.Context;

import java.util.Calendar;

import edu.umd.fcmd.sensorlisteners.issuehandling.PermissionDeniedHandler;

/**
 * Created by MMueller on 11/17/2016.
 */

public class TimedApplicationTaskFactory {

    public TimedApplicationTask create(ApplicationsListener applicationsListener, Context context, PermissionDeniedHandler permissionDeniedHandler){
        return new TimedApplicationTask(applicationsListener, context, permissionDeniedHandler);
    }
}
