package com.earthtoernie.usbdb;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.tuple.ImmutablePair;

public class UsbDb {

    private static final Pattern VID_LINE = Pattern.compile("^([0-9a-fA-F]{4})\\s+(.*)");
    private static final Pattern PID_LINE = Pattern.compile("^\\t([0-9a-fA-F]{4})\\s+(.*)");

    private static final String JDBC_URL = "jdbc:sqlite:test.db";

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

        } catch (SQLException | ClassNotFoundException e) {
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
            String vendor = resultSetVendor.getString("VENDOR");
            String vidString = getHexString(vid);

            PreparedStatement selectProductPreparedStatement =
                    c.prepareStatement("SELECT * FROM \"$tableName\" WHERE id=?".replace("$tableName", vidString));
            selectProductPreparedStatement.setInt(1, pid);
            ResultSet resultSetProduct = selectProductPreparedStatement.executeQuery();
            resultSetProduct.next();
            String product = resultSetProduct.getString("PRODUCT");

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