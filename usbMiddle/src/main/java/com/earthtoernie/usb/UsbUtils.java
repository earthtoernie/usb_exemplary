package com.earthtoernie.usb;

import com.earthtoernie.usbdb.UsbDb;

import javax.usb.*;
import java.util.List;

public class UsbUtils {

    private final UsbDb usbDb;


    public UsbUtils() {
        usbDb = new UsbDb();
    }
    public void checkUsb4java(){
        try {
            UsbServices services = UsbHostManager.getUsbServices();
            UsbHub rootHub = services.getRootUsbHub();
            List<UsbDevice> devices = rootHub.getAttachedUsbDevices();
//            List<Pair<String, String>> devicesPretty = devices.stream()
//                            .map(x -> usbDb.getVendorAndDevice(x.toString()))


            System.out.println(devices.toString());

//            logger.info(devices.toString());

        } catch (UsbException e) {
            e.printStackTrace();
            throw new RuntimeException("can not create a UsbHostManager, check that 'Usb4java' loaded in properties");
        }
    }
}
