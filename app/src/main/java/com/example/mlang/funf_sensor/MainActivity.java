package com.example.mlang.funf_sensor;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ToggleButton;

import edu.mit.media.funf.FunfManager;
import edu.mit.media.funf.pipeline.BasicPipeline;
import edu.mit.media.funf.probe.Probe.DataListener;

/**
 * Created by MLang on 19.12.2014.
 */
public class MainActivity extends Activity implements DataListener{
    //all the event listeners have to be defined here

    public static final String PIPELINE_NAME = "default";
    private FunfManager funfManager;
    private BasicPipeline pipeline;

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
            funfManager.enablePipeline(PIPELINE_NAME);
            pipeline = (BasicPipeline) funfManager.getRegisteredPipeline(PIPELINE_NAME);
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