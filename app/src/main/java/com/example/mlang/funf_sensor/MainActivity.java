package com.example.mlang.funf_sensor;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * Created by MLang on 19.12.2014.
 */
public class MainActivity extends Activity{
    //all the event listeners have to be defined here

    //onCreate is the rendering of the main page
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    //defines the behavior when the toggle button is clicked
    public void onToggleClicked(View view) {
        // Is the toggle on?
        boolean on = ((ToggleButton) view).isChecked();

        if (on) {
            // collect data
        } else {
            // don't collect data
        }
    }

    public void onSaveToSDClicked(View view) {
        //save data to sd card
    }

    public void onSaveToRemoteClicked(View view) {
        //save data to sd card
    }
}