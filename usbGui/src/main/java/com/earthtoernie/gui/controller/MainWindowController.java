package com.earthtoernie.gui.controller;

import com.earthtoernie.gui.UsbManager;
import com.earthtoernie.gui.model.MeasurementsHolder;
import com.earthtoernie.gui.services.GoTempService;
import com.earthtoernie.gui.services.RandomWalkService;
import com.earthtoernie.gui.view.ViewFactory;
import com.earthtoernie.usbdb.TableDumpInfo;
import com.earthtoernie.usbdb.UsbDbExplorer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainWindowController extends BaseController implements Initializable {
    public MainWindowController(UsbManager usbManager, ViewFactory viewFactory, String fxmlName) {
        super(usbManager, viewFactory, fxmlName);
    }

    @FXML
    LineChart<String, Double> measurementsChartView;

    @FXML
    void aboutAction(ActionEvent event) {
        String javaVersion = System.getProperty("java.version");
        String javaRuntime = System.getProperty("java.runtime.name");
        String vmVersion = System.getProperty("java.vm.version");
        String vendor = System.getProperty("java.vendor");
        String message = String.format("Java: %s\nRuntime: %s\nVM: %s\nVendor: %s", javaVersion, javaRuntime, vmVersion, vendor);
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Application Information");
        alert.setContentText(message);
        alert.showAndWait();
    }

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

    @FXML
    void databaseAction(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("USB Database");
        alert.setHeaderText(null);
        alert.setContentText("Choose an action for the database:");

        ButtonType printBtn = new ButtonType("print database");
        alert.getButtonTypes().setAll(printBtn, ButtonType.CLOSE);

        var result = alert.showAndWait();
        result.ifPresent(bt -> {
            if (bt == printBtn) {
                try {
                    List<TableDumpInfo> dumps = new UsbDbExplorer().loadDatabaseDumps();
                    if (dumps.isEmpty()) {
                        new Alert(AlertType.INFORMATION, "No tables found in USB database").showAndWait();
                        return;
                    }
                    TabPane tabPane = buildDatabaseTabPane(dumps);
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setTitle("USB Database");
                    stage.setScene(new Scene(tabPane));
                    stage.setWidth(1100);
                    stage.setHeight(800);
                    stage.showAndWait();
                } catch (Exception e) {
                    new Alert(AlertType.ERROR, "Error reading USB database: " + e.getMessage()).showAndWait();
                }
            }
        });
    }

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

    // Helper that builds the TabPane UI from table dumps
    TabPane buildDatabaseTabPane(List<TableDumpInfo> dumps) {
        TabPane tabPane = new TabPane();
        for (TableDumpInfo td : dumps) {
            Tab tab = new Tab();
            tab.setText(buildTabTitle(td));
            TextArea ta = new TextArea(td.content);
            ta.setEditable(false);
            ta.setWrapText(false);
            ta.setPrefWidth(1000);
            ta.setPrefHeight(700);
            tab.setContent(ta);
            tab.setClosable(false);
            tabPane.getTabs().add(tab);
        }
        return tabPane;
    }

    // Helper that builds a tab title string from a dump (easy to unit test)
    static String buildTabTitle(TableDumpInfo td) {
        return buildTabTitle(td.name, td.rowCount, td.vendorName);
    }

    // Overload for tests and general formatting without needing TableDump
    static String buildTabTitle(String name, int rowCount, String vendorName) {
        String vn = (vendorName != null && !vendorName.isEmpty()) ? " - " + vendorName : "";
        return name + " (" + rowCount + " items" + vn + ")";
    }

}
