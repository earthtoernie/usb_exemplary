package com.earthtoernie.usb;

import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import javax.usb.*;
// Java I/O: Tips and Techniques for Putting I/O to Work Second Edition
// by Elliotte Rusty Harold
// Page 576
public class USBDeviceDescriber {
    public static void main(String[] args) throws UsbException, UnsupportedEncodingException {
        UsbServices services = UsbHostManager.getUsbServices();
        UsbHub root = services.getRootUsbHub();
        listDevices(root);
    }

    public static void mainNoExceptions(){
        try {
            UsbServices services = UsbHostManager.getUsbServices();
            UsbHub root = services.getRootUsbHub();
            listDevices(root);
        } catch (UsbException|UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }

    public static void listDevices(UsbHub hub) throws UsbException, UnsupportedEncodingException {
        List<UsbDevice> devices = hub.getAttachedUsbDevices();
        for (UsbDevice device : devices) {
            describe(device);
            if (device.isUsbHub()) {
                listDevices((UsbHub) device);
            }
        }
    }

    public static void describe(UsbDevice device) throws UsbException, UnsupportedEncodingException {
        UsbDeviceDescriptor descriptor = device.getUsbDeviceDescriptor();
        byte manufacturerCode = descriptor.iManufacturer();
        System.out.println("Manufacturer index: " + manufacturerCode);
        System.out.println("Manufacturer string: " + device.getString(manufacturerCode));
        byte productCode = descriptor.iProduct();
        System.out.println("Product index: " + productCode);
        System.out.println("Product string: " + device.getString(productCode));
        byte serialCode = descriptor.iSerialNumber();
        System.out.println("Serial number index: " + serialCode);
        System.out.println("Serial number string: " + device.getString(serialCode));

        System.out.println("Vendor ID: 0x" + Integer.toHexString(descriptor.idVendor()));
        System.out.println("Product ID: 0x" + Integer.toHexString(descriptor.idProduct()));
        System.out.println("Class: " + descriptor.bDeviceClass());
        System.out.println("Subclass: " + descriptor.bDeviceSubClass());
        System.out.println("Protocol: " + descriptor.bDeviceProtocol());

        System.out.println("Device version: " + decodeBCD(descriptor.bcdDevice()));
        System.out.println("USB version: " + decodeBCD(descriptor.bcdUSB()));
        System.out.println("Maximum control packet size: " + descriptor.bMaxPacketSize0());
        System.out.println("Number of configurations:  " + descriptor.bNumConfigurations());

        System.out.println();
    }

    public static String decodeBCD(short bcd) {
        int upper = (0xFF00 & bcd) >> 8;
        int middle = (0xF0 & bcd) >> 4;
        int lower = 0x0F & bcd;
        return upper + "." + middle + "." + lower;
    }
}
