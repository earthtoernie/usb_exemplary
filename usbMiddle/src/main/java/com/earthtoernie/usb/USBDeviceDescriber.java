package com.earthtoernie.usb;

import com.earthtoernie.usbdb.UsbDb;

import javax.usb.*;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
//import org.usb4java.javax.DeviceNotFoundException

// Java I/O: Tips and Techniques for Putting I/O to Work Second Edition
// by Elliotte Rusty Harold
// Page 576

// see https://github.com/usb4java/usb4java-examples
public class USBDeviceDescriber {

    private static final UsbDb usbDb = new UsbDb();

    private USBDeviceDescriber() {
        // static only class
    }

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
//        System.out.println("hello world");
        UsbDeviceDescriptor descriptor = device.getUsbDeviceDescriptor();
        byte manufacturerCode = descriptor.iManufacturer();
        System.out.println("Manufacturer index: " + manufacturerCode);
        System.out.println("vendor string: " + getStringFromDb(device, UsbStringCode.MANUFACTURER_CODE));
        System.out.println("Manufacturer string: " + getStringNative(device, manufacturerCode));/////////// throws NegativeArraySizeException
//        System.out.println("Manufacturer string: " + Arrays.toString(getBytesNative(device, manufacturerCode)));


        byte productCode = descriptor.iProduct();
        System.out.println("Product index: " + productCode);
        System.out.println("Product string: " + getStringFromDb(device, UsbStringCode.PRODUCT_CODE));
        System.out.println("Product string: " + getStringNative(device, productCode));
//        System.out.println("Product string: " + Arrays.toString(getBytesNative(device, productCode)));


        byte serialCode = descriptor.iSerialNumber();
        System.out.println("Serial number index: " + serialCode);
        System.out.println("Serial number string: " + getStringFromDb(device, UsbStringCode.SERIAL_CODE));
        System.out.println("Serial number string: " + getStringNative(device, serialCode));
        System.out.println("Serial number string (bytes): " + Arrays.toString(getBytesNative(device, serialCode)));


        System.out.println("Vendor ID: 0x" + Integer.toHexString(Short.toUnsignedInt(descriptor.idVendor())));
        System.out.println("Product ID: 0x" + Integer.toHexString(Short.toUnsignedInt(descriptor.idProduct())));
        System.out.println("Class: " + descriptor.bDeviceClass()); // todo print this as an int
        System.out.println("Subclass: " + descriptor.bDeviceSubClass());
        System.out.println("Protocol: " + descriptor.bDeviceProtocol());

        System.out.println("Device version: " + decodeBCD(descriptor.bcdDevice()));
        System.out.println("USB version: " + decodeBCD(descriptor.bcdUSB()));
        System.out.println("Maximum control packet size: " + descriptor.bMaxPacketSize0());
        System.out.println("Number of configurations:  " + descriptor.bNumConfigurations());

        System.out.println();
    }

    public static String getStringFromDb(UsbDevice device, UsbStringCode code) {
        String resultString = "";

        if(code == UsbStringCode.MANUFACTURER_CODE) {
            short idVendor = device.getUsbDeviceDescriptor().idVendor();
            resultString = usbDb.getVendor(Short.toUnsignedInt(idVendor));
        }
        if(code == UsbStringCode.PRODUCT_CODE) {
            short idVendor = device.getUsbDeviceDescriptor().idVendor();
            short idProduct = device.getUsbDeviceDescriptor().idProduct();
            resultString = usbDb.getVendorAndDevice(Short.toUnsignedInt(idVendor), Short.toUnsignedInt(idProduct)).right;
        }
        return "# linux-usb.org: # " + resultString;

    }

    public static String getStringNative(UsbDevice device, byte index) {
        String resultString = "";

        try {
            resultString = device.getString(index);
        } catch (UsbPlatformException e) {
            resultString =  "## UsbPlatformException" + e.getMessage().substring(30);
            return resultString;
        }
        catch (NegativeArraySizeException e) {
            resultString = "## NegativeArraySizeException, index must be > 2, index:" + index;
            return resultString;
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (UsbException e) {
            e.printStackTrace();
        }
        return resultString;
    }

    public static byte[] getBytesNative(UsbDevice device, byte index) {
        byte[] resultBytes;

        try {
            var someBytes = device.getUsbStringDescriptor(index).bString();
            resultBytes = someBytes;
            return resultBytes;

        } catch (UsbPlatformException e) {
            return null;
        } catch (UsbException e) {
            e.printStackTrace();
        }  catch (NegativeArraySizeException e) {
            return null;
        }

        return null;
    }

    public static String decodeBCD(short bcd) {
        int upper = (0xFF00 & bcd) >> 8;
        int middle = (0xF0 & bcd) >> 4;
        int lower = 0x0F & bcd;
        return upper + "." + middle + "." + lower;
    }

    private enum UsbStringCode {
        MANUFACTURER_CODE,
        PRODUCT_CODE,
        SERIAL_CODE
    }
}
