package com.example;

import org.fraunhofer.cese.funf_sensor.backend.models.responseDataSetApi.model.ProbeEntry;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvFileWriter {

    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";
    private static final String FILE_HEADER = "id,timestamp,probeType,sensorData,userID";
    private static final String FILE_NAME = System.getProperty("user.home") + "/sensorData.csv";
    private static FileWriter fileWriter;
    private static BufferedReader fileReader;



    public static void writeCsvFile(List<ProbeEntry> data) {

        try {
            //Initialise Writer and Reader. Create file if it doesn't exist.
            fileWriter = new FileWriter(FILE_NAME, true);
            fileReader = new BufferedReader(new FileReader(FILE_NAME));


            //Add the header to the file if it just has been created
            try{
                if(!(fileReader.readLine().equals(FILE_HEADER))){
                    fileWriter.append(FILE_HEADER);
                    fileWriter.append(NEW_LINE_SEPARATOR);
                }
            }catch(Exception e){
                fileWriter.append(FILE_HEADER);
                fileWriter.append(NEW_LINE_SEPARATOR);
            }


            // put in the ProbeEntries
            for (ProbeEntry probeEntry : data) {
                fileWriter.append(probeEntry.getId());
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(probeEntry.getTimestamp().toString());
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(probeEntry.getProbeType());
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(probeEntry.getSensorData());
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(probeEntry.getUserID());
                fileWriter.append(NEW_LINE_SEPARATOR);
            }

            System.out.println("Entries succesfully written to csv file.");

        } catch (IOException e) {
            System.out.println("Error in CsvFileWriter!");
            e.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
                fileReader.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter!");
            }
        }
    }
}
