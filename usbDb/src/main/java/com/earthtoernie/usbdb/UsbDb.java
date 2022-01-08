package com.earthtoernie.usbdb;


import org.apache.commons.lang3.tuple.ImmutablePair;

import java.sql.*;
import java.util.regex.Pattern;

public class UsbDb {

    private static final Pattern VID_LINE = Pattern.compile("^([0-9a-fA-F]{4})\\s+(.*)");
    private static final Pattern PID_LINE = Pattern.compile("^\\t([0-9a-fA-F]{4})\\s+(.*)");

    // private String JDBC_URL = "jdbc:sqlite:usbids.db";
    // https://stackoverflow.com/questions/52092038/java-sqlite-unable-to-access-db-file-inside-executable-jar
    private final String JDBC_URL = "jdbc:sqlite::resource:usbids.db";

    public UsbDb() { }

    public static void main(String args[]) { }

    public static String getHexString(int i) {
        String s = Integer.toHexString(i);
        return ("0000" + s).substring(s.length());
    }

    public String getVendor(int vid){
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(JDBC_URL);
            c.setAutoCommit(false);
            PreparedStatement selectVendorPreparedStatement = c.prepareStatement("SELECT * FROM VID_TABLE WHERE id=?");
            selectVendorPreparedStatement.setInt(1, vid);
            ResultSet resultSet = selectVendorPreparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getString("VENDOR");
        }catch (SQLException | ClassNotFoundException e) {
            if (e.getMessage().equals("ResultSet closed")) {
                return null;
            }
            e.printStackTrace();
        }
        return null;
    }

    public ImmutablePair<String, String> getVendorAndDevice(int vid, int pid) {
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(JDBC_URL);
            c.setAutoCommit(false);
            PreparedStatement selectVendorPreparedStatement = c.prepareStatement("SELECT * FROM VID_TABLE WHERE id=?");
            selectVendorPreparedStatement.setInt(1, vid);
            ResultSet resultSetVendor = selectVendorPreparedStatement.executeQuery();
            resultSetVendor.next();
            String vendor = "** NOT FOUND **";
            String product = "** NOT FOUND **";
            if(!resultSetVendor.isClosed()) {
                vendor = resultSetVendor.getString("VENDOR");
            } else {
                return ImmutablePair.of(vendor, product); // no table for vendor, so just return
            }
            String vidString = getHexString(vid);

            PreparedStatement selectProductPreparedStatement =
                    c.prepareStatement("SELECT * FROM \"$tableName\" WHERE id=?".replace("$tableName", vidString));
            selectProductPreparedStatement.setInt(1, pid);
            ResultSet resultSetProduct = selectProductPreparedStatement.executeQuery();
            resultSetProduct.next();
            if(!resultSetProduct.isClosed()) {
                // this throw if device id not found, so have to check isClosed
                product = resultSetProduct.getString("PRODUCT");
            }

            return ImmutablePair.of(vendor, product);


        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;

    }

    private void printAllVendors(){

        Connection c = null;
        Statement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            c = DriverManager.getConnection(JDBC_URL);
            c.setAutoCommit(false);
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM VID_TABLE;" );

            while ( rs.next() ) {
                int id = rs.getInt("id");
                String vid  = rs.getString("VID");
                String vendor = rs.getString("VENDOR");

                System.out.println( "ID = " + id );
                System.out.println( "VID = " + vid );
                System.out.println( "VENDOR = " + vendor );
                System.out.println();
            }
            rs.close();
            stmt.close();
            c.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.out.println("Opened database successfully");
    }

}