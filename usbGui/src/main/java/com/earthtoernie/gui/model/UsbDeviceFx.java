package com.earthtoernie.gui.model;

import javafx.beans.property.SimpleStringProperty;

import javax.usb.UsbDevice;
import javax.usb.UsbHub;

// apt  install hardinfo
// https://askubuntu.com/questions/725871/where-does-lsusb-t-get-its-data-from
// https://opensource.com/article/20/8/usb-id-repository
// /var/lib/usbutils/usb.ids (found with strace)

// http://www.java2s.com/Code/Java/JavaFX/CreateTreeViewfromBean.htm

// Root hub is of type 'RootHub' (concrete)
// GoTemp is of type 'NonHub' (UsbDevice)
// Other Hubs of type 'UsbHub'

// https://github.com/javax-usb/javax-usb-example/blob/master/src/FindUsbDevice.java TODO read this!!

public class UsbDeviceFx {

    private boolean isHub;
    private boolean isRootHub = false;
    private SimpleStringProperty name;

    public UsbDeviceFx(UsbDevice usbDevice){

        isHub = usbDevice.isUsbHub();
        if(isHub){
            isRootHub = ((UsbHub)usbDevice).isRootUsbHub();
            if(isRootHub) {
                name = new SimpleStringProperty("root hub");
            } else {
                name = new SimpleStringProperty("hub");
            }
        } else {
            name = new SimpleStringProperty("device");
        }

    }
}
