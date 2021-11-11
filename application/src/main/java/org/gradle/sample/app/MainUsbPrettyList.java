package org.gradle.sample.app;

import com.earthtoernie.usb.USBDeviceDescriber;
import com.earthtoernie.usb.USBLister;


public class MainUsbPrettyList {

    public static void main(String[] args) throws Exception {

//        USBDeviceDescriber.mainNoExceptions(); TODO fix this one
        USBLister.mainNoExceptions();


    }

    private static void printModuleDebug(Class<?> clazz) { // TODO delete this method
        System.out.println(clazz.getModule().getName() + " - " + clazz.getModule().getDescriptor().version().get());
    }

}
