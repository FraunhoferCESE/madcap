package org.fraunhofer.cese.funf_sensor.backend.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Lucas on 10/5/2015.
 */
public class ProbeSaveResult {

    private List<String> saved;
    private List<String> alreadyExists;

    public static ProbeSaveResult create(Collection<String> saved, Collection<String> alreadyExists) {
        ProbeSaveResult result = new ProbeSaveResult();
        result.setSaved(saved);
        result.setAlreadyExists(alreadyExists);
        return result;
    }

    private ProbeSaveResult() {}

    public List<String> getSaved() {
        return saved;
    }

    public void setSaved(Collection<String> saved) {
        this.saved = new ArrayList<>(saved);
    }

    public List<String> getAlreadyExists() {
        return alreadyExists;
    }

    public void setAlreadyExists(Collection<String> alreadyExists) {
        this.alreadyExists = new ArrayList<>(alreadyExists);
    }

}
