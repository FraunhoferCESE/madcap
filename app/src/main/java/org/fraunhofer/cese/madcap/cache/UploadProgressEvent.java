package org.fraunhofer.cese.madcap.cache;

/**
 * Created by llayman on 5/1/2017.
 */

public class UploadProgressEvent {

    private final int value;

    UploadProgressEvent(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

