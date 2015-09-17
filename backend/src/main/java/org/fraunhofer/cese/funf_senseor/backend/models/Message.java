package org.fraunhofer.cese.funf_senseor.backend.models;

import java.util.List;

/**
 * Message is the object that is sent from the device to the backend,
 * containing multiple SensorDataSets.
 *
 * @param ListOfSensorData      can contain multiple SensorDataSets to be passed over to the backend
 */

public class Message {

    //attributes
    private List<SensorDataSet> ListOfSensorData;

    //getters and setters
    public List<SensorDataSet> getListOfSensorData() {
        return ListOfSensorData;
    }

    public void setListOfSensorData(List<SensorDataSet> listOfSensorData) {
        ListOfSensorData = listOfSensorData;
    }

    //Object methods
    @Override
    public boolean equals(Object obj){

        if(this==obj)
            return true;
        if(!(obj instanceof Message))
            return false;

        Message object=(Message)obj;

        return this.ListOfSensorData.equals(object.getListOfSensorData());
    }

    @Override
    public int hashCode(){
        return this.ListOfSensorData.hashCode();
    }

    @Override
    public String toString(){
        return this.ListOfSensorData.toString();
    }

    //Constructor(s)
    public Message(List<SensorDataSet> ListOfSensorData){
        this.ListOfSensorData=ListOfSensorData;
    }
}
