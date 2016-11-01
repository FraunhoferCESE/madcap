package edu.umd.fcmd.sensorlisteners.model;

/**
 * Created by ANepaul on 10/28/2016.
 */

public class AccelerometerState extends State {
    private float xAxis;
    private float yAxis;
    private float zAxis;

    public float getxAxis() {
        return xAxis;
    }

    public void setxAxis(float xAxis) {
        this.xAxis = xAxis;
    }

    public float getyAxis() {
        return yAxis;
    }

    public void setyAxis(float yAxis) {
        this.yAxis = yAxis;
    }

    public float getzAxis() {
        return zAxis;
    }

    public void setzAxis(float zAxis) {
        this.zAxis = zAxis;
    }

    @Override
    public String getData() {
        StringBuilder dataBuilder = getBaseData();
        dataBuilder.append(",\n\tXAxis: ").append(xAxis)
                .append("\n\tYAxis: ").append(yAxis)
                .append("\n\tZAxis: ").append(zAxis)
                .append("\n}");
        return dataBuilder.toString();
    }
}
