package org.fraunhofer.cese.madcap.factories;

import android.content.Intent;

import com.google.gson.JsonObject;

/**
 * Created by llayman on 9/13/2016.
 */
public interface JsonObjectFactory {
    JsonObject createJsonObject(Intent intent);
}
