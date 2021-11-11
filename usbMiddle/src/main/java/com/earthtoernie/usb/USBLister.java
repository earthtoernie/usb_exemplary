package com.earthtoernie.usb;

import com.earthtoernie.usbdb.UsbDb;

import javax.usb.*;
import java.io.UnsupportedEncodingException;
import java.util.List;
//import org.usb4java.javax.DeviceNotFoundException

// Java I/O: Tips and Techniques for Putting I/O to Work Second Edition
// by Elliotte Rusty Harold
// Page 576

public class USBLister {

    private static final UsbDb usbDb = new UsbDb();

    private USBLister() {
        // static only class
    }

    public static void main(String[] args) throws UsbException, UnsupportedEncodingException {

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

//    Bus 002 Device 001: ID 1d6b:0003
//    Bus 004 Device 001: ID 1d6b:0003
//    Bus 003 Device 001: ID 1d6b:0002
//    Bus 003 Device 004: ID 08f7:0002
//    Bus 001 Device 001: ID 1d6b:0002
//    Bus 001 Device 002: ID 8087:0025
//    Bus 001 Device 003: ID 0409:005a
//    Bus 001 Device 005: ID 2516:0027
//    Bus 001 Device 004: ID 046d:c547

    public static void listDevices(UsbHub hub) throws UsbException, UnsupportedEncodingException {
        List<UsbDevice> devices = hub.getAttachedUsbDevices();
        for (UsbDevice device : devices) {
            boolean isHub = device.isUsbHub();
            short idVendor = device.getUsbDeviceDescriptor().idVendor();
            short idProduct = device.getUsbDeviceDescriptor().idProduct();
            // https://programming.guide/java/convert-unsigned-short-to-int.html
            var description = usbDb.getVendorAndDevice(Short.toUnsignedInt(idVendor), Short.toUnsignedInt(idProduct));
            String isHubString = isHub ? "T " : "F ";
            System.out.println(device + " hub: " + isHubString + description);
            if (isHub) {
                listDevices((UsbHub) device);
            }
        }
    }

}
