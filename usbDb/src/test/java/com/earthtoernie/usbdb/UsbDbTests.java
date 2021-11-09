package com.earthtoernie.usbdb;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UsbDbTests {

    @Test
    public void getVendorTest(){
        UsbDb usbDb = new UsbDb();
        String result = usbDb.getVendor(1);
        assertEquals("Fry's Electronics", result);
    }

    @Test
    public void getVendorAndDeviceTest(){
        UsbDb usbDb = new UsbDb();
        Pair<String, String> result = usbDb.getVendorAndDevice(1, 30584); // 0x1, 0x7778
        assertEquals("Fry's Electronics", result.getLeft());
        assertEquals("Counterfeit flash drive [Kingston]", result.getRight());
    }

    @Test
    public void getHexStringTest(){
        String result = UsbDb.getHexString(30584);
        assertEquals("7778", result);
    }

    @AfterAll
    static void tear(){
        System.out.println("@AfterAll executed");
    }
}
