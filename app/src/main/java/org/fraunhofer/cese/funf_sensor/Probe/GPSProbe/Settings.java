package org.fraunhofer.cese.funf_sensor.Probe.GPSProbe;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by MLang on 05.02.2015.
 */
public class Settings {
    private static final String UNIT_STRING = "MeasureUnit";
    private static final String PREF_NAME = "SpeedDemo_20100707";

    public static int getMeasureUnit(Context context){
        return getInt(context,Settings.UNIT_STRING);
    }

    public static void setMeasureUnit(Context context,int limit){
        putInt(context,Settings.UNIT_STRING,limit);
    }

    private static int getInt(Context context, String tag) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, 0);

        return pref.getInt(tag, 0);
    }

    public static void putInt(Context context, String tag, int value) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, 0);
        SharedPreferences.Editor editor = pref.edit();

        editor.putInt(tag, value);
        editor.commit();
    }
}
