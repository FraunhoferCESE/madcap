package org.fraunhofer.cese.madcap.authentication;

import com.google.android.gms.common.ConnectionResult;

/**
 * Created by llayman on 11/21/2016.
 */

public class ApiUtils {

    public static String getTextFromConnectionResult(int result) {
        switch(result) {
            case ConnectionResult.SUCCESS:
                return "Play Services successfully connected.";
            case ConnectionResult.SERVICE_MISSING:
                return "Play services not available, please install them.";
            case ConnectionResult.SERVICE_UPDATING:
                return "Play services are currently updating, please wait.";
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                return "Play services not up to date. Please update.";
            case ConnectionResult.SERVICE_DISABLED:
                return "Play services are disabled. Please enable.";
            case ConnectionResult.SERVICE_INVALID:
                return "Play services are invalid. Please reinstall them";
            default:
                return "Unknown Play Services return code.";

        }
    }
}
