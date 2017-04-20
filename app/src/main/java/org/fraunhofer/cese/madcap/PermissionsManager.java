package org.fraunhofer.cese.madcap;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import android.widget.CheckBox;
import android.widget.CompoundButton;

/**
 * Created by PGuruprasad on 18/04/2017.
 */

public class PermissionsManager extends Activity implements CheckBox.OnCheckedChangeListener{
    private static final int REQUEST_CODE = 998;
    private boolean ACCESS_PERMIT = false;

    CheckBox contactsCB, locationCB, storageCB;
    CheckBox smsCB, telephoneCB, networkCB;

    AlertDialog.Builder dialogBuilder;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);

        contactsCB = (CheckBox) findViewById(R.id.contactsCheckBox);
        contactsCB.setOnCheckedChangeListener(this);
        contactsCB.setChecked(
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) ? true :
                        getPermissionWithRationale(Manifest.permission.GET_ACCOUNTS, "MADCAP requires permission to read your contacts to get user name."));

        locationCB = (CheckBox) findViewById(R.id.locationCheckBox);
        locationCB.setOnCheckedChangeListener(this);
        locationCB.setChecked(
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) ? true :
                        getPermissionWithRationale(Manifest.permission.ACCESS_FINE_LOCATION,"MADCAP requires permission to access your location."));

        storageCB = (CheckBox) findViewById(R.id.storageCheckBox);
        storageCB.setOnCheckedChangeListener(this);
        storageCB.setChecked(
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) ? true :
                        getPermissionWithRationale(Manifest.permission.READ_EXTERNAL_STORAGE, "MADCAP requires your permission to read external storage."));

        smsCB = (CheckBox) findViewById(R.id.smsCheckBox);
        smsCB.setOnCheckedChangeListener(this);
        smsCB.setChecked(
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) ? true :
                        getPermissionWithRationale(Manifest.permission.READ_SMS, "MADCAP requires your permission to read SMS notification."));

        telephoneCB = (CheckBox) findViewById(R.id.telephoneCheckBox);
        telephoneCB.setOnCheckedChangeListener(this);
        telephoneCB.setChecked(
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) ? true :
                        getPermissionWithRationale(Manifest.permission.READ_PHONE_STATE, "MADCAP requires your permission to read phone state."));

    }

    private boolean getPermissionWithRationale(String permit, String rationale) {
        dialogBuilder = new AlertDialog.Builder(this);
        ACCESS_PERMIT = false;
        switch (permit) {
            case Manifest.permission.GET_ACCOUNTS:
                dialogBuilder.setTitle("MADCAP permissions")
                        .setMessage(rationale)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(PermissionsManager.this, new String[]{Manifest.permission.GET_ACCOUNTS},
                                                REQUEST_CODE);
                                        dialog.cancel();
                                    }
                                })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                dialog = dialogBuilder.create();
                dialog.show();
                break;

            case Manifest.permission.ACCESS_FINE_LOCATION:
                dialogBuilder.setTitle("MADCAP permissions")
                        .setMessage(rationale)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(PermissionsManager.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                                REQUEST_CODE-1);
                                        dialog.cancel();
                                    }
                                })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                dialog = dialogBuilder.create();
                dialog.show();
                break;

            case Manifest.permission.READ_EXTERNAL_STORAGE:
                dialogBuilder.setTitle("MADCAP permissions")
                        .setMessage(rationale)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(PermissionsManager.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                                REQUEST_CODE-2);
                                        dialog.cancel();
                                    }
                                })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                dialog = dialogBuilder.create();
                dialog.show();
                break;

            case Manifest.permission.READ_SMS:
                dialogBuilder.setTitle("MADCAP permissions")
                        .setMessage(rationale)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(PermissionsManager.this, new String[]{Manifest.permission.READ_SMS},
                                                REQUEST_CODE-3);
                                        dialog.cancel();
                                    }
                                })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                dialog = dialogBuilder.create();
                dialog.show();
                break;

            case Manifest.permission.READ_PHONE_STATE:
                dialogBuilder.setTitle("MADCAP permissions")
                        .setMessage(rationale)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(PermissionsManager.this, new String[]{Manifest.permission.READ_PHONE_STATE},
                                                REQUEST_CODE-4);
                                        dialog.dismiss();
                                    }
                                })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                dialog = dialogBuilder.create();
                dialog.show();
                break;
        }
        return ACCESS_PERMIT;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case REQUEST_CODE:
                contactsCB.setChecked((grantResults[0] == PackageManager.PERMISSION_GRANTED) ? true : false); break;
            case REQUEST_CODE-3:
                smsCB.setChecked((grantResults[0] == PackageManager.PERMISSION_GRANTED) ? true : false); break;
            case REQUEST_CODE-1:
                locationCB.setChecked((grantResults[0] == PackageManager.PERMISSION_GRANTED) ? true : false); break;
            case REQUEST_CODE-2:
                storageCB.setChecked((grantResults[0] == PackageManager.PERMISSION_GRANTED) ? true : false); break;
            case REQUEST_CODE-4:
                telephoneCB.setChecked((grantResults[0] == PackageManager.PERMISSION_GRANTED) ? true : false); break;
//            case REQUEST_CODE-5:
//                networkCB.setChecked((grantResults[0] == PackageManager.PERMISSION_GRANTED) ? true : false); break;

        }
//        if(requestCode == REQUEST_CODE) {
//            ACCESS_PERMIT = (grantResults[0] == PackageManager.PERMISSION_GRANTED) ? true : false;
//            dialog.cancel();
//        }
    }

    @Override
    public void onCheckedChanged(CompoundButton checkbox, boolean isChecked) {
        switch(checkbox.getId()){
            case R.id.contactsCheckBox:
                if(isChecked) return;
                getPermissionWithRationale(Manifest.permission.GET_ACCOUNTS, "MADCAP requires permission to read your contacts to get username."); return;
            case R.id.locationCheckBox:
                if(isChecked) return;
                getPermissionWithRationale(Manifest.permission.ACCESS_FINE_LOCATION,"MADCAP requires permission to access your location."); return;
            case R.id.smsCheckBox:
                if(isChecked) return;
                getPermissionWithRationale(Manifest.permission.READ_SMS, "MADCAP requires your permission to read SMS notification."); return;
            case R.id.storageCheckBox:
                if(isChecked) return;
                getPermissionWithRationale(Manifest.permission.READ_EXTERNAL_STORAGE, "MADCAP requires your permission to read external storage."); return;
            case R.id.telephoneCheckBox:
                if(isChecked) return;
                getPermissionWithRationale(Manifest.permission.READ_PHONE_STATE, "MADCAP requires your permission to read phone state."); return;

        }
    }
}