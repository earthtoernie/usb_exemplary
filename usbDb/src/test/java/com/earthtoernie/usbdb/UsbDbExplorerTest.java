package com.earthtoernie.usbdb;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UsbDbExplorerTest {

    @Test
    void loadDatabaseDumps_containsVidTable_andVendorLookup() throws Exception {
        UsbDbExplorer explorer = new UsbDbExplorer();
        List<TableDumpInfo> dumps = explorer.loadDatabaseDumps();
        assertNotNull(dumps, "dumps should not be null");
        // find VID_TABLE
        TableDumpInfo vidTable = dumps.stream()
                .filter(td -> "VID_TABLE".equals(td.name))
                .findFirst()
                .orElse(null);
        assertNotNull(vidTable, "VID_TABLE should be present in the database dumps");
        // VID_TABLE content should contain columns VID and VENDOR
        assertTrue(vidTable.content.contains("VID"), "VID_TABLE content should contain column VID");
        assertTrue(vidTable.content.contains("VENDOR"), "VID_TABLE content should contain column VENDOR");

        // ensure at least one vendor table resolves to a non-empty vendorName
        boolean hasVendorName = dumps.stream()
                .anyMatch(td -> td.name.length() == 4 && td.vendorName != null && !td.vendorName.isEmpty());
        assertTrue(hasVendorName, "At least one vendor table should have a resolved vendor name");
    }
}

