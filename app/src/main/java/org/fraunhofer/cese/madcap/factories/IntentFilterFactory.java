package org.fraunhofer.cese.madcap.factories;

import android.content.IntentFilter;

import org.apache.commons.collections4.Factory;


/**
 * Created by MMueller on 9/21/2016.
 */

public interface IntentFilterFactory extends Factory {
    @Override
    IntentFilter create();
}
