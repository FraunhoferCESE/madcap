package org.fraunhofer.cese.madcap.factories;

import android.content.Intent;

import javax.inject.Inject;

/**
 * Created by MMueller on 9/26/2016.
 */
public class IntentFactory implements MadcapFactory {
    @Override
    public Intent getNew() {
        return new Intent();
    }

}
