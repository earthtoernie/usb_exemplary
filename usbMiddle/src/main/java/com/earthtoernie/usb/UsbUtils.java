package com.earthtoernie.usb;

import com.earthtoernie.usbdb.UsbDb;

import javax.usb.*;
import java.util.List;
import java.util.stream.Collectors;

public class UsbUtils {

    private final UsbDb usbDb;

    private final static int VERNIER_VENDOR_ID = 0x08F7;
    private final static int GOTEMP_PRODUCT_ID = 2;


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

    public static List<String> getUsbDevicesAsStringList(){
        try {
            UsbServices services = UsbHostManager.getUsbServices();
            UsbHub rootHub = services.getRootUsbHub();
            List devices = rootHub.getAttachedUsbDevices();
            UsbHub h = (UsbHub) devices.get(0);
            return (List<String>) devices.stream().map(it -> it.toString()).collect(Collectors.toList());

        } catch (UsbException e) {
            e.printStackTrace();
            throw new RuntimeException("finding of usb devices not working!");
        }
    }

    protected static List<UsbDevice> getHubs(UsbHub rootHub){

        List<UsbDevice> hubs = rootHub.getAttachedUsbDevices();
//        if(hubs.size() == 0){
//            throw new RuntimeException("zero hubs found!");
//        }
        return hubs;

    }

    private static UsbDevice searchDevices(UsbHub hub){
        List<UsbDevice> devices = getHubs(hub);
        for (UsbDevice device : devices){
            UsbDeviceDescriptor descriptor = device.getUsbDeviceDescriptor();
            int manufactureCode = descriptor.idVendor();
            int productCode = descriptor.idProduct();
            if (manufactureCode == VERNIER_VENDOR_ID && productCode == GOTEMP_PRODUCT_ID){
                return device;
            } else if (device.isUsbHub()){
                UsbDevice found = searchDevices((UsbHub) device);
                if (found != null){
                    return found;
                }
            }
        }
        return null;
    }

    protected static UsbHub getRootHub(){
        try {
            UsbServices services = UsbHostManager.getUsbServices();
            UsbHub rootHub = services.getRootUsbHub();
            return rootHub;

        } catch (UsbException e) {
            e.printStackTrace();
            throw new RuntimeException("can not connect to USB to find hubs");
        }
    }

    protected static UsbDevice getGoTemp(){
        UsbHub rootHub = getRootHub();
        return searchDevices(rootHub);
    }
}
