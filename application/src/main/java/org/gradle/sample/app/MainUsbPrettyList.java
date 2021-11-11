package org.gradle.sample.app;

import com.earthtoernie.usb.USBDeviceDescriber;
import com.earthtoernie.usb.USBLister;


public class MainUsbPrettyList {

    public static void main(String[] args) throws Exception {

        USBDeviceDescriber.mainNoExceptions();
        // TODO delete this
//        USBLister.mainNoExceptions();
//        Bus 002 Device 001: ID 1d6b:0003 hub: T (Linux Foundation,3.0 root hub)
//        Bus 004 Device 001: ID 1d6b:0003 hub: T (Linux Foundation,3.0 root hub)
//        Bus 003 Device 001: ID 1d6b:0002 hub: T (Linux Foundation,2.0 root hub)
//        Bus 003 Device 004: ID 08f7:0002 hub: F (Vernier,EasyTemp/Go!Temp)
//        Bus 001 Device 001: ID 1d6b:0002 hub: T (Linux Foundation,2.0 root hub)
//        Bus 001 Device 002: ID 8087:0025 hub: F (Intel Corp.,Wireless-AC 9260 Bluetooth Adapter)
//        Bus 001 Device 003: ID 0409:005a hub: T (NEC Corp.,HighSpeed Hub)
//        Bus 001 Device 005: ID 2516:0027 hub: F (Cooler Master Co., Ltd.,CM Storm Coolermaster Novatouch TKL)
//        Bus 001 Device 004: ID 046d:c547 hub: F (Logitech, Inc.,** NOT FOUND **)


    }

    private static void printModuleDebug(Class<?> clazz) { // TODO delete this method
        System.out.println(clazz.getModule().getName() + " - " + clazz.getModule().getDescriptor().version().get());
    }

}
