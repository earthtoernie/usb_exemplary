package com.earthtoernie.gui.controller;

import com.earthtoernie.gui.UsbManager;
import com.earthtoernie.gui.model.MeasurementsHolder;
import com.earthtoernie.gui.services.GoTempService;
import com.earthtoernie.gui.services.RandomWalkService;
import com.earthtoernie.gui.view.ViewFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;

import java.net.URL;
import java.util.ResourceBundle;

public class MainWindowController extends BaseController implements Initializable {
    public MainWindowController(UsbManager usbManager, ViewFactory viewFactory, String fxmlName) {
        super(usbManager, viewFactory, fxmlName);
    }

    @FXML
    LineChart<String, Double> measurementsChartView;

    @FXML
    void startMeasureButtonAction(ActionEvent event) {
        var newDataSet = GoTempService.makeNewDataSet(measurementsHolder.getMeasurementsObservable());
        goTempService = new GoTempService(newDataSet, 5);
        goTempService.start();
    }

    @FXML
    void stopMeasureButtonAction(ActionEvent event) {
        goTempService.cancel();
        setUpMeasurementsChart();
    }

    @FXML
    void resetMeasureButtonAction(ActionEvent event) { }

    @FXML
    void startRandomButtonAction(ActionEvent event) {
        var newDataSet = RandomWalkService.makeNewDataSet(measurementsHolder.getMeasurementsObservable());
        randomWalkService = new RandomWalkService(newDataSet, 5);
        randomWalkService.start();
    }

    @FXML
    void stopRandomButtonAction(ActionEvent event) {
        randomWalkService.cancel();
        setUpMeasurementsChart();
    }

    @FXML
    void resetRandomButtonAction(ActionEvent event) { }

    RandomWalkService randomWalkService;
    GoTempService goTempService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpMeasurementsChart();
    }

    private MeasurementsHolder measurementsHolder;

    private void setUpMeasurementsChart(){

        MeasurementsHolder measurementsHolder= new MeasurementsHolder("hello chart");
        var allMeasurements = measurementsHolder.getMeasurementsObservable();
        measurementsChartView.setData(allMeasurements);
        this.measurementsHolder = measurementsHolder;
    }
}
