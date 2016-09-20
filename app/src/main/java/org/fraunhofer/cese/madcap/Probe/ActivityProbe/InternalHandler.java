package org.fraunhofer.cese.madcap.Probe.ActivityProbe;

import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.widget.TextView;

import com.pathsense.android.sdk.location.PathsenseDetectedActivities;
import com.pathsense.android.sdk.location.PathsenseDetectedActivity;
import com.pathsense.android.sdk.location.PathsenseDeviceHolding;
import com.pathsense.android.sdk.location.PathsenseLocationProviderApi;

import org.fraunhofer.cese.madcap.MainActivity;

import java.util.List;

/**
 * Created by MMueller on 9/20/2016.
 * @deprecated
 */
public class InternalHandler extends Handler {
    ActivityProbe activityProbe;
    //
    InternalHandler(ActivityProbe activityProbr)
    {
        this.activityProbe = activityProbe;
    }
    @Override
    public void handleMessage(Message msg)
    {
        final ActivityProbe activityProbe = this.activityProbe;
//        final TextView textDetectedActivity0 = activity != null ? activity.mTextDetectedActivity0 : null;
//        final TextView textDetectedActivity1 = activity != null ? activity.mTextDetectedActivity1 : null;
//        final TextView textDeviceHolding = activity != null ? activity.mTextDeviceHolding : null;
        final PathsenseLocationProviderApi api = activityProbe != null ? activityProbe.mApi : null;
        //
        if (activityProbe != null && api != null)
        {
            switch (msg.what)
            {
                case ActivityProbe.MESSAGE_ON_ACTIVITY_CHANGE:
                {
                    PathsenseDetectedActivities detectedActivities = (PathsenseDetectedActivities) msg.obj;
                    PathsenseDetectedActivity mostProbableActivity = detectedActivities.getMostProbableActivity();
                    if (mostProbableActivity != null)
                    {
                        StringBuilder detectedActivityString = new StringBuilder(mostProbableActivity.getDetectedActivity().name());
//							if (mostProbableActivity.isStationary())
//							{
//								detectedActivityString.append(" STATIONARY");
//							}
                        String s = detectedActivityString.toString();
                        //textDetectedActivity1.setText(detectedActivityString.toString());
                    } else
                    {
                        String s = "";
                        //textDetectedActivity1.setText("");
                    }
                    break;
                }
                case ActivityProbe.MESSAGE_ON_ACTIVITY_UPDATE:
                {
                    PathsenseDetectedActivities detectedActivities = (PathsenseDetectedActivities) msg.obj;
                    PathsenseDetectedActivity mostProbableActivity = detectedActivities.getMostProbableActivity();
                    if (mostProbableActivity != null)
                    {
                        List<PathsenseDetectedActivity> detectedActivityList = detectedActivities.getDetectedActivities();
                        int numDetectedActivityList = detectedActivityList != null ? detectedActivityList.size() : 0;
                        if (numDetectedActivityList > 0)
                        {
                            StringBuilder detectedActivityString = new StringBuilder();
                            for (int i = 0; i < numDetectedActivityList; i++)
                            {
                                PathsenseDetectedActivity detectedActivity = detectedActivityList.get(i);
                                if (i > 0)
                                {
                                    detectedActivityString.append("<br />");
                                }
                                detectedActivityString.append(detectedActivity.getDetectedActivity().name() + " " + detectedActivity.getConfidence());
                                String s = detectedActivityString.toString();
                            }
                            //textDetectedActivity0.setText(Html.fromHtml(detectedActivityString.toString()));
                        }
                    } else
                    {
                        String s = "";
                        //textDetectedActivity0.setText("");
                    }
                    break;
                }
                case ActivityProbe.MESSAGE_ON_DEVICE_HOLDING:
                {
                    PathsenseDeviceHolding deviceHolding = (PathsenseDeviceHolding) msg.obj;
                    if (deviceHolding != null)
                    {
                        String s = "";
                        //textDeviceHolding.setText(deviceHolding.isHolding() ? "Holding" : "Not Holding");
                    } else
                    {
                        String s = "";
                        //textDeviceHolding.setText("");
                    }
                    break;
                }
            }
        }
    }
}
