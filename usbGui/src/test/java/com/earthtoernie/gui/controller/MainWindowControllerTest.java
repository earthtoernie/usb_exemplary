package com.earthtoernie.gui.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MainWindowControllerTest {

    @Test
    void buildTabTitle_withVendor() {
        String title = MainWindowController.buildTabTitle("046d", 42, "Logitech, Inc.");
        assertEquals("046d (42 items - Logitech, Inc.)", title);
    }

    @Test
    void buildTabTitle_withoutVendor() {
        String title = MainWindowController.buildTabTitle("VID_TABLE", 1, "");
        assertEquals("VID_TABLE (1 items)", title);
    }
}
