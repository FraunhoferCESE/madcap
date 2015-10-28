package com.example;

/**
 *
 */


import org.fraunhofer.cese.funf_sensor.backend.models.responseDataSetApi.ResponseDataSetApi;
import org.fraunhofer.cese.funf_sensor.backend.models.responseDataSetApi.model.ResponseDataSet;
import org.fraunhofer.cese.funf_sensor.backend.models.responseDataSetApi.model.ProbeEntry;

import java.util.ArrayList;
import java.util.List;

import java.io.IOException;


public class DataPuller {
//
//    public ResponseDataSetApi responseDataSetApi;
//
//    public List<ProbeEntry> pullDataFromAppengine(long fromTimestamp, long toTimestamp) {
//
//        List<ProbeEntry> data = new ArrayList<>();
//
//        boolean pullAgain = true;
//        while (pullAgain) {
//
//            ResponseDataSet responseDataSet = new ResponseDataSet();
//
//            try {
//                System.out.println(responseDataSetApi.toString());
//                responseDataSet = responseDataSetApi.getResponseFromTo(fromTimestamp, toTimestamp).execute();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            data = responseDataSet.getContent();
//
//            if(responseDataSet.getNumberOfEntries()< org.fraunhofer.cese.funf_sensor.backend.models.ResponseDataSet.getCHUNK_SIZE())
//                pullAgain = false;
//            else
//                fromTimestamp = responseDataSet.getTimestampOfLastEntry();
//        }
//
//        return data;
//    }
//
//    public DataPuller(ResponseDataSetApi responseDataSetApi){
//        this.responseDataSetApi = responseDataSetApi;
//    }
}
