package edu.umd.fcmd.sensorlisteners.listener.applications;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jaredrummler.android.device.DeviceName;

import java.util.List;

import edu.umd.fcmd.sensorlisteners.issuehandling.PermissionsManager;
import edu.umd.fcmd.sensorlisteners.model.applications.AppPermissionsProbe;
import edu.umd.fcmd.sensorlisteners.model.applications.ForegroundBackgroundEventsProbe;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by MMueller on 11/17/2016.
 *
 * Retrieving the last Applications and their time when they have been moving from
 * foreground to the background or vice versa.
 *
 * The implementation has to be different for Androiod API level 19-21 and API level
 * 21+ since the Android OS developersr closed the possbilities to detect the running
 * applications via the process stack in API level 21. For API level 21+ we now use
 * the UsageStatsManager and the events gathered from it. This method is not available
 * for api level 20 and lower.
 *
 * Also for API level 21+ permission on usageStats are required. If they have not been
 * granted the permissionsManager triggers a callback method.
 */

class TimedApplicationTask extends AsyncTask<Void, ForegroundBackgroundEventsProbe, Void> {
    private final String TAG = getClass().getSimpleName();
    private int applicationProbeSleepTime;
    private long lastTime;
    private ComponentName lastComponent;

    private final ApplicationsListener applicationsListener;
    private final PermissionsManager permissionsManager;
    private final ActivityManager activityManager;
    private final Context context;
    private int apiLevel;

    TimedApplicationTask(ApplicationsListener applicationsListener, Context context, PermissionsManager permissionsManager) {
        this.applicationsListener = applicationsListener;
        this.permissionsManager = permissionsManager;
        this.context = context;
        activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                applicationProbeSleepTime = 5000;
            } else {
                applicationProbeSleepTime = 1000;
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
    @SuppressLint("InlinedApi")
    @Override
    protected Void doInBackground(Void... params) {
        Log.d(TAG, "started doInBackground");

        // send app permissions probe once per MADCAP data collection session start
        //sendAppPermissionsProbes();

        while (!isCancelled()) {
            if(checkPermissions()){
                checkForNewEvents(context, lastComponent, lastTime);

                try {
                    //Log.d(TAG, "Sleep now");
                    //noinspection BusyWait
                    Thread.sleep((long) applicationProbeSleepTime);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                    Log.d(TAG, "Sleep has been tried to interrupt, but thread interrupted the interrupting Thread.");
                }
            }//else{ permissionsManager.onPermissionDenied(Settings.ACTION_USAGE_ACCESS_SETTINGS); }
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
        if(apiLevel >=Build.VERSION_CODES.LOLLIPOP){
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
            long currentTime = System.currentTimeMillis();
            if (lastTime != 0L) {
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

    public void sendInitialProbes(){
        if(checkPermissions()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                long currentTime = System.currentTimeMillis();
                    //Retrieve form last time to current time
                    UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
                    UsageEvents usageEvents = usageStatsManager.queryEvents(currentTime-5000, currentTime);

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
                this.lastTime = currentTime;
                //getUsageEvents();
            } else {// version earlier than lollipop
                ActivityManager mActivityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
                ComponentName componentName = mActivityManager.getRunningTasks(1).get(0).topActivity;

                long currentTime = System.currentTimeMillis();
                double acc = computeAccuracy(Build.VERSION.SDK_INT, lastTime, currentTime);

                ForegroundBackgroundEventsProbe newProbe = new ForegroundBackgroundEventsProbe();
                newProbe.setAccuracy(acc);
                newProbe.setPackageName(componentName.getPackageName());
                newProbe.setDate(currentTime);
                newProbe.setEventType(UsageEvents.Event.MOVE_TO_FOREGROUND);
                publishProgress(newProbe);

                this.lastComponent = componentName;
            }
//        }else permissionsManager.onPermissionDenied(Settings.ACTION_USAGE_ACCESS_SETTINGS);
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

    private void sendAppPermissionsProbes() {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS | PackageManager.GET_META_DATA);
        DeviceName.DeviceInfo info = DeviceName.getDeviceInfo(context);
        String deviceManufacturerName = info.manufacturer.toLowerCase();

        for (PackageInfo packageInfo : packages) {
            // do not create probe if:
            // 1. The app does not ask for any permissions (requestedPermissions list is null)
            // 2. The app is a system app, and therefore can be trusted (contains "android" in the package name)
            // 3. The app is a manufacturer provided app and therefore can be trusted (contains manufacturer name in the package name)
            if (packageInfo.requestedPermissions != null && !packageInfo.packageName.contains("android") && !packageInfo.packageName.contains(deviceManufacturerName)) {
                AppPermissionsProbe appPermProbe = new AppPermissionsProbe();
                appPermProbe.setPackageName(packageInfo.packageName);
                String rejectedPermissions = "";
                String grantedPermissions = "";
                String permission;

                for (int i=0; i < packageInfo.requestedPermissions.length; i++) {
                    permission = packageInfo.requestedPermissions[i];
                    if ((packageInfo.requestedPermissionsFlags[i] & PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0) {
                        // permission was granted
                        grantedPermissions = grantedPermissions.concat(permission);
                        grantedPermissions = grantedPermissions.concat("\n");
                    }
                    else {
                        // permission was rejected
                        rejectedPermissions = rejectedPermissions.concat(permission);
                        rejectedPermissions = rejectedPermissions.concat("\n");
                    }
                }

                appPermProbe.setPermissionsRejected(rejectedPermissions);
                appPermProbe.setPermissionsGranted(grantedPermissions);
                applicationsListener.onUpdate(appPermProbe);
            }
        }
    }
}
