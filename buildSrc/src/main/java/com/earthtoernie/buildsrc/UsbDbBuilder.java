package com.earthtoernie.buildsrc;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UsbDbBuilder {

    private static final Pattern VID_LINE = Pattern.compile("^([0-9a-fA-F]{4})\\s+(.*)");
    private static final Pattern PID_LINE = Pattern.compile("^\\t([0-9a-fA-F]{4})\\s+(.*)");

    public int textCountVids(String usbIdsPath) {
        try {
            LineIterator it = FileUtils.lineIterator(new File(usbIdsPath), "UTF-8");
            int vidCount = 0;
            String lastVid = "";
            while (it.hasNext()) {
                String line = it.nextLine();
                Matcher matcherVid = VID_LINE.matcher(line);
                // Matcher matcherPid = PID_LINE.matcher(line);
                if (matcherVid.find()) {
                    String nextVid = matcherVid.group(1);
                    String vendor = matcherVid.group(2);
                    if (lastVid != nextVid) {
                        vidCount++;
                    }
                }
            }
            return vidCount;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void populateDB(String jdbcUrl, String usbIdsPath, int maxPercent, boolean debugPrint) {
//        usbIdsPath = "/var/lib/usbutils/usb.ids";
        Connection c = null;
        int totalVids = this.textCountVids(usbIdsPath);
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(jdbcUrl);
            var statement = c.createStatement();
            statement.execute("PRAGMA SYNCHRONOUS=OFF"); // speeds up database a ton!, see https://blog.devart.com/increasing-sqlite-performance.html
            System.out.println("Opened database successfully");

            LineIterator it = FileUtils.lineIterator(new File(usbIdsPath), "UTF-8");
            int vidCount = 0;

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
                            vidCount ++;
                            double percentDoneDouble = ((double) vidCount / totalVids) * 100.0;
                            long percentDone = Math.round(percentDoneDouble);
                            //System.out.println(jdbcUrl);
                            if (percentDone == maxPercent){
                                break;
                            }
                            if(debugPrint) {
                                System.out.println(line +" * vid: " + vidCount + " / " + totalVids + " * " + percentDone + " %"); // prints vendors
                            }
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
        } catch (SQLException | IOException | ClassNotFoundException e) {
            System.err.println(e);
            // System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }
}
