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

    public static void main(String args[]) { }

    public static String getHexString(int i) {
        String s = Integer.toHexString(i);
        return ("0000" + s).substring(s.length());
    }

    public String getVendor(int vid){
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:test.db");
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
            c = DriverManager.getConnection("jdbc:sqlite:test.db");
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

    public void populateDB() {
        Connection c = null;
        try {
            c = DriverManager.getConnection("jdbc:sqlite:test.db");
            System.out.println("Opened database successfully");

            LineIterator it = FileUtils.lineIterator(new File("/var/lib/usbutils/usb.ids"), "UTF-8");

            Statement dropVidTableStatement = c.createStatement();
            dropVidTableStatement.executeUpdate("DROP TABLE IF EXISTS VID_TABLE");

            Statement createVidTableStatement = c.createStatement();
            String createVidTableString = "CREATE TABLE VID_TABLE " +
                    "(ID INT PRIMARY KEY     NOT NULL," +
                    " VID           TEXT     NOT NULL," +
                    " VENDOR        TEXT     NOT NULL);";
            createVidTableStatement.executeUpdate(createVidTableString);

            String lastVid = "";
            try {
                while (it.hasNext()) {
                    String line = it.nextLine();
                    Matcher matcherVid = VID_LINE.matcher(line);
                    Matcher matcherPid = PID_LINE.matcher(line);
                    if (matcherVid.find()) {
                        String nextVid = matcherVid.group(1);
                        String vendor = matcherVid.group(2);
                        if (lastVid != nextVid) {
                            System.out.println(line); // prints vendors
                            String createVidRowString = "INSERT INTO VID_TABLE (ID,VID,VENDOR) VALUES (?, ?, ? );";
                            PreparedStatement createVidRowPreparedStatement = c.prepareStatement(createVidRowString);
                            createVidRowPreparedStatement.setInt(1, Integer.parseInt(nextVid, 16));
                            createVidRowPreparedStatement.setString(2, nextVid);
                            createVidRowPreparedStatement.setString(3, vendor);
                            createVidRowPreparedStatement.executeUpdate();
                            lastVid = nextVid;
                            PreparedStatement dropVidSpecificTablePreparedStatement =
                                    c.prepareStatement("DROP TABLE IF EXISTS \"$tableName\";".replace("$tableName", lastVid));
                            dropVidSpecificTablePreparedStatement.executeUpdate();
                            Statement createVidSpecificTableStatement = c.createStatement();
                            String createVidSpecificTableString = "CREATE TABLE \"$tableName\" "+
                                    "(ID INT PRIMARY KEY    NOT NULL," +
                                    " PID           TEXT    NOT NULL," +
                                    " PRODUCT       TEXT    NOT NULL)";
                            var foo = createVidSpecificTableString.replace("$tableName", lastVid);
                            createVidSpecificTableStatement.executeUpdate(createVidSpecificTableString.replace("$tableName", lastVid));
                        }

                    } else if (matcherPid.find()) {
                        String pid = matcherPid.group(1);
                        String product = matcherPid.group(2);
                        String createPidRowString =
                                "INSERT INTO \"$tableName\" (ID,PID,PRODUCT) VALUES (?, ?, ? );".replace("$tableName", lastVid);
                        PreparedStatement createPidRowPreparedStatement = c.prepareStatement(createPidRowString);
                        createPidRowPreparedStatement.setInt(1, Integer.parseInt(pid, 16));
                        createPidRowPreparedStatement.setString(2, pid);
                        createPidRowPreparedStatement.setString(3, product);
                        createPidRowPreparedStatement.executeUpdate();

                    }
                }
            } catch(NoSuchElementException ex) {
                ex.printStackTrace();
            }finally {
                LineIterator.closeQuietly(it);
            }
        } catch (SQLException | IOException e) {
            System.err.println(e);
            // System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
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
            c = DriverManager.getConnection("jdbc:sqlite:test.db");
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