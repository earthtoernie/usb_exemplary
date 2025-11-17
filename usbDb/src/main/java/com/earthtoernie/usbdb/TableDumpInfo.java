package com.earthtoernie.usbdb;

public class TableDumpInfo {
    public final String name;
    public final String content;
    public final int rowCount;
    public final String vendorName;

    public TableDumpInfo(String name, String content, int rowCount, String vendorName) {
        this.name = name;
        this.content = content;
        this.rowCount = rowCount;
        this.vendorName = vendorName;
    }
}
