package com.earthtoernie.usb.model;

import java.time.LocalDateTime;

public class SensorMeasurement {

    private double measuredValue;
    private int sequence;
    private double time;
    LocalDateTime dateTime;

    public double getMeasuredValue() {
        return measuredValue;
    }

    public int getSequence() {
        return sequence;
    }

    public double getTime() {
        return time;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public SensorMeasurement(double measuredValue, int sequence, double time) {
        this.measuredValue = measuredValue;
        this.sequence = sequence;
        this.time = time;
        this.dateTime = LocalDateTime.now();
    }
}
