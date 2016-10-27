package org.fraunhofer.cese.madcap;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.fraunhofer.cese.madcap.authentification.MadcapAuthManager;

import static android.R.string.no;
import static android.os.Build.VERSION_CODES.M;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuitProjectDialogFragment extends DialogFragment {
    private final String TAG = "QuitProjectDialogFragment";
    private MadcapAuthManager madcapAuthManager = MadcapAuthManager.getInstance();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.quitProjectDialog)
                .setPositiveButton(R.string.quitProjectDialogQuitButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        sendQuitEmailNotification();
                    }
                })
                .setNegativeButton(R.string.quitProjectDialogCancelButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    private void sendQuitEmailNotification(){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{getString(R.string.firstQuitProjectEmailAdress)});
        i.putExtra(Intent.EXTRA_SUBJECT, "Pocket Securit: Participant quit Pocket Security");
        i.putExtra(Intent.EXTRA_TEXT   , "Hi \n User "+madcapAuthManager.getLastSignedInUsersName()+" ("+madcapAuthManager.getUserId()+") left the program");
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            MyApplication.madcapLogger.e(TAG, "There are no email clients installed");
        }
    }
}
