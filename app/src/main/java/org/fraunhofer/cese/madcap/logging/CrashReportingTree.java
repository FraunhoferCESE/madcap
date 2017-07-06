package org.fraunhofer.cese.madcap.logging;

import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

import timber.log.Timber;

/**
 * Custom Timber logger to send Firebase crash reports.
 */

public class CrashReportingTree extends Timber.Tree {
    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        if ((priority == Log.VERBOSE) || (priority == Log.DEBUG) || (priority == Log.INFO)) {
            return;
        }
        FirebaseCrash.logcat(priority, tag, message);
        FirebaseCrash.report((t != null) ? t : new Exception(message));
    }
}