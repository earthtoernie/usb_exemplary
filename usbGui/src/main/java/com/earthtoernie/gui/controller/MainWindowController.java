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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
                // Read the sqlite database bundled on the classpath and display its entire contents in a dialog
                String resourceName = "usbids.db"; // resource path on the classpath root
                StringBuilder sb = new StringBuilder();

                // Use the SQLite classpath resource URL so the packaged DB (in usbDb's resources) is used
                String jdbcUrl = "jdbc:sqlite::resource:" + resourceName;
                try {
                    // Ensure SQLite driver is available at runtime
                    Class.forName("org.sqlite.JDBC");
                } catch (ClassNotFoundException e) {
                    sb.append("SQLite JDBC driver not found: ").append(e.getMessage()).append('\n');
                }

                try (Connection conn = DriverManager.getConnection(jdbcUrl)) {
                    Statement stmt = conn.createStatement();
                    // Get list of tables
                    ResultSet tables = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' ORDER BY name;");
                    while (tables.next()) {
                        String table = tables.getString("name");
                        sb.append("Table: ").append(table).append('\n');

                        // Get column names
                        List<String> cols = new ArrayList<>();
                        ResultSet colsRs = stmt.executeQuery("PRAGMA table_info('" + table + "');");
                        while (colsRs.next()) {
                            cols.add(colsRs.getString("name"));
                        }
                        colsRs.close();

                        // Header row
                        for (int i = 0; i < cols.size(); i++) {
                            if (i > 0) sb.append(" | ");
                            sb.append(cols.get(i));
                        }
                        sb.append('\n');

                        // Rows
                        ResultSet rows = stmt.executeQuery("SELECT * FROM \"" + table + "\";");
                        while (rows.next()) {
                            for (int i = 0; i < cols.size(); i++) {
                                if (i > 0) sb.append(" | ");
                                String val = rows.getString(cols.get(i));
                                sb.append(val == null ? "NULL" : val);
                            }
                            sb.append('\n');
                        }
                        rows.close();
                        sb.append('\n');
                    }
                    tables.close();
                    stmt.close();
                } catch (SQLException e) {
                    sb.append("Error reading database: ").append(e.getMessage()).append('\n');
                    e.printStackTrace();
                }

                // Show the results in an information dialog with expandable text area
                Alert info = new Alert(AlertType.INFORMATION);
                info.setTitle("USB Database Contents");
                info.setHeaderText("Contents of resource: " + resourceName);
                TextArea ta = new TextArea(sb.toString());
                ta.setEditable(false);
                ta.setWrapText(false);
                ta.setPrefWidth(800);
                ta.setPrefHeight(600);
                info.getDialogPane().setExpandableContent(ta);
                info.getDialogPane().setExpanded(true);
                info.showAndWait();
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
}
