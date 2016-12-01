package edu.umd.fcmd.sensorlisteners.listener.applications;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import edu.umd.fcmd.sensorlisteners.issuehandling.PermissionDeniedHandler;
import edu.umd.fcmd.sensorlisteners.model.ForegroundBackgroundEventsProbe;

import static android.content.Context.ACTIVITY_SERVICE;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by MMueller on 12/1/2016.
 */
public class TimedApplicationTaskTest {
    ApplicationsListener mockApplicationsListener;
    Context mockContext;
    PermissionDeniedHandler mockPermissionDeniedHandler;

    @Before
    public void setUp() throws Exception {
        mockApplicationsListener = mock(ApplicationsListener.class);
        mockContext = mock(Context.class);
        mockPermissionDeniedHandler = mock(PermissionDeniedHandler.class);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void constructorTest(){
        try {
            setFinalStatic(Build.VERSION.class.getField("SDK_INT"), 21);
        } catch (Exception e) {
            e.printStackTrace();
        }

        TimedApplicationTask cut21 = new TimedApplicationTask(mockApplicationsListener, mockContext, mockPermissionDeniedHandler);

        try {
            setFinalStatic(Build.VERSION.class.getField("SDK_INT"), 19);
        } catch (Exception e) {
            e.printStackTrace();
        }

        TimedApplicationTask cut19 = new TimedApplicationTask(mockApplicationsListener, mockContext, mockPermissionDeniedHandler);

    }

    @Test
    public void doInBackground() throws Exception {
        //Not testable due to infinite loop
    }

    @Test
    public void onProgressUpdate() throws Exception {
        TimedApplicationTask cut = new TimedApplicationTask(mockApplicationsListener, mockContext, mockPermissionDeniedHandler);

        ForegroundBackgroundEventsProbe mockForegroundBackgroundEventsProbe = mock(ForegroundBackgroundEventsProbe.class);

        cut.onProgressUpdate(mockForegroundBackgroundEventsProbe);

        verify(mockApplicationsListener).onUpdate(mockForegroundBackgroundEventsProbe);
    }

    @Test
    public void checkPermissions() throws Exception {
        try {
            setFinalStatic(Build.VERSION.class.getField("SDK_INT"), 21);
        } catch (Exception e) {
            e.printStackTrace();
        }

        TimedApplicationTask cut21 = new TimedApplicationTask(mockApplicationsListener, mockContext, mockPermissionDeniedHandler);

        when(mockContext.getPackageName()).thenReturn("A");

        PackageManager mockPackageManager = mock(PackageManager.class);
        when(mockContext.getPackageManager()).thenReturn(mockPackageManager);

        ApplicationInfo mockApplicationInfo = mock(ApplicationInfo.class);
        mockApplicationInfo.uid = 1;
        mockApplicationInfo.packageName = "name";

        when(mockPackageManager.getApplicationInfo("A", 0)).thenReturn(mockApplicationInfo);

        AppOpsManager mockAppOpsManager = spy(AppOpsManager.class);
        when(mockContext.getSystemService(Context.APP_OPS_SERVICE)).thenReturn((AppOpsManager) mockAppOpsManager);

        when(mockAppOpsManager.checkOpNoThrow(anyString(), anyInt(), anyString())).thenReturn(0);
        Assert.assertTrue(cut21.checkPermissions());

        when(mockAppOpsManager.checkOpNoThrow(anyString(), anyInt(), anyString())).thenReturn(1);
        Assert.assertFalse(cut21.checkPermissions());

        try {
            setFinalStatic(Build.VERSION.class.getField("SDK_INT"), 19);
        } catch (Exception e) {
            e.printStackTrace();
        }

        TimedApplicationTask cut19 = new TimedApplicationTask(mockApplicationsListener, mockContext, mockPermissionDeniedHandler);

        Assert.assertTrue(cut19.checkPermissions());

    }

    @Test
    public void checkForNewEvents() throws Exception {
        try {
            setFinalStatic(Build.VERSION.class.getField("SDK_INT"), 21);
        } catch (Exception e) {
            e.printStackTrace();
        }

        UsageStatsManager mockUsageStatsManager = mock(UsageStatsManager.class);
        UsageEvents mockUsageEvents = mock(UsageEvents.class);

        when(mockContext.getSystemService(Context.USAGE_STATS_SERVICE)).thenReturn(mockUsageStatsManager);
        when(mockUsageStatsManager.queryEvents(anyLong(), anyLong())).thenReturn(mockUsageEvents);
        when(mockUsageEvents.hasNextEvent()).thenReturn(false);

        TimedApplicationTask cut21 = new TimedApplicationTask(mockApplicationsListener, mockContext, mockPermissionDeniedHandler);

        ComponentName mockComponentName = mock(ComponentName.class);
        cut21.checkForNewEvents(mockContext, mockComponentName, 10000);

        try {
            setFinalStatic(Build.VERSION.class.getField("SDK_INT"), 19);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ActivityManager mockActivityManager = mock(ActivityManager.class);
        when(mockContext.getSystemService(ACTIVITY_SERVICE)).thenReturn(mockActivityManager);

        ComponentName mockComponentName2 = mock(ComponentName.class);
        List<ActivityManager.RunningTaskInfo> mockList = (List<ActivityManager.RunningTaskInfo>) mock(List.class);
        when(mockActivityManager.getRunningTasks(1)).thenReturn(mockList);

        ActivityManager.RunningTaskInfo mockRunningTask = mock(ActivityManager.RunningTaskInfo.class);
        mockRunningTask.topActivity = mockComponentName2;
        when(mockList.get(0)).thenReturn(mockRunningTask);

        when(mockComponentName2.getClassName()).thenReturn("AAAAAA");

        TimedApplicationTask cut19 = new TimedApplicationTask(mockApplicationsListener, mockContext, mockPermissionDeniedHandler);
        cut19.checkForNewEvents(mockContext, mockComponentName, 10000);
    }

    //For accessing the Build Version.
    //This is using refelction.
    static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);
    }

}