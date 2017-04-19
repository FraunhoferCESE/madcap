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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);

        contactsCB = (CheckBox) findViewById(R.id.contactsCheckBox);
        contactsCB.setOnCheckedChangeListener(this);

        locationCB = (CheckBox) findViewById(R.id.locationCheckBox);
        locationCB.setOnCheckedChangeListener(this);

        storageCB = (CheckBox) findViewById(R.id.storageCheckBox);
        storageCB.setOnCheckedChangeListener(this);

        smsCB = (CheckBox) findViewById(R.id.smsCheckBox);
        smsCB.setOnCheckedChangeListener(this);

        telephoneCB = (CheckBox) findViewById(R.id.telephoneCheckBox);
        telephoneCB.setOnCheckedChangeListener(this);

        networkCB = (CheckBox) findViewById(R.id.networkCheckBox);
        networkCB.setOnCheckedChangeListener(this);

        checkCheckBoxes();
    }

    private void checkCheckBoxes(){
        contactsCB.setChecked(
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) ? true :
                        getPermissionWithRationale(Manifest.permission.READ_CONTACTS, "MADCAP requires permission to read your contacts to get username."));

        locationCB.setChecked(
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) ? true :
                        getPermissionWithRationale(Manifest.permission.ACCESS_FINE_LOCATION,"MADCAP requires permission to access your location."));

        storageCB.setChecked(
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) ? true :
                        getPermissionWithRationale(Manifest.permission.READ_EXTERNAL_STORAGE, "MADCAP requires your permission to read external storage."));

        smsCB.setChecked(
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) ? true :
                        getPermissionWithRationale(Manifest.permission.READ_SMS, "MADCAP requires your permission to read SMS notification."));

        telephoneCB.setChecked(
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) ? true :
                        getPermissionWithRationale(Manifest.permission.READ_PHONE_STATE, "MADCAP requires your permission to read phone state."));

        networkCB.setChecked(
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) ? true :
                        getPermissionWithRationale(Manifest.permission.ACCESS_COARSE_LOCATION, "MADCAP requires your permission to read network state."));
    }

    private boolean getPermissionWithRationale(String permit, String rationale) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        switch (permit) {
            case Manifest.permission.READ_CONTACTS:
//                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                    dialog.setTitle("MADCAP permissions")
                            .setMessage(rationale)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ActivityCompat.requestPermissions(PermissionsManager.this, new String[]{Manifest.permission.READ_CONTACTS},
                                                    REQUEST_CODE);
                                            dialog.dismiss();
                                        }
                                    })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create();
                dialog.show();
                break;

            case Manifest.permission.ACCESS_FINE_LOCATION:
//                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                dialog.setTitle("MADCAP permissions")
                        .setMessage(rationale)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(PermissionsManager.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                                REQUEST_CODE);
                                        dialog.dismiss();
                                    }
                                })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .create();
                dialog.show();
                break;

            case Manifest.permission.READ_EXTERNAL_STORAGE:
//                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                dialog.setTitle("MADCAP permissions")
                        .setMessage(rationale)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(PermissionsManager.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                                REQUEST_CODE);
                                        dialog.dismiss();
                                    }
                                })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();
                break;

            case Manifest.permission.READ_SMS:
//                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                dialog.setTitle("MADCAP permissions")
                        .setMessage(rationale)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(PermissionsManager.this, new String[]{Manifest.permission.READ_SMS},
                                                REQUEST_CODE);
                                        dialog.dismiss();
                                    }
                                })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();
                break;

            case Manifest.permission.READ_PHONE_STATE:
//                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                dialog.setTitle("MADCAP permissions")
                        .setMessage(rationale)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(PermissionsManager.this, new String[]{Manifest.permission.READ_PHONE_STATE},
                                                REQUEST_CODE);
                                        dialog.dismiss();
                                    }
                                })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();
                break;

            case Manifest.permission.ACCESS_COARSE_LOCATION:
//                if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                dialog.setTitle("MADCAP permissions")
                        .setMessage(rationale)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(PermissionsManager.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                                REQUEST_CODE);
                                        dialog.dismiss();
                                    }
                                })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();
                break;
        }
        return ACCESS_PERMIT;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == REQUEST_CODE)
            ACCESS_PERMIT = (grantResults[0]==PackageManager.PERMISSION_GRANTED)? true : false;
        checkCheckBoxes();
    }

    @Override
    public void onCheckedChanged(CompoundButton checkbox, boolean isChecked) {
        switch(checkbox.getId()){
            case R.id.contactsCheckBox:
                getPermissionWithRationale(Manifest.permission.READ_CONTACTS, "MADCAP requires permission to read your contacts to get username."); break;
            case R.id.locationCheckBox:
                getPermissionWithRationale(Manifest.permission.ACCESS_FINE_LOCATION,"MADCAP requires permission to access your location."); break;
            case R.id.smsCheckBox:
                getPermissionWithRationale(Manifest.permission.READ_SMS, "MADCAP requires your permission to read SMS notification."); break;
            case R.id.storageCheckBox:
                getPermissionWithRationale(Manifest.permission.READ_EXTERNAL_STORAGE, "MADCAP requires your permission to read external storage."); break;
            case R.id.telephoneCheckBox:
                getPermissionWithRationale(Manifest.permission.READ_PHONE_STATE, "MADCAP requires your permission to read phone state."); break;
            case R.id.networkCheckBox:
                getPermissionWithRationale(Manifest.permission.ACCESS_COARSE_LOCATION, "MADCAP requires your permission to read network state."); break;

        }
    }
}