package edu.umd.fcmd.sensorlisteners.listener.applications;

import android.content.Context;

import java.util.Calendar;

/**
 * Created by MMueller on 11/17/2016.
 */

public class TimedApplicationTaskFactory {

    public TimedApplicationTask create(ApplicationsListener applicationsListener, Context context, Calendar calendar){
        return new TimedApplicationTask(applicationsListener, context, calendar);
    }
}
