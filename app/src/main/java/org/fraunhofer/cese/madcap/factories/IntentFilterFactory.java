package org.fraunhofer.cese.madcap.factories;

import android.content.IntentFilter;

import javax.inject.Inject;

/**
 * Created by MMueller on 9/26/2016.
 */

public class IntentFilterFactory implements MadcapFactory {
    @Override
    public IntentFilter getNew() {
        return new IntentFilter();
    }

    public IntentFilter getNew(String s) {
        return new IntentFilter(s);
    }

    @Inject
    public IntentFilterFactory(){

    }
}
