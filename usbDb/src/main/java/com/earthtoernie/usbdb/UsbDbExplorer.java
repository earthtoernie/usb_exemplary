package com.earthtoernie.usbdb;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsbDbExplorer {
    private static final String JDBC_URL = "jdbc:sqlite::resource:usbids.db";

    public UsbDbExplorer() {}

    public List<TableDumpInfo> loadDatabaseDumps() throws SQLException {
        ensureDriver();
        return loadDatabaseDumps(JDBC_URL);
    }

    public List<TableDumpInfo> loadDatabaseDumps(String jdbcUrl) throws SQLException {
        List<TableDumpInfo> dumps = new ArrayList<>();
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
                    String vendorName = lookupVendorName(conn, table);

                    dumps.add(new TableDumpInfo(table, sb.toString(), rowCount, vendorName));
                }
            }
        }
        return dumps;
    }

    private static String lookupVendorName(Connection conn, String table) {
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
                if (vendorName.isEmpty() && isHex4(table)) {
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
        return vendorName;
    }

    private static boolean isHex4(String s) {
        if (s == null || s.length() != 4) return false;
        for (int i = 0; i < 4; i++) {
            char c = s.charAt(i);
            boolean hex = (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
            if (!hex) return false;
        }
        return true;
    }

    private static String csvEscape(String s) {
        if (s == null) return "";
        boolean needQuote = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        String escaped = s.replace("\"", "\"\"");
        return needQuote ? ("\"" + escaped + "\"") : escaped;
    }

    private static void ensureDriver() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC driver not found", e);
        }
    }
}

