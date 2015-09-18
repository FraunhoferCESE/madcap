package org.fraunhofer.cese.funf_sensor.backend.models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.sun.javafx.beans.IDProperty;

import java.util.List;

/**
 * Message is the object that is sent from the device to the backend,
 * containing multiple SensorDataSets.
 *
 * @param ListOfSensorData      can contain multiple SensorDataSets to be passed over to the backend
 */

@Entity
public class Message {

    //attributes
    @Id
    private Long id;
    private List<SensorDataSet> ListOfSensorData;

    //getters and setters
    public List<SensorDataSet> getListOfSensorData() {
        return ListOfSensorData;
    }

    public void setListOfSensorData(List<SensorDataSet> listOfSensorData) {
        ListOfSensorData = listOfSensorData;
    }

    public Long getId(){return id;}

    //Object methods
    @Override
    public boolean equals(Object obj){

        if(this==obj)
            return true;
        if(!(obj instanceof Message))
            return false;

        Message object=(Message)obj;

        return this.ListOfSensorData.equals(object.getListOfSensorData()) && this.id.equals(object.getId());
    }

    @Override
    public int hashCode(){
        int hashCode=17;
        hashCode=31*hashCode+this.ListOfSensorData.hashCode();
        hashCode=31*hashCode+this.id.hashCode();
        return hashCode;
    }

    @Override
    public String toString(){
        return "Message-No.: "+this.id+" Message-Content: "+this.ListOfSensorData.toString();
    }

    //Constructor(s)
    public Message(List<SensorDataSet> ListOfSensorData){
        this.ListOfSensorData=ListOfSensorData;
    }
}
