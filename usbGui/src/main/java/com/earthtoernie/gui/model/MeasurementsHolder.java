package com.earthtoernie.gui.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

public class MeasurementsHolder {

    private ObservableList<XYChart.Series<String, Double>> measurementsObservable;

    // https://stackoverflow.com/questions/41226155/javafx-linechart-classcastexception-integer-to-string

    public MeasurementsHolder(String name) {
        this.measurementsObservable = FXCollections.observableArrayList();

        ObservableList<XYChart.Data<String,Double>> dataPointsObservable = FXCollections.observableArrayList();

        XYChart.Data<Integer,Double> dataPoint = new XYChart.Data<>(1,15.0);
//        dataPointsObservable.add(dataPoint);
        dataPointsObservable.add(new XYChart.Data<String, Double>("1",15.0));
        dataPointsObservable.add(new XYChart.Data<>("2",11.0));
        dataPointsObservable.add(new XYChart.Data<>("3",24.0));
        dataPointsObservable.add(new XYChart.Data<>("4",21.0));
        dataPointsObservable.add(new XYChart.Data<>("5",4.0));
        dataPointsObservable.add(new XYChart.Data<>("6",9.0));
        dataPointsObservable.add(new XYChart.Data<>("7",19.0));
        dataPointsObservable.add(new XYChart.Data<>("8",16.0));
        dataPointsObservable.add(new XYChart.Data<>("9",15.0));
        dataPointsObservable.add(new XYChart.Data<>("10",8.0));



        XYChart.Series seriesOne = new XYChart.Series<String, Double>("dummy data", dataPointsObservable);
        this.measurementsObservable.add(seriesOne);

//        seriesOne.add(new XYChart.Data<>(1,15.0));
//        this.measurementsObservable.add(data);
    }


    public ObservableList<XYChart.Series<String, Double>> getMeasurementsObservable(){
        return measurementsObservable;

    }
}
