package com.earthtoernie.usb;


import javax.usb.*;
import java.util.List;

public class UsbUtils {
    public void checkUsb4java(){
        try {
            UsbServices services = UsbHostManager.getUsbServices();
            UsbHub rootHub = services.getRootUsbHub();
            List<UsbDevice> devices = rootHub.getAttachedUsbDevices();


            System.out.println(devices.toString());

//            logger.info(devices.toString());

        } catch (UsbException e) {
            e.printStackTrace();
            throw new RuntimeException("can not create a UsbHostManager, check that 'Usb4java' loaded in properties");
        }
    }
}
