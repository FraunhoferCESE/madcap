package com.example;

import com.google.inject.Inject;

import org.fraunhofer.cese.funf_sensor.backend.models.probeDataSetApi.ProbeDataSetApi;
import org.fraunhofer.cese.funf_sensor.backend.models.responseDataSetApi.ResponseDataSetApi;

public class MyClass {

    final ProbeDataSetApi probeDataSetApi;
    final ResponseDataSetApi responseDataSetApi;

    @Inject
    public MyClass(ProbeDataSetApi probeDataSetApi, ResponseDataSetApi responseDataSetApi){
        this.probeDataSetApi = probeDataSetApi;
        this.responseDataSetApi = responseDataSetApi;
    }

}
