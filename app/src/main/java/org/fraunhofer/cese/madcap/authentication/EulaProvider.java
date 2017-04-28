package org.fraunhofer.cese.madcap.authentication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.preference.PreferenceManager;

import org.fraunhofer.cese.madcap.R;

/**
 * This file provides simple End User License Agreement
 * It shows a simple dialog with the license text, and two buttons.
 * If user clicks on 'cancel' button, app closes and user will not be granted access to app.
 * If user clicks on 'accept' button, app access is allowed and this choice is saved in preferences
 * so next time this will not show, until next upgrade.
 * <p>
 * From https://jayeshcp.wordpress.com/2013/07/23/showing-eula-in-android-app/
 */
class EulaProvider {
    private final Activity mContext;

    EulaProvider(Activity context) {
        mContext = context;
    }

    /**
     * Display the EULA popup window
     *
     * @param listener a EULAListener callback object whose methods are triggered based on whether the user accepts or declines the EULA
     */
    void show(final EULAListener listener) {
        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(mContext);

        final String eulaKey = mContext.getString(R.string.EULA_key);
        //noinspection BooleanVariableAlwaysNegated
        boolean bAlreadyAccepted = prefs.getBoolean(eulaKey, false);
        if (!bAlreadyAccepted) {

            // EULA title
            String title = "END USER LICENSE AGREEMENT FOR " + mContext.getString(R.string.app_name);

            // EULA text from strings.xml
            String message = mContext.getString(R.string.eula);

            // Disable orientation changes, to prevent parent activity
            // reinitialization
            mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                    .setTitle(title)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(R.string.accept,
                            new DialogInterface.OnClickListener() {

                                @SuppressLint("ApplySharedPref")
                                @Override
                                public void onClick(
                                        DialogInterface dialog, int which) {
                                    // Mark this version as read.
                                    SharedPreferences.Editor editor = prefs
                                            .edit();
                                    editor.putBoolean(eulaKey, true);
                                    editor.commit();

                                    // Close dialog
                                    dialog.dismiss();

                                    // Enable orientation changes based on
                                    // device's sensor
                                    mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

                                    // Callback to indicate the user accepted the EULA
                                    if (listener != null) {
                                        listener.onAccept();
                                    }
                                }
                            })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

                                    // Callback to indicate the user declined the EULA
                                    if (listener != null) {
                                        listener.onCancel();
                                    }
                                }

                            });
            builder.create().show();
        }
    }
}

