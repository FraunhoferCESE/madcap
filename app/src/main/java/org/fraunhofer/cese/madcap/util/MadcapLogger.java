package org.fraunhofer.cese.madcap.util;

import android.util.Log;

/**
 * Created by MMueller on 10/20/2016.
 */

public class MadcapLogger {
    private static int logLevel = 5;
    private static boolean ERROR = logLevel >= 1;
    private static boolean WARNING = logLevel >= 2;
    private static boolean INFO = logLevel >= 3;
    private static boolean DEBUG = logLevel >=4;
    private static boolean VERBOSE = logLevel >=5;

    public static void setLogLevel(int logLevel){
        if(logLevel >= 0 && logLevel <=5){
            MadcapLogger.logLevel = logLevel;
        }else{
            throw new IllegalArgumentException("Loglevel has to be between 0 and 5");
        }
    }

    public static int getLogLevel(){
        return logLevel;
    }

    /**
     * Logs data as an error if the logLevel requirements are fullfilled.
     * @param tag The log Tag.
     * @param message The Log-Message itself.
     */
    public final void e(String tag, String message){
        if(ERROR){
            Log.e(tag,message);
        }
    }

    /**
     * Logs data as an error if the logLevel requirements are fullfilled.
     * @param tag The log Tag.
     * @param message The Log-Message itself.
     * @param t The stack trace.
     */
    public final void e(String tag, String message, Throwable t){
        if(ERROR){
            Log.e(tag,message, t);
        }
    }

    /**
     * Logs data as an warning if the logLevel requirements are fullfilled.
     * @param tag The log Tag.
     * @param message The Log-Message itself.
     */
    public final void w(String tag, String message){
        if(WARNING){
            Log.w(tag,message);
        }
    }

    /**
     * Logs data as a warning if the logLevel requirements are fullfilled.
     * @param tag The log Tag.
     * @param message The Log-Message itself.
     * @param t The stack trace.
     */
    public final void w(String tag, String message, Throwable t){
        if(ERROR){
            Log.e(tag,message, t);
        }
    }

    /**
     * Logs data as an information if the logLevel requirements are fullfilled.
     * @param tag The log Tag.
     * @param message The Log-Message itself.
     */
    public final void i(String tag, String message){
        if(INFO){
            Log.i(tag,message);
        }
    }

    /**
     * Logs data as an information if the logLevel requirements are fullfilled.
     * @param tag The log Tag.
     * @param message The Log-Message itself.
     * @param t The stack trace.
     */
    public final void i(String tag, String message, Throwable t){
        if(ERROR){
            Log.e(tag,message, t);
        }
    }

    /**
     * Logs data as debug if the logLevel requirements are fullfilled.
     * @param tag The log Tag.
     * @param message The Log-Message itself.
     */
    public final void d(String tag, String message){
        if(DEBUG){
            Log.d(tag,message);
        }
    }

    /**
     * Logs data as debug message if the logLevel requirements are fullfilled.
     * @param tag The log Tag.
     * @param message The Log-Message itself.
     * @param t The stack trace.
     */
    public final void d(String tag, String message, Throwable t){
        if(ERROR){
            Log.e(tag,message, t);
        }
    }

    /**
     * Logs data as verbose if the logLevel requirements are fullfilled.
     * @param tag The log Tag.
     * @param message The Log-Message itself.
     */
    public final void v(String tag, String message){
        if(VERBOSE){
            Log.v(tag,message);
        }
    }

    /**
     * Logs data as verbose message if the logLevel requirements are fullfilled.
     * @param tag The log Tag.
     * @param message The Log-Message itself.
     * @param t The stack trace.
     */
    public final void v(String tag, String message, Throwable t){
        if(ERROR){
            Log.e(tag,message, t);
        }
    }
}
