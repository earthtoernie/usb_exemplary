package com.earthtoernie.gui.controller;

import com.earthtoernie.gui.UsbManager;
import com.earthtoernie.gui.model.MeasurementsHolder;
import com.earthtoernie.gui.services.GoTempService;
import com.earthtoernie.gui.services.RandomWalkService;
import com.earthtoernie.gui.view.ViewFactory;
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
import java.io.StringWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
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
                // Read the sqlite database bundled on the classpath and display its entire contents
                String resourceName = "usbids.db"; // resource path on the classpath root

                // Use the SQLite classpath resource URL so the packaged DB (in usbDb's resources) is used
                String jdbcUrl = "jdbc:sqlite::resource:" + resourceName;
                try {
                    // Ensure SQLite driver is available at runtime
                    Class.forName("org.sqlite.JDBC");
                } catch (ClassNotFoundException e) {
                    Alert err = new Alert(AlertType.ERROR, "SQLite JDBC driver not found: " + e.getMessage());
                    err.showAndWait();
                    return;
                }

                // Use helper to load database dumps
                List<TableDump> dumps;
                try {
                    dumps = loadDatabaseDumps(jdbcUrl);
                } catch (SQLException e) {
                    Alert err = new Alert(AlertType.ERROR, "Error reading database: " + e.getMessage());
                    err.showAndWait();
                    return;
                }

                if (dumps.isEmpty()) {
                    Alert info = new Alert(AlertType.INFORMATION, "No tables found in resource: " + resourceName);
                    info.showAndWait();
                    return;
                }

                // Build UI via helper and show it
                TabPane tabPane = buildDatabaseTabPane(dumps);

                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("USB Database: " + resourceName);
                stage.setScene(new Scene(tabPane));
                stage.setWidth(1100);
                stage.setHeight(800);
                stage.showAndWait();
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

    // Helper that reads the DB and constructs a list of table dumps with row counts and optional vendor names
    List<TableDump> loadDatabaseDumps(String jdbcUrl) throws SQLException {
        List<TableDump> dumps = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(jdbcUrl)) {
            try (Statement tablesStmt = conn.createStatement();
                 ResultSet tables = tablesStmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' ORDER BY name;")) {
                while (tables.next()) {
                    String table = tables.getString("name");

                    if (table.startsWith("sqlite_")) {
                        continue; // skip internal tables
                    }

                    StringBuilder sb = new StringBuilder();
                    sb.append("Table: ").append(table).append('\n');

                    int rowCount = 0;
                    try {
                        // Get column names
                        List<String> cols = new ArrayList<>();
                        try (Statement colsStmt = conn.createStatement();
                             ResultSet colsRs = colsStmt.executeQuery("PRAGMA table_info('" + table + "');")) {
                            while (colsRs.next()) {
                                cols.add(colsRs.getString("name"));
                            }
                        }

                        // CSV header row
                        for (int i = 0; i < cols.size(); i++) {
                            if (i > 0) sb.append(',');
                            sb.append(csvEscape(cols.get(i)));
                        }
                        sb.append('\n');

                        // Rows
                        try (Statement rowsStmt = conn.createStatement();
                             ResultSet rows = rowsStmt.executeQuery("SELECT * FROM \"" + table + "\";")) {
                            while (rows.next()) {
                                for (int i = 0; i < cols.size(); i++) {
                                    if (i > 0) sb.append(',');
                                    String val = rows.getString(cols.get(i));
                                    sb.append(csvEscape(val));
                                }
                                sb.append('\n');
                                rowCount++;
                            }
                        }

                        // Append row count summary
                        sb.append("# rows: ").append(rowCount).append('\n');
                    } catch (Exception tableEx) {
                        sb.append("Error processing table ").append(table).append(": ").append(tableEx.getMessage()).append('\n');
                        StringWriter sw = new StringWriter();
                        tableEx.printStackTrace(new PrintWriter(sw));
                        sb.append(sw).append('\n');
                    }

                    // Lookup vendor name for this table (if it's a vendor table)
                    String vendorName = "";
                    try {
                        if (!"VID_TABLE".equalsIgnoreCase(table)) {
                            try (PreparedStatement vendorStmt = conn.prepareStatement("SELECT VENDOR FROM VID_TABLE WHERE VID=?")) {
                                vendorStmt.setString(1, table);
                                try (ResultSet vr = vendorStmt.executeQuery()) {
                                    if (vr.next()) {
                                        vendorName = vr.getString("VENDOR");
                                    }
                                }
                            }
                            if (vendorName.isEmpty()) {
                                try (PreparedStatement vendorStmt2 = conn.prepareStatement("SELECT VENDOR FROM VID_TABLE WHERE id=?")) {
                                    vendorStmt2.setInt(1, Integer.parseInt(table, 16));
                                    try (ResultSet vr2 = vendorStmt2.executeQuery()) {
                                        if (vr2.next()) {
                                            vendorName = vr2.getString("VENDOR");
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception ignore) {
                        // leave vendorName empty if lookup fails
                    }

                    dumps.add(new TableDump(table, sb.toString(), rowCount, vendorName));
                }
            }
        }
        return dumps;
    }

    // Helper that builds the TabPane UI from table dumps
    TabPane buildDatabaseTabPane(List<TableDump> dumps) {
        TabPane tabPane = new TabPane();
        for (TableDump td : dumps) {
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
    static String buildTabTitle(TableDump td) {
        return buildTabTitle(td.name, td.rowCount, td.vendorName);
    }

    // Overload for tests and general formatting without needing TableDump
    static String buildTabTitle(String name, int rowCount, String vendorName) {
        String vn = (vendorName != null && !vendorName.isEmpty()) ? " - " + vendorName : "";
        return name + " (" + rowCount + " items" + vn + ")";
    }

    private static class TableDump {
        final String name;
        final String content;
        final int rowCount;
        final String vendorName;

        TableDump(String name, String content, int rowCount, String vendorName) {
            this.name = name;
            this.content = content;
            this.rowCount = rowCount;
            this.vendorName = vendorName;
        }
    }

    // CSV escape helper: double quotes inside fields are doubled, and fields containing commas, quotes, or newlines are quoted
    private static String csvEscape(String s) {
        if (s == null) return "";
        boolean needQuote = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        String escaped = s.replace("\"", "\"\"");
        return needQuote ? ("\"" + escaped + "\"") : escaped;
    }
}
