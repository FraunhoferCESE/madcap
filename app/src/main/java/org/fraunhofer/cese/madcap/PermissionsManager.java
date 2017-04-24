package org.fraunhofer.cese.madcap;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
    CheckBox smsCB, telephoneCB, usageStatsCB;

    AlertDialog.Builder dialogBuilder;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);

        contactsCB = (CheckBox) findViewById(R.id.contactsCheckBox);
        contactsCB.setOnCheckedChangeListener(this);
        contactsCB.setChecked(isContactPermitted());
//                (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) ? true :
//                        getPermissionWithRationale(Manifest.permission.GET_ACCOUNTS, "MADCAP needs to access your Google username and email. It does not read your other contacts."));

        locationCB = (CheckBox) findViewById(R.id.locationCheckBox);
        locationCB.setOnCheckedChangeListener(this);
        locationCB.setChecked(isLocationPermitted());
//                (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) ? true :
//                        getPermissionWithRationale(Manifest.permission.ACCESS_FINE_LOCATION,"MADCAP requires permission to access your location."));

        storageCB = (CheckBox) findViewById(R.id.storageCheckBox);
        storageCB.setOnCheckedChangeListener(this);
        storageCB.setChecked(isStoragePermitted());
//                (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) ? true :
//                        getPermissionWithRationale(Manifest.permission.READ_EXTERNAL_STORAGE, "MADCAP requires your permission to read and write data to your phone's memory."));

        smsCB = (CheckBox) findViewById(R.id.smsCheckBox);
        smsCB.setOnCheckedChangeListener(this);
        smsCB.setChecked(isSmsPermitted());
//                (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) ? true :
//                        getPermissionWithRationale(Manifest.permission.READ_SMS, "MADCAP needs this permission to know when you receive text messages. It does not read your texts."));

        telephoneCB = (CheckBox) findViewById(R.id.telephoneCheckBox);
        telephoneCB.setOnCheckedChangeListener(this);
        telephoneCB.setChecked(isTelephonePermitted());
//                (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) ? true :
//                        getPermissionWithRationale(Manifest.permission.READ_PHONE_STATE, "MADCAP needs this permission to know when you are making or receiving phone calls."));

        usageStatsCB = (CheckBox) findViewById(R.id.usageStatsCheckBox);
        usageStatsCB.setOnCheckedChangeListener(this);
        usageStatsCB.setChecked(isUsageStatsPermitted());

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
                        .setCancelable(true);
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
                        .setCancelable(true);
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
                        .setCancelable(true);
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
                        .setCancelable(true);
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
                        .setCancelable(true);
                dialog = dialogBuilder.create();
                dialog.show();
                break;

            case Settings.ACTION_USAGE_ACCESS_SETTINGS:
                dialogBuilder.setTitle("MADCAP permissions")
                        .setMessage(rationale)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        dialog.dismiss();
                                    }
                                })
                        .setCancelable(true);
                dialog = dialogBuilder.create();
                dialog.show();
                break;
        }
        return ACCESS_PERMIT;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if((grantResults[0] == PackageManager.PERMISSION_GRANTED))
            switch(requestCode){
                case REQUEST_CODE:
                    contactsCB.setChecked(true); contactsCB.setClickable(false); break;
                case REQUEST_CODE-3:
                    smsCB.setChecked(true); smsCB.setClickable(false); break;
                case REQUEST_CODE-1:
                    locationCB.setChecked(true); locationCB.setClickable(false); break;
                case REQUEST_CODE-2:
                    storageCB.setChecked(true); storageCB.setClickable(false); break;
                case REQUEST_CODE-4:
                    telephoneCB.setChecked(true); telephoneCB.setClickable(false); break;
                case REQUEST_CODE-5:
                    usageStatsCB.setChecked(true); usageStatsCB.setClickable(false); break;
            }
    }

    @Override
    public void onCheckedChanged(CompoundButton checkbox, boolean isChecked) {
        switch(checkbox.getId()){
            case R.id.contactsCheckBox:
//                if(!isChecked) return;
                getPermissionWithRationale(Manifest.permission.GET_ACCOUNTS, getString(R.string.contacts_rationale)); return;
            case R.id.locationCheckBox:
//                if(!isChecked) return;
                getPermissionWithRationale(Manifest.permission.ACCESS_FINE_LOCATION, getString(R.string.location_rationale)); return;
            case R.id.smsCheckBox:
//                if(!isChecked) return;
                getPermissionWithRationale(Manifest.permission.READ_SMS, getString(R.string.sms_rationale)); return;
            case R.id.storageCheckBox:
//                if(!isChecked) return;
                getPermissionWithRationale(Manifest.permission.READ_EXTERNAL_STORAGE, getString(R.string.storage_rationale)); return;
            case R.id.telephoneCheckBox:
//                if(!isChecked) return;
                getPermissionWithRationale(Manifest.permission.READ_PHONE_STATE, getString(R.string.telephone_rationale)); return;
            case R.id.usageStatsCheckBox:
//                if(!isChecked) return;
                getPermissionWithRationale(Settings.ACTION_USAGE_ACCESS_SETTINGS, getString(R.string.access_usage_rationale));

        }
    }

    public boolean isContactPermitted(){
        return (ActivityCompat.checkSelfPermission(PermissionsManager.this, Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) ? true : false;
    }

    public boolean isLocationPermitted(){
        return (ActivityCompat.checkSelfPermission(PermissionsManager.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) ? true : false;
    }

    public boolean isSmsPermitted(){
        return (ActivityCompat.checkSelfPermission(PermissionsManager.this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) ? true : false;
    }

    public boolean isStoragePermitted(){
        return (ActivityCompat.checkSelfPermission(PermissionsManager.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) ? true : false;
    }

    public boolean isTelephonePermitted(){
        return (ActivityCompat.checkSelfPermission(PermissionsManager.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) ? true : false;
    }

    public boolean isUsageStatsPermitted(){
        return (ActivityCompat.checkSelfPermission(PermissionsManager.this, Settings.ACTION_USAGE_ACCESS_SETTINGS) == PackageManager.PERMISSION_GRANTED) ? true : false;
    }

//    public boolean isPermitted(String permissionString){
//        switch
//    }
}