package org.fraunhofer.cese.madcap.factories;

import android.content.IntentFilter;

/**
 * Created by MMueller on 9/21/2016.
 */

public class IntentFilterFactoryReal implements IntentFilterFactory {
    @Override
    public IntentFilter create() {
        return new IntentFilter();
    }
}
