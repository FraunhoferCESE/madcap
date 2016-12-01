package edu.umd.fcmd.sensorlisteners.listener.applications;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;


import edu.umd.fcmd.sensorlisteners.issuehandling.PermissionDeniedHandler;
import edu.umd.fcmd.sensorlisteners.model.ForegroundBackgroundEventsProbe;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by MMueller on 11/17/2016.
 */

public class TimedApplicationTask extends AsyncTask<Void, ForegroundBackgroundEventsProbe, Void> {
    private final String TAG = getClass().getSimpleName();
    private int APPLICATION_SLEEP_TIME;
    private final int normalizationFactor = 5000;
    private long lastTime;
    private ComponentName lastComponent = null;

    private final ApplicationsListener applicationsListener;
    private final PermissionDeniedHandler permissionDeniedHandler;
    private final ActivityManager activityManager;
    private final Context context;
    private int apiLevel;

    TimedApplicationTask(ApplicationsListener applicationsListener, Context context, PermissionDeniedHandler permissionDeniedHandler) {
        this.applicationsListener = applicationsListener;
        this.permissionDeniedHandler = permissionDeniedHandler;
        this.context = context;
        activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                APPLICATION_SLEEP_TIME = 5000;

            } else {
                APPLICATION_SLEEP_TIME = 1000;
            }
            apiLevel = Build.VERSION.SDK_INT;
            Log.d(TAG, "API level " + Build.VERSION.SDK_INT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param params The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected Void doInBackground(Void... params) {
        Log.d(TAG, "started doInBackground");

        while (!isCancelled()) {
            if(checkPermissions()){
                checkForNewEvents(context, lastComponent, lastTime);

                try {
                    //Log.d(TAG, "Sleep now");
                    //noinspection BusyWait
                    Thread.sleep((long) APPLICATION_SLEEP_TIME);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                    Log.d(TAG, "Sleep has been tried to interrupt, but thread interrupted the interrupting Thread.");
                }
            }else{
                permissionDeniedHandler.onPermissionDenied(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(ForegroundBackgroundEventsProbe... values) {
        ForegroundBackgroundEventsProbe eventsProbe = values[0];
        applicationsListener.onUpdate(eventsProbe);
    }

    /**
     * Checks if the permission for accessing the events is being gratnted
     * or denied.
     * @return true if granted, else false.
     */
    protected boolean checkPermissions(){
        if(apiLevel >=21){
            try {
                PackageManager packageManager = context.getPackageManager();
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
                AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
                return (mode == AppOpsManager.MODE_ALLOWED);

            } catch (PackageManager.NameNotFoundException e) {
                return false;
            }
        }else{
            return true;
        }
    }

    /**
     * Checking for new events.
     * @param context the service context.
     * @param lastComponent the last cached component.
     * @param lastTime the last cached time.
     */
    public void checkForNewEvents(Context context, ComponentName lastComponent, long lastTime) {
        if (apiLevel >= Build.VERSION_CODES.LOLLIPOP) {
            this.lastTime = getUsageEventsStatsManager(context, lastTime);
            //getUsageEvents();
        } else {
            this.lastComponent = getUsageEventsRunningTasks(context, lastComponent);
        }

    }

    private long getUsageEventsStatsManager(Context context, long lastTime) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // This does not work
            //long currentTime = calendar.getTimeInMillis();
            long currentTime = System.currentTimeMillis();
            if (lastTime != 0) {
                //Retrieve form last time to current time
                UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
                UsageEvents usageEvents = usageStatsManager.queryEvents(lastTime, currentTime);

                while(usageEvents.hasNextEvent()){
                    UsageEvents.Event event = new UsageEvents.Event();
                    usageEvents.getNextEvent(event);
                    ForegroundBackgroundEventsProbe probe = new ForegroundBackgroundEventsProbe();
                    probe.setAccuracy(1);
                    probe.setClassName(event.getClassName());
                    probe.setEventType(event.getEventType());
                    probe.setPackageName(event.getPackageName());
                    probe.setDate(event.getTimeStamp());
                    publishProgress(probe);
                }

//                Map<String, UsageStats> stats = usageStatsManager.queryAndAggregateUsageStats(lastTime, currentTime);
//
//                for(Map.Entry<String, UsageStats> entry: stats.entrySet()){
//                    Log.d(TAG, "UsageStatsEntry: Package name: "+entry.getValue().getPackageName()+
//                            "\n First timestamp: "+entry.getValue().getFirstTimeStamp()+
//                            "\n Last timestamp: "+entry.getValue().getLastTimeStamp()+
//                            "\n Last time used: "+entry.getValue().getLastTimeUsed()+
//                            "\n Total Time in Foreground "+entry.getValue().getTotalTimeInForeground());
//                }

            }
            return currentTime;
        } else throw new IllegalArgumentException("Wrong SDK level to invoke this method.");
    }

    /**
     * Process running tasks for old Api levels
     *
     * @param context       the context.
     * @param lastComponent the cached last component.
     * @return the new latest retrieved componentName.
     */
    private ComponentName getUsageEventsRunningTasks(Context context, @Nullable ComponentName lastComponent) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager mActivityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            ComponentName componentName = mActivityManager.getRunningTasks(1).get(0).topActivity;

            //Check if something new happened
            if (lastComponent != null) {
                if (!componentName.getClassName().equals(lastComponent.getClassName())) {
                    long currentTime = System.currentTimeMillis();
                    double acc = computeAccuracy(Build.VERSION.SDK_INT, lastTime, currentTime);

                    //The last known applications got in background
                    ForegroundBackgroundEventsProbe oldProbe = new ForegroundBackgroundEventsProbe();
                    oldProbe.setAccuracy(acc);
                    oldProbe.setPackageName(componentName.getPackageName());
                    oldProbe.setDate(currentTime);
                    oldProbe.setEventType(UsageEvents.Event.MOVE_TO_BACKGROUND);
                    publishProgress(oldProbe);

                    //The new application got in foreground
                    ForegroundBackgroundEventsProbe newProbe = new ForegroundBackgroundEventsProbe();
                    newProbe.setAccuracy(acc);
                    newProbe.setPackageName(componentName.getPackageName());
                    newProbe.setDate(currentTime);
                    newProbe.setEventType(UsageEvents.Event.MOVE_TO_FOREGROUND);
                    publishProgress(newProbe);
                }
            }

            return componentName;
        } else {
            Log.d(TAG, "getUsageEventsRunningTasks is deprecated for Api level 21+");
            return null;
        }
    }

    /**
     * Method to determine accuracy.
     * acc is   1, if api level 21+
     * e^(-period/normalizationfactor), else
     *
     * @param apilevel       the current api level.
     * @param intervallStart start of the intervall.
     * @param intervallStop  end of intervall.
     * @return accuracy [0,1]
     */
    private double computeAccuracy(int apilevel, long intervallStart, long intervallStop) {
        return intervallStop - intervallStart;

        //TODO find a good function to represent this.
//        if (apilevel >= Build.VERSION_CODES.LOLLIPOP) {
//            return 1.0;
//        } else {
//            long periode = intervallStop - intervallStart;
//            return Math.exp(-(periode / normalizationFactor));
//        }
    }
}
