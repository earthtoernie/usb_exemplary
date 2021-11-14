package com.earthtoernie.gui.services;

import com.earthtoernie.usb.SensorGoTemp;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.chart.XYChart;

import java.util.concurrent.TimeUnit;

public class GoTempService extends Service {

    private ObservableList<XYChart.Data<String,Double>> measurements;

    private int minValue = 15;
    private int maxValue = 35;
    private int counterValue = 0;
    private int numSamples;
    private SensorGoTemp sensorGoTemp;


    /** adds to the measurementsObservable and returns a reference to a new elementary data set*/
    public static ObservableList<XYChart.Data<String,Double>> makeNewDataSet(ObservableList<XYChart.Series<String, Double>> measurementsObservable){
        ObservableList<XYChart.Data<String,Double>> newDataPointsObservable = FXCollections.observableArrayList();

        XYChart.Series<String, Double> newSeries =  new XYChart.Series<String, Double>("random walk", newDataPointsObservable);
        measurementsObservable.add(newSeries);
        return newDataPointsObservable;
    }

    public GoTempService(ObservableList<XYChart.Data<String,Double>> measurements, int numSamples){
        this.measurements = measurements;
        this.numSamples = numSamples;
        this.sensorGoTemp = new SensorGoTemp();
    }


    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                System.out.println("random walk started for series: " );
                for(int i = 0; i< numSamples; i++){
                    double measurement = measureAndSleep();
                    Platform.runLater(() -> {
                        measurements.add(new XYChart.Data<>(String.valueOf(counterValue) ,measurement));
                    });
                }
                sensorGoTemp.closeSensorGoTemp();
                return null;
            }
        };
    }


    private double measureAndSleep() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(1000);
        counterValue++;
        double measurement = this.sensorGoTemp.getNextMeasurement();
        return measurement;
    }
}
