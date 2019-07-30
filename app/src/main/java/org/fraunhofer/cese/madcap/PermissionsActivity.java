package org.fraunhofer.cese.madcap;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;

import org.fraunhofer.cese.madcap.issuehandling.MadcapPermissionsManager;
import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by PGuruprasad on 18/04/2017.
 * <p>
 * Activity to show a list of permission used by MADCAP
 * and request for missing permissions.
 * <p>
 * Permission getter is closely tied to this activity.
 */

@SuppressWarnings("PackageVisibleField")
public class PermissionsActivity extends ChildActivity {
    private static final int REQUEST_CODE = 996;
//    private boolean ACCESS_PERMIT = false;

    @BindView(R.id.contactsCheckBox) CheckBox contactsCB;
    private static final int CONTACTS_PERMISSION_REQUEST = 900;

    @BindView(R.id.locationCheckBox) CheckBox locationCB;
    private static final int LOCATION_PERMISSION_REQUEST = 901;

    // Temporarily commenting out the following permissions required for SMS and Call log
    // as per the restrictions imposed by GooglePlayStore
    // Dt: 07/30/2019
    //@BindView(R.id.smsCheckBox) CheckBox smsCB;
    private static final int SMS_PERMISSION_REQUEST = 903;

    @BindView(R.id.telephoneCheckBox) CheckBox telephoneCB;
    private static final int TELEPHONE_PERMISSION_REQUEST = 904;

    @BindView(R.id.usageStatsCheckBox) CheckBox usageStatsCB;
    private static final int USAGE_STATS_PERMISSION_REQUEST = 905;

    @Inject MadcapPermissionsManager permissionsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //noinspection CastToConcreteClass
        ((MyApplication) getApplication()).getComponent().inject(this);
        setContentView(R.layout.activity_permissions);
        ButterKnife.bind(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if ((grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            switch (requestCode) {
                case CONTACTS_PERMISSION_REQUEST:
                    EventBus.getDefault().post(MadcapPermissionsManager.PermissionGrantedEvent.CONTACTS);
                    break;
                case LOCATION_PERMISSION_REQUEST:
                    EventBus.getDefault().post(MadcapPermissionsManager.PermissionGrantedEvent.LOCATION);
                    break;
                case SMS_PERMISSION_REQUEST:
                    EventBus.getDefault().post(MadcapPermissionsManager.PermissionGrantedEvent.SMS);
                    break;
                case TELEPHONE_PERMISSION_REQUEST:
                    EventBus.getDefault().post(MadcapPermissionsManager.PermissionGrantedEvent.TELEPHONE);
                    break;
                case USAGE_STATS_PERMISSION_REQUEST:
                    EventBus.getDefault().post(MadcapPermissionsManager.PermissionGrantedEvent.USAGE);
                    break;
            }
            permissionsManager.updatePermissionNotification();
        }
    }


    //@OnClick({R.id.contactsCheckBox, R.id.locationCheckBox, R.id.smsCheckBox, R.id.telephoneCheckBox})
    @OnClick({R.id.contactsCheckBox, R.id.locationCheckBox, R.id.telephoneCheckBox})
    public void onCheckboxClicked(CompoundButton checkbox) {
        switch (checkbox.getId()) {
            case R.id.contactsCheckBox:
                getPermissionWithRationale(contactsCB, Manifest.permission.GET_ACCOUNTS, getString(R.string.contacts_rationale), CONTACTS_PERMISSION_REQUEST);
                return;
            case R.id.locationCheckBox:
                getPermissionWithRationale(locationCB, Manifest.permission.ACCESS_FINE_LOCATION, getString(R.string.location_rationale), LOCATION_PERMISSION_REQUEST);
                return;
                // Temporarily commenting out the following permissions required for SMS and Call log
                // as per the restrictions imposed by GooglePlayStore
                // Dt: 07/30/2019
                /*case R.id.smsCheckBox:
                getPermissionWithRationale(smsCB, Manifest.permission.READ_SMS, getString(R.string.sms_rationale), SMS_PERMISSION_REQUEST);
                return;*/
            case R.id.telephoneCheckBox:
                getPermissionWithRationale(telephoneCB, Manifest.permission.READ_PHONE_STATE, getString(R.string.telephone_rationale), TELEPHONE_PERMISSION_REQUEST);
                return;
        }

    }

    private void getPermissionWithRationale(final Checkable checkbox, final String permit, CharSequence rationale, final int requestCode) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("MADCAP permissions")
                .setMessage(rationale)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(PermissionsActivity.this, new String[]{permit}, requestCode);
                                dialog.cancel();
                            }
                        })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        checkbox.setChecked(false);
                    }
                })
                .setCancelable(true);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    @OnClick(R.id.usageStatsCheckBox)
    public void requestUsageStats(final Checkable checkbox) {
        String rationale = getString(R.string.access_usage_rationale);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setTitle("MADCAP permissions")
                    .setMessage(rationale)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                                    startActivity(intent);
                                    dialog.dismiss();
                                }
                            })
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            checkbox.setChecked(false);
                        }
                    })
                    .setCancelable(true);
            AlertDialog dialog = dialogBuilder.create();
            dialog.show();
        } else {
            //noinspection deprecation
            getPermissionWithRationale(usageStatsCB, Manifest.permission.GET_TASKS, rationale, USAGE_STATS_PERMISSION_REQUEST);
        }
    }

    private static void disableCheckbox(CheckBox checkBox) {
        checkBox.setChecked(true);
        checkBox.setClickable(false);
        checkBox.setEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
            disableCheckbox(contactsCB);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            disableCheckbox(locationCB);
        }

        // Temporarily commenting out the following permissions required for SMS and Call log
        // as per the restrictions imposed by GooglePlayStore
        // Dt: 07/30/2019
        //if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
        //    disableCheckbox(smsCB);
        //}

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            disableCheckbox(telephoneCB);
        }

        //noinspection deprecation
        if (((Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) && permissionsManager.isUsageStatsPermitted())
                || ((Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) && (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_TASKS) == PackageManager.PERMISSION_GRANTED))) {
            disableCheckbox(usageStatsCB);
        }
    }

    @Override
    protected void onSignOut() {
        finish();
    }
}